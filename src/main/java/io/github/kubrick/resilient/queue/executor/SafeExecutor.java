package io.github.kubrick.resilient.queue.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;

public class SafeExecutor implements Executor {

    private static final Logger REJECTED_EXECUTION_LOGGER =
            LoggerFactory.getLogger(SafeExecutor.class.getName() + ".rejectedExecution");

    private Executor realExecutor;

    public SafeExecutor(Executor realExecutor) {
        this.realExecutor = realExecutor;
    }

    @Override
    public void execute(Runnable command) {
        try {
            realExecutor.execute(command);
        } catch (Throwable t) {
            REJECTED_EXECUTION_LOGGER.warn("Failed to submit a task.", t);
        }
    }
}
