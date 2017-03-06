package com.cheney.lam.sdk.request;

import android.support.v4.util.Pools;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cheney on 17/3/1.
 */


public class ActionResponse {

    private Pools.SynchronizedPool<ActionResponse> pool = new Pools.SynchronizedPool<>(15);

    public static ActionResponse acquire() {
        return new ActionResponse();
    }

    public static final int RESPONSE_ASYNC = 1;

    public static final int SUCCESS = 0;

    public static final int ERR_UNKNOWN = -1;

    public static final int ERR_MODULE_NOT_FOUND = -2;

    public static final int ERR_INVALID_REQUEST = -3;

    public static final int ERR_API_NOT_SUPPORT = -4;

    private static final String DEF_KEY = "__MR_DEF";
    private int code = SUCCESS;

    private ActionResponse() {
    }

    private Map<String, Object> resultMap;
    private Object singleResult;

    public boolean isSuccess() {
        return code == SUCCESS;
    }

    public int getCode() {
        return code;
    }

    public Object getResult() {
        return singleResult;
    }

    public Object getResult(String key) {
        if (key != null && resultMap != null) {
            return resultMap.get(key);
        }
        return null;
    }


    public ActionResponse success() {
        this.code = SUCCESS;
        return this;
    }

    public ActionResponse responseAsync() {
        this.code = RESPONSE_ASYNC;
        return this;
    }

    public ActionResponse errNotSupport() {
        this.code = ERR_API_NOT_SUPPORT;
        return this;
    }

    public ActionResponse errNoModule() {
        this.code = ERR_MODULE_NOT_FOUND;
        return this;
    }

    public ActionResponse errInvalid() {
        this.code = ERR_INVALID_REQUEST;
        return this;
    }

    public ActionResponse errUnknown() {
        this.code = ERR_UNKNOWN;
        return this;
    }

    public ActionResponse putResult(String key, Object value) {
        if (key == null || value == null) {
            return this;
        }

        if (resultMap == null) {
            resultMap = new HashMap<>(2);
        }
        resultMap.put(key, value);
        return this;
    }

    public ActionResponse putResult(Object result) {
        singleResult = result;
        return this;
    }
}
