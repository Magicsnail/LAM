package com.cheney.lam.sdk;

import android.util.Log;

/**
 * Created by cheney on 17/3/3.
 */
public class ModuleLogger {

    static ILogger logger;
    static final String TAG = "Module";

    public static void d(String s) {
        if (logger != null) {
            logger.d(TAG, s);
        } else {
            Log.d(TAG, s);
        }
    }

    public static void i(String s) {
        if (logger != null) {
            logger.d(TAG, s);
        } else {
            Log.i(TAG, s);
        }
    }

    public static void w(String s) {
        if (logger != null) {
            logger.d(TAG, s);
        } else {
            Log.w(TAG, s);
        }
    }

    public static void e(String s) {
        if (logger != null) {
            logger.d(TAG, s);
        } else {
            Log.e(TAG, s);
        }
    }
}
