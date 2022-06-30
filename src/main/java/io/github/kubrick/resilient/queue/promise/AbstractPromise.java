package io.github.kubrick.resilient.queue.promise;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class AbstractPromise<VALUE> implements Promise<VALUE> {

    @Override
    public VALUE get() throws InterruptedException, ExecutionException {
        await();

        Throwable cause = cause();
        if (cause == null) {
            return getNow();
        }

        throw new ExecutionException(cause);
    }

    @Override
    public VALUE get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        if (await(timeout, unit)) {
            Throwable cause = cause();
            if (cause == null) {
                return getNow();
            }

            throw new ExecutionException(cause);
        }

        throw new TimeoutException();
    }
}
