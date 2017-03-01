package com.cheney.lam.sdk;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import java.util.Map;

/**
 * Created by cheney on 17/3/1.
 */

public interface IModule<T extends Fragment> {

    void init();

    String getName();

    String getVersionName();

    boolean isReady();

    ModuleResponse invokeApi(int reqId, String api, Map<String, Object> param);

    T getFragment(String path, Bundle params);

    View getView(Context context, String path, Bundle params);
}
