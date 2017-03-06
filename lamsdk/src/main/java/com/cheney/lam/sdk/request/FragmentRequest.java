package com.cheney.lam.sdk.request;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Parcelable;

import com.cheney.lam.sdk.IModule;
import com.cheney.lam.sdk.ModuleRouter;

/**
 * Created by cheney on 17/3/2.
 */

public class FragmentRequest extends AbsRequest<FragmentRequest> {

    private Bundle param;

    private static ACQUIRE<FragmentRequest> acquire = new ACQUIRE<FragmentRequest>() {
        @Override
        public FragmentRequest create() {
            return new FragmentRequest();
        }
    };

    public static FragmentRequest acquire() {
        return acquire.acquire();
    }

    @Override
    protected ACQUIRE myAcquire() {
        return acquire;
    }

    public FragmentRequest path(String path) {
        this.path = path;
        return this;
    }

    public FragmentRequest param(String key, String value) {
        if (param == null) {
            param = new Bundle();
        }
        param.putString(key, value);
        return this;
    }

    public FragmentRequest param(String key, long value) {
        if (param == null) {
            param = new Bundle();
        }
        param.putLong(key, value);
        return this;
    }

    public FragmentRequest param(String key, int value) {
        if (param == null) {
            param = new Bundle();
        }
        param.putInt(key, value);
        return this;
    }

    public FragmentRequest param(String key, boolean value) {
        if (param == null) {
            param = new Bundle();
        }
        param.putBoolean(key, value);
        return this;
    }

    public FragmentRequest param(String key, Parcelable value) {
        if (param == null) {
            param = new Bundle();
        }
        param.putParcelable(key, value);
        return this;
    }

    public Fragment invoke() {
        if (reqId <= 0 || module == null || path == null) {
            release();
            return null;
        }

        IModule iModule = ModuleRouter.instance().router(this);
        if (iModule != null) {
            Fragment fragment = iModule.getFragment(path, param);
            release();
            return fragment;
        } else {
            release();
            return null;
        }
    }
}
