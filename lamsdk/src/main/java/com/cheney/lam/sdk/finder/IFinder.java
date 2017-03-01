package com.cheney.lam.sdk.finder;

import android.os.Bundle;

import com.cheney.lam.sdk.IAction;

/**
 * Created by cheney on 17/3/1.
 */

public interface IFinder {

    Object findFragment(String path, Bundle params);

    IAction findAction(String api);

//    View findView(Context context, String path, Bundle params);
}