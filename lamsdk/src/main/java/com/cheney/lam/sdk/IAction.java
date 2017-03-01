package com.cheney.lam.sdk;

import java.util.Map;

/**
 * Created by cheney on 17/3/1.
 */

public interface IAction {

    ModuleResponse invoke(int reqId, String api, Map<String, Object> param);
}
