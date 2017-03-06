package com.cheney.lam.sdk.request;

import android.support.v4.util.Pools;

import com.cheney.lam.sdk.ModuleLogger;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by cheney on 17/3/2.
 */

public abstract class AbsRequest<R extends AbsRequest> {
    private static AtomicInteger increase = new AtomicInteger(0);

    protected int reqId;
    protected String module;
    protected String path;

    public abstract static class ACQUIRE<T extends AbsRequest> {

        private Pools.SynchronizedPool<T> pool = new Pools.SynchronizedPool<>(15);

        public T acquire() {
            T request = pool.acquire();
            if (request == null) {
                request = this.create();
            }
            request.reqId = increase.incrementAndGet();
            ModuleLogger.i("create request id=" + request.reqId);
            return request;
        }

        public void release(T request) {
            pool.release(request);
        }

        public abstract T create();
    }

    public void release() {
        ModuleLogger.i("release request " + this.path + ", id=" + this.reqId);
        reqId = 0;
        module = null;
        path = null;
        myAcquire().release((R) this);
    }

    protected abstract ACQUIRE myAcquire();

    public String getModule() {
        return module;
    }

    public String getPath() {
        return path;
    }

    public int getReqId() {
        return reqId;
    }

    public R module(String module) {
        this.module = module;
        return (R) this;
    }

}
