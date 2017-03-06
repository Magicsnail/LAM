package com.snail.demo;

import android.app.Application;
import android.util.Log;

import com.cheney.lam.sdk.IModule;
import com.cheney.lam.sdk.ModuleRouter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by cheney on 17/3/1.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ModuleRouter.instance().initialize(this, "modules.json");
    }
}
