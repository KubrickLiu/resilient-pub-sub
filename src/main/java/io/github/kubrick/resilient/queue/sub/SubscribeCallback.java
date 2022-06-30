package io.github.kubrick.resilient.queue.sub;

import io.github.kubrick.resilient.queue.promise.MessagePromise;

public abstract class SubscribeCallback<VALUE> {

    public abstract void callbackPass(MessagePromise<VALUE> promise);

    public abstract void callbackError(MessagePromise<VALUE> promise, Throwable t);

    public static class AnonymousSubscribeCallback extends SubscribeCallback {
        public static final AnonymousSubscribeCallback INSTANCE = new AnonymousSubscribeCallback();

        private AnonymousSubscribeCallback() {
        }

        @Override
        public void callbackPass(MessagePromise promise) {

        }

        @Override
        public void callbackError(MessagePromise promise, Throwable t) {

        }
    }
}
