package com.snail.demo;

import android.app.Application;

import com.cheney.lam.sdk.IModule;
import com.cheney.lam.sdk.ModuleRouter;

/**
 * Created by cheney on 17/3/1.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            String xName = "qianniu.modulex.ModuleX";
            String yName = "qianniu.moduley.ModuleY";
            Class<IModule> xClass = (Class<IModule>) Class.forName(xName);
            Class<IModule> yClass = (Class<IModule>) Class.forName(yName);

            IModule xModule = xClass.newInstance();
            IModule yModule = yClass.newInstance();
            ModuleRouter.instance().register(xModule);
            ModuleRouter.instance().register(yModule);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }
}
