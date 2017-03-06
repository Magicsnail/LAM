package com.cheney.lam.sdk.request;

import android.util.Log;

import com.cheney.lam.sdk.IModule;
import com.cheney.lam.sdk.ModuleRouter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by cheney on 17/3/2.
 */
public class ActionRequest extends AbsRequest<ActionRequest> {

    private static ConcurrentHashMap<Integer, ActionRequest> sAsyncRequestCache = new ConcurrentHashMap<>(10);

    private Map<String, Object> args;
    private IActionCallback resultCallback;
    private boolean isAsyncRequest = false;

    private static ACQUIRE<ActionRequest> acquire = new ACQUIRE<ActionRequest>() {

        @Override
        public ActionRequest create() {
            return new ActionRequest();
        }
    };

    @Override
    protected ACQUIRE myAcquire() {
        return acquire;
    }

    public static ActionRequest acquire() {
        return acquire.acquire();
    }

    public ActionRequest action(String action) {
        this.path = action;
        return this;
    }

    @Override
    public void release() {
        args.clear();
        resultCallback = null;
        isAsyncRequest = false;
        super.release();
    }

    public ActionRequest param(String key, Object value) {
        if (args == null) {
            args = new HashMap<>(4);
        }
        args.put(key, value);
        return this;
    }

    public ActionRequest async(IActionCallback callback) {
        this.resultCallback = callback;
        isAsyncRequest = true;
        return this;
    }

    public ActionResponse invoke() {
        if (reqId <= 0 || module == null || path == null) {
            release();
            return ActionResponse.acquire().errInvalid();
        }

        final IModule iModule = ModuleRouter.instance().router(this);
        if (iModule != null) {
            // 如果异步请求，则提交异步任务执行
            if (isAsyncRequest) {
                if (resultCallback != null) {
                    sAsyncRequestCache.put(reqId, this);
                }
                ModuleRouter.instance().getConfig().getTaskExecutor().submit(new RequestTask(this, iModule));
                return ActionResponse.acquire().success();
            } else {
                ActionResponse response = iModule.invokeApi(reqId, path, args);
                release();
                return response;
            }
        } else {
            release();
            return ActionResponse.acquire().errNoModule();
        }
    }

    public static void response(final int reqId, final ActionResponse response) {
        ModuleRouter.instance().getConfig().getTaskExecutor().submit (new Runnable() {
            @Override
            public void run() {
                ActionRequest request = sAsyncRequestCache.remove(reqId);
                if (request != null) {
                    if (request.resultCallback != null) {
                        request.resultCallback.actionResponse(response);
                    }
                    request.release();
                }
            }
        });
    }

    static class RequestTask implements Runnable {
        private int ID;
        private String path;
        private Map<String, Object> arg;
        IModule iModule;

        RequestTask(ActionRequest request, IModule iModule) {
            ID = request.reqId;
            path = request.path;
            arg = request.args;
            this.iModule = iModule;
            Log.i("ActionRequest", "submit request Task " + ID);
        }

        @Override
        public void run() {
            ActionResponse response = iModule.invokeApi(ID, path, arg);
            if (response != null) {
                switch (response.getCode()) {
                    case ActionResponse.RESPONSE_ASYNC:
                        // 说明执行方的接口也是异步返回的
                        return;
                    default:
                        // 说明执行方正常执行完
                        ActionRequest.response(ID, response);
                        break;
                }
            }
        }
    }
}
