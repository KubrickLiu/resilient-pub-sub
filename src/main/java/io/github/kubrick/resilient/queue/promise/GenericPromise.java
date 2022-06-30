package io.github.kubrick.resilient.queue.promise;

import io.github.kubrick.resilient.queue.executor.SafeExecutor;
import io.github.kubrick.resilient.queue.executor.SyncExecutor;
import io.github.kubrick.resilient.queue.listener.GenericFutureListener;
import io.github.kubrick.resilient.queue.listener.GenericFutureListenerGroup;

import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public abstract class GenericPromise<VALUE> extends AbstractPromise<VALUE> {

    protected final Executor executor;

    private short waiters;

    private volatile VALUE result = null;

    private volatile boolean isACK = false;

    private final AtomicReference<Signal> status = new AtomicReference<>(Signal.UNCANCELLABLE);

    enum Signal {
        SUCCESS, UNCANCELLABLE, FAILURE, CANCEL
    }

    private volatile Throwable cause;

    protected abstract GenericFutureListenerGroup getListenerGroup();

    public GenericPromise() {
        this.executor = new SafeExecutor(SyncExecutor.INSTANCE);
    }

    public GenericPromise(Executor executor) {
        this.executor = new SafeExecutor(executor);
    }

    @Override
    public Promise<VALUE> setSuccess(VALUE result) {
        if (setValue0(Signal.SUCCESS, result)) {
            notifyListeners();
            return this;
        }
        throw new IllegalStateException("complete already: " + this);
    }

    @Override
    public boolean trySuccess(VALUE result) {
        if (setValue0(Signal.SUCCESS, result)) {
            notifyListeners();
            return true;
        }
        return false;
    }

    @Override
    public Promise<VALUE> setFailure(Throwable cause) {
        if (setValue0(Signal.FAILURE, null)) {
            notifyListeners();
            this.cause = cause;
            return this;
        }
        throw new IllegalStateException("complete already: " + this, cause);
    }

    @Override
    public boolean tryFailure(Throwable cause) {
        if (setValue0(Signal.FAILURE, null)) {
            notifyListeners();
            this.cause = cause;
            return true;
        }
        return false;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (setValue0(Signal.CANCEL, null)) {
            notifyListeners();
            return true;
        }
        return false;
    }

    private boolean setValue0(Signal signal, VALUE result) {
        if (status.compareAndSet(Signal.UNCANCELLABLE, signal)) {
            this.result = result;
            notifyWaiters();
            return true;
        }
        return false;
    }

    @Override
    public Promise<VALUE> addListeners(GenericFutureListener<? extends Future<VALUE>>... listeners) {
        getListenerGroup().addListeners(listeners);

        if (isDone()) {
            notifyListeners();
        }

        return this;
    }

    @Override
    public Promise<VALUE> removeListeners(GenericFutureListener<? extends Future<VALUE>>... listeners) {
        getListenerGroup().removeListeners(listeners);
        return this;
    }

    @Override
    public Promise<VALUE> await() throws InterruptedException {
        if (isDone()) {
            return this;
        }

        if (Thread.interrupted()) {
            throw new InterruptedException(toString());
        }

        synchronized (this) {
            while (!isDone()) {
                incWaiters();
                try {
                    wait(0, 100);
                } finally {
                    decWaiters();
                }
            }
        }

        return this;
    }

    @Override
    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        if (isDone()) {
            return true;
        }

        long timeoutNanos = unit.toNanos(timeout);
        if (timeoutNanos <= 0) {
            return isDone();
        }

        if (Thread.interrupted()) {
            throw new InterruptedException(toString());
        }

        long startTime = System.nanoTime();
        long waitTime = timeoutNanos;
        boolean interrupted = false;
        try {
            for (; ; ) {
                synchronized (this) {
                    if (isDone()) {
                        return true;
                    }

                    incWaiters();

                    try {
                        wait(waitTime / 1000000, (int) (waitTime % 1000000));
                    } catch (InterruptedException e) {
                        interrupted = true;
                    } finally {
                        decWaiters();
                    }
                }

                if (isDone()) {
                    return true;
                } else {
                    waitTime = timeoutNanos - (System.nanoTime() - startTime);
                    if (waitTime <= 0) {
                        return isDone();
                    }
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void incWaiters() {
        if (waiters == Short.MAX_VALUE) {
            throw new IllegalStateException("too many waiters: " + this);
        }
        ++waiters;
    }

    private void decWaiters() {
        --waiters;
    }

    @Override
    public Promise<VALUE> sync() throws Exception {
        await();

        if (cause == null) {
            return this;
        }

        throw (RuntimeException) cause;
    }

    @Override
    public boolean isCancelled() {
        return status.get() == Signal.CANCEL;
    }

    @Override
    public boolean isDone() {
        return status.get() != Signal.UNCANCELLABLE;
    }

    @Override
    public boolean isFailure() {
        return status.get() == Signal.FAILURE;
    }

    @Override
    public VALUE getNow() {
        if (isDone()) {
            return result;
        }

        return null;
    }

    @Override
    public void ack() {
        isACK = true;
    }

    @Override
    public boolean isACK() {
        return isACK;
    }

    private void notifyListeners() {
        getListenerGroup().notifyListeners(this);
    }

    private void notifyWaiters() {
        if (waiters > 0) {
            synchronized (this) {
                if (waiters > 0) {
                    notifyAll();
                }
            }
        }
    }

    @Override
    public Throwable cause() {
        return this.cause;
    }
}
