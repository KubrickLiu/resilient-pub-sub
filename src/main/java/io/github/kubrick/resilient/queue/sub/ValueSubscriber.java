package io.github.kubrick.resilient.queue.sub;

import io.github.kubrick.resilient.queue.promise.MessagePromise;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class ValueSubscriber<VALUE> extends AbstractSubscriber<VALUE> {

    private Function<MessagePromise<VALUE>, VALUE> function;

    public ValueSubscriber(@NotNull Object topicArg,
                           @NotNull Function<MessagePromise<VALUE>, VALUE> function) {
        super(topicArg);
        this.function = function;
    }

    @Override
    public void sub(MessagePromise<VALUE> promise) {
        SubscribeCallback<VALUE> callback = getSubscribeCallback();

        try {
            VALUE value = doSub(promise);
            promise.setSuccess(value);

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

    public VALUE doSub(MessagePromise<VALUE> promise) {
        return function.apply(promise);
    }
}
