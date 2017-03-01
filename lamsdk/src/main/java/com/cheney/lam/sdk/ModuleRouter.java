package com.cheney.lam.sdk;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by cheney on 17/3/1.
 */
public class ModuleRouter<F extends Fragment> {

    private ConcurrentHashMap<String, IModule> moduleMap;

    private ConcurrentHashMap<Integer, ModuleRequest> requestMap;

    private static ModuleRouter router;

    private ModuleRouter() {
        moduleMap = new ConcurrentHashMap<>(10);
        requestMap = new ConcurrentHashMap<>(20);
    }

    public static ModuleRouter instance() {
        if (router == null) {
            synchronized (ModuleRouter.class) {
                if (router == null) {
                    router = new ModuleRouter();
                }
            }
        }
        return router;
    }


    public void register(IModule module) {
        if (module != null) {
            moduleMap.put(module.getName(), module);
        }
    }

    private boolean isModuleReady(String module) {
        IModule m = moduleMap.get(module);
        return m != null && m.isReady();
    }

    IModule getReadiedModule(String module) {
        IModule m = moduleMap.get(module);
        if (m != null && m.isReady()) {
            return m;
        }
        return null;
    }

    private Intent getActivityIntent(Context context, String module, String path, Bundle params) {
        if (!isModuleReady(module)) {
            return null;
        }
        Uri uri = Uri.parse("router://" + module + path);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (params != null && params.size() > 0) {
            intent.putExtras(params);
        }
        if (context instanceof Application) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return intent;
    }

    public boolean startActivity(Context context, String module, String path, Bundle params) {
        Intent intent = getActivityIntent(context, module, path, params);
        if (intent != null) {
            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Log.e(module, e.getLocalizedMessage());
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean startActivityForResult(Activity context, String module, String path, Bundle params, int requestCode) {
        Intent intent = getActivityIntent(context, module, path, params);
        if (intent != null) {
            try {
                context.startActivityForResult(intent, requestCode);
            } catch (ActivityNotFoundException e) {
                Log.e(module, e.getLocalizedMessage());
            }
            return true;
        } else {
            return false;
        }
    }

    public Fragment getFragment(String module, String path, Bundle params) {
        IModule iModule = getReadiedModule(module);
        if (iModule != null) {
            return iModule.getFragment(path, params);
        }
        return null;
    }

    public View getView(Context context, String moudle, String path, Bundle params) {
        IModule iModule = getReadiedModule(moudle);
        if (iModule != null) {
            return iModule.getView(context, path, params);
        }
        return null;
    }

//    public ModuleResponse router(ModuleRequest request) {
//        if (request == null) {
//            return null;
//        }
//
//        IModule module = getReadiedModule(request.getModule());
//        if (module != null) {
//
//        }
//        requestMap.put(request.getReqId(), request);
//
//    }
//
//    public void response() {
//
//    }
}
