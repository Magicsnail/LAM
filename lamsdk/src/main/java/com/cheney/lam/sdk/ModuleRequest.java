package com.cheney.lam.sdk;

import android.support.v4.util.Pools;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by cheney on 17/3/1.
 */

public class ModuleRequest {

    private static AtomicInteger increase = new AtomicInteger(0);

    private int reqId;
    private String module;
    private String api;
    private Map<String, Object> param;

    private static Pools.SynchronizedPool<ModuleRequest> pool = new Pools.SynchronizedPool<>(20);

    public static ModuleRequest acquire() {
        ModuleRequest request = pool.acquire();
        if (request == null) {
            request = new ModuleRequest();
        }
        request.reqId = increase.incrementAndGet();
        return request;
    }

    public void release() {
        reqId = 0;
        module = null;
        api = null;
        param.clear();
        pool.release(this);
    }

    public int getReqId() {
        return reqId;
    }

    public String getModule() {
        return module;
    }

    public String getApi() {
        return api;
    }

    public ModuleRequest module(String module) {
        this.module = module;
        return this;
    }

    public ModuleRequest api(String api) {
        this.api = api;
        return this;
    }

    public ModuleRequest param(String key, Object value) {
        if (param == null) {
            param = new HashMap<>(4);
        }
        param.put(key, value);
        return this;
    }

    public ModuleResponse invoke() {
        if (reqId <= 0 || module == null || api == null) {
            ModuleResponse result = new ModuleResponse();
            return result.setCode(ModuleResponse.ERR_INVALID_REQUEST);
        }
        IModule xModule = ModuleRouter.instance().getReadiedModule(module);
        if (xModule != null) {
            return xModule.invokeApi(reqId, api, param);
        } else {
            ModuleResponse result = new ModuleResponse();
            return result.setCode(ModuleResponse.ERR_MODULE_NOT_FOUND);
        }
    }

//    public ModuleResponse invoke(IModuleApiCallback callback) {
//        ModuleResponse result = new ModuleResponse();
//        if (reqId <= 0 || module == null || api == null) {
//            return result.setCode(ModuleResponse.ERR_INVALID_REQUEST);
//        }
//        IModule xModule = ModuleRouter.instance().getReadiedModule(module);
//        if (xModule != null) {
//            xModule.invokeAsyncRequest(reqId, api, param);
//        } else {
//            ModuleResponse result = new ModuleResponse();
//            return result.setCode(ModuleResponse.ERR_MODULE_NOT_FOUND);
//        }
//
//        return result.setCode(ModuleResponse.SUCCESS);
//    }
}