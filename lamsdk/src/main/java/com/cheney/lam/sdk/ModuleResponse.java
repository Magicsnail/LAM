package com.cheney.lam.sdk;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cheney on 17/3/1.
 */


public class ModuleResponse {
    public static final int SUCCESS = 0;

    public static final int ERR_UNKNOWN = -1;

    public static final int ERR_MODULE_NOT_FOUND = -2;

    public static final int ERR_INVALID_REQUEST = -3;

    public static final int ERR_API_NOT_SUPPORT = -4;

    private static final String DEF_KEY = "__MR_DEF";
    private int code = SUCCESS;

    private Map<String, Object> resultMap;
    private Object singleResult;

    public boolean isSuccess() {
        return code == SUCCESS;
    }

    public int getCode() {
        return code;
    }

    public ModuleResponse setCode(int code) {
        this.code = code;
        return this;
    }

    public ModuleResponse setResult(String key, Object value) {
        if (key == null || value == null) {
            return this;
        }

        if (resultMap == null) {
            resultMap = new HashMap<>(2);
        }
        resultMap.put(key, value);
        return this;
    }

    public ModuleResponse setResult(Object result) {
        singleResult = result;
        return this;
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
}
