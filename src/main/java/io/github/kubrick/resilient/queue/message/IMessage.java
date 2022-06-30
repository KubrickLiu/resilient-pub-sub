package io.github.kubrick.resilient.queue.message;

import io.github.kubrick.resilient.queue.topic.ITopic;
import io.github.kubrick.resilient.queue.promise.MessagePromise;

import java.io.Serializable;
import java.util.concurrent.Executor;

public interface IMessage<CONTENT> extends Serializable {

    void setTopic(ITopic topic);

    ITopic getTopic();

    CONTENT getContent();

    <VALUE> MessagePromise<VALUE> newPromise();

    <VALUE> MessagePromise<VALUE> newPromise(Executor executor);
}
