package com.cheney.lam.sdk;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by cheney on 17/3/6.
 */

public class ModuleConfig {

    private IModuleTaskExecutor taskExecutor;

    class DefaultTaskExecutor implements IModuleTaskExecutor {
        Executor executor = Executors.newSingleThreadExecutor();

        @Override
        public void submit(Runnable task) {
            executor.execute(task);
        }
    }

    private ModuleConfig() {
    }

    /**
     * 配置任务执行器
     */
    public IModuleTaskExecutor getTaskExecutor() {
        if (taskExecutor == null) {
            taskExecutor = new DefaultTaskExecutor();
        }
        return taskExecutor;
    }


    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {
        ModuleConfig config;

        public Builder() {
            config = new ModuleConfig();
        }

        public Builder taskExecutor(IModuleTaskExecutor executor) {
            config.taskExecutor = executor;
            return this;
        }

        public Builder logger(ILogger logger) {
            ModuleLogger.logger = logger;
            return this;
        }

        public ModuleConfig build() {
            return config;
        }
    }
}
