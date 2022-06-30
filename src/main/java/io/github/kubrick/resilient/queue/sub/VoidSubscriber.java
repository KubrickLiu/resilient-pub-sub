package io.github.kubrick.resilient.queue.sub;

import io.github.kubrick.resilient.queue.promise.MessagePromise;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class VoidSubscriber extends AbstractSubscriber<Void> {

    private Consumer<MessagePromise> consumer;

    public VoidSubscriber(@NotNull Object topicArg, Consumer<MessagePromise> consumer) {
        super(topicArg);
        this.consumer = consumer;
    }

    @Override
    public void sub(MessagePromise<Void> promise) {
        SubscribeCallback<Void> callback = getSubscribeCallback();

        try {
            doSub(promise);
            promise.setSuccess(null);

            if (callback != null) {
                callback.callbackPass(promise);
            }
        } catch (Throwable t) {
            promise.setFailure(t);

            if (callback != null) {
                callback.callbackError(promise, t);
            }
        }
    }

    public void doSub(MessagePromise promise) {
        consumer.accept(promise);
    }
}