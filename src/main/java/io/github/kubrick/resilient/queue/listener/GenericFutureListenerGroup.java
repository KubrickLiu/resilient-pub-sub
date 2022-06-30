package io.github.kubrick.resilient.queue.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

public class GenericFutureListenerGroup {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericFutureListenerGroup.class);

    protected Set<GenericFutureListener> listeners = new HashSet<>();

    protected final Executor executor;

    public GenericFutureListenerGroup(Executor executor) {
        this.executor = executor;
    }

    public void addListeners(GenericFutureListener<? extends Future>... listeners) {
        synchronized (this) {
            for (GenericFutureListener listener : listeners) {
                this.listeners.add(listener);
            }
        }
    }

    public void removeListeners(GenericFutureListener<? extends Future>... listeners) {
        synchronized (this) {
            for (GenericFutureListener listener : listeners) {
                this.listeners.remove(listener);
            }
        }
    }

    public void notifyListeners(Future<?> future) {
        executor.execute(() -> doNotifyListeners(future));
    }

    private void doNotifyListeners(Future<?> future) {
        Set<GenericFutureListener> currentListeners = new HashSet<>(this.listeners);

        for (GenericFutureListener listener : currentListeners) {
            try {
                listener.operationComplete(future);
            } catch (Throwable t) {
                LOGGER.warn("An exception was thrown by "
                        + listener.getClass().getName() + ".operationComplete()", t);
            }
        }
    }
}
