package io.github.kubrick.resilient.queue.executor;

import java.util.concurrent.Executor;

public enum SyncExecutor implements Executor {
    INSTANCE;

    @Override
    public void execute(Runnable command) {
        command.run();
    }

    @Override
    public String toString() {
        return "SyncExecutor";
    }
}
