package com.cheney.lam.sdk.finder;

import android.app.Fragment;
import android.os.Bundle;

import com.cheney.lam.sdk.IAction;
import com.cheney.lam.sdk.IModule;
import com.cheney.lam.sdk.request.ActionResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cheney on 17/3/1.
 */
public class ModuleInject {

    final static HashMap<IModule, IFinder> finderMap = new HashMap<>(15);

    public static Fragment finder(IModule host, String path, Bundle bundle) {
        IFinder finder = getFinder(host);
        if (finder != null) {
            return (Fragment) finder.findFragment(path, bundle);
        }
        return null;
    }

    public static ActionResponse invokeAction(IModule host, int reqId, String api, Map<String, Object> param) {
        IFinder finder = getFinder(host);
        IAction action = finder.findAction(api);
        if (action != null) {
            return action.invoke(reqId, api, param);
        }
        return ActionResponse.acquire()
                .errNotSupport();
    }

    private static IFinder getFinder(IModule host) {
        synchronized (finderMap) {
            IFinder finder = finderMap.get(host);
            if (finder == null) {
                String className = host.getClass().getName();
                try {
                    Class<?> finderClass = Class.forName(className + "$$Finder");
                    finder = (IFinder) finderClass.newInstance();
                    finderMap.put(host, finder);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            return finder;
        }
    }
}