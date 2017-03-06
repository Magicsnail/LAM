package com.cheney.lam.sdk;

import com.cheney.lam.sdk.request.ActionResponse;

import java.util.Map;

/**
 * Created by cheney on 17/3/1.
 */

public interface IAction {

    ActionResponse invoke(int reqId, String api, Map<String, Object> param);
}
