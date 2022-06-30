package io.github.kubrick.resilient.queue.listener;

import java.util.EventListener;
import java.util.concurrent.Future;

public interface GenericFutureListener<F extends Future<?>> extends EventListener {

    /**
     * Invoked when the operation associated with the {@link Future} has been completed.
     *
     * @param future the source {@link Future} which called this callback
     */
    void operationComplete(F future) throws Exception;
}
