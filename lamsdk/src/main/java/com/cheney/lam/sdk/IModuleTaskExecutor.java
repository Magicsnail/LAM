package com.cheney.lam.sdk;

/**
 * Created by cheney on 17/3/3.
 */

public interface IModuleTaskExecutor {

    void submit(Runnable task);
}
