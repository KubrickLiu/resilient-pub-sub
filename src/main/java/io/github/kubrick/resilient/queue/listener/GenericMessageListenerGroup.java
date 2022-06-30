package io.github.kubrick.resilient.queue.listener;

import io.github.kubrick.resilient.queue.promise.MessagePromise;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;

public class GenericMessageListenerGroup extends GenericFutureListenerGroup {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericMessageListenerGroup.class);

    public GenericMessageListenerGroup(Executor executor) {
        super(executor);
    }

    public void notifyChannelFull(@NotNull MessagePromise promise) {
        executor.execute(() -> {
            Set<GenericFutureListener> currentListeners = new HashSet<>(this.listeners);

            for (GenericFutureListener listener : currentListeners) {
                if (listener instanceof GenericMessageListener) {
                    try {
                        ((GenericMessageListener) listener).operationChannelFull(promise);
                    } catch (Throwable t) {
                        LOGGER.warn("An exception was thrown by "
                                + listener.getClass().getName() + ".notifyChannelFull()", t);
                    }
                }
            }
        });
    }
}
