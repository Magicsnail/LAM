package com.cheney.lam.sdk.request;

import android.app.Activity;
import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by cheney on 17/3/2.
 */

public class ActivityRequest extends AbsRequest<ActivityRequest> {

    private Bundle param;

    private static ACQUIRE<ActivityRequest> acquire = new ACQUIRE<ActivityRequest>() {
        @Override
        public ActivityRequest create() {
            return new ActivityRequest();
        }
    };


    @Override
    protected ACQUIRE myAcquire() {
        return acquire;
    }

    public static ActivityRequest acquire() {
        return acquire.acquire();
    }

    public ActivityRequest path(String path) {
        this.path = path;
        return this;
    }

    public ActivityRequest param(String key, String value) {
        if (param == null) {
            param = new Bundle();
        }
        param.putString(key, value);
        return this;
    }

    public ActivityRequest param(String key, long value) {
        if (param == null) {
            param = new Bundle();
        }
        param.putLong(key, value);
        return this;
    }

    public ActivityRequest param(String key, int value) {
        if (param == null) {
            param = new Bundle();
        }
        param.putInt(key, value);
        return this;
    }

    public ActivityRequest param(String key, boolean value) {
        if (param == null) {
            param = new Bundle();
        }
        param.putBoolean(key, value);
        return this;
    }

    public ActivityRequest param(String key, Parcelable value) {
        if (param == null) {
            param = new Bundle();
        }
        param.putParcelable(key, value);
        return this;
    }

    @NonNull
    private Intent getActivityIntent(Context context, String module, String path, Bundle params) {
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

    public void start(Context context) {
        Intent intent = getActivityIntent(context, module, path, param);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.e(module, e.getLocalizedMessage());
        }

        // 使用完释放
        release();
    }

    public void startForResult(Activity context, int requestId) {
        Intent intent = getActivityIntent(context, module, path, param);
        try {
            context.startActivityForResult(intent, requestId);
        } catch (ActivityNotFoundException e) {
            Log.e(module, e.getLocalizedMessage());
        }

        // 使用完释放
        release();
    }

}
