package io.github.kubrick.resilient.queue.sub;

import io.github.kubrick.resilient.queue.topic.ITopic;
import io.github.kubrick.resilient.queue.promise.MessagePromise;

public interface ISubscriber<V> {

    ITopic getTopic();

    void sub(MessagePromise<V> promise);

    void setSubscribeCallback(SubscribeCallback<V> callback);

    SubscribeCallback<V> getSubscribeCallback();
}
