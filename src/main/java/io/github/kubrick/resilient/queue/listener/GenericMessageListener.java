package io.github.kubrick.resilient.queue.listener;

import io.github.kubrick.resilient.queue.promise.MessagePromise;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Future;

public interface GenericMessageListener<F extends Future<?>>
        extends GenericFutureListener<F> {

    default void operationChannelFull(@NotNull MessagePromise promise) throws Exception {
        // do nothing
    }
}
