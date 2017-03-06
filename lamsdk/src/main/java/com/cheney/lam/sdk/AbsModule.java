package com.cheney.lam.sdk;

import android.app.Fragment;
import android.os.Bundle;

import com.cheney.lam.sdk.finder.ModuleInject;
import com.cheney.lam.sdk.request.ActionResponse;

import java.util.Map;

/**
 * Created by cheney on 17/3/6.
 */

public abstract class AbsModule<T extends Fragment> implements IModule<T> {

    @Override
    public ActionResponse invokeApi(int reqId, String api, Map<String, Object> param) {
        return ModuleInject.invokeAction(this, reqId, api, param);
    }

    @Override
    public T getFragment(String path, Bundle params) {
        return (T) ModuleInject.finder(this, path, params);
    }

}
