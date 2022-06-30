package io.github.kubrick.resilient.queue.promise;

import io.github.kubrick.resilient.queue.listener.GenericFutureListener;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public interface Promise<VALUE> extends Future<VALUE> {

    /**
     * Marks this future as a success and notifies all listeners.
     *
     * @param result
     * @return
     */
    Promise<VALUE> setSuccess(VALUE result);

    /**
     * Marks this future as a success and notifies all listeners.
     *
     * @param result
     * @return
     */
    boolean trySuccess(VALUE result);

    /**
     * Marks this future as a failure and notifies all listeners.
     *
     * @param cause
     * @return
     */
    Promise<VALUE> setFailure(Throwable cause);

    /**
     * Marks this future as a failure and notifies all listeners.
     *
     * @param cause
     * @return
     */
    boolean tryFailure(Throwable cause);

    Promise<VALUE> addListeners(GenericFutureListener<? extends Future<VALUE>>... listeners);

    Promise<VALUE> removeListeners(GenericFutureListener<? extends Future<VALUE>>... listeners);

    Promise<VALUE> await() throws InterruptedException;

    boolean await(long timeout, TimeUnit unit) throws InterruptedException;

    Promise<VALUE> sync() throws Exception;

    VALUE getNow();

    void ack();

    boolean isACK();

    boolean isFailure();

    Throwable cause();
}
