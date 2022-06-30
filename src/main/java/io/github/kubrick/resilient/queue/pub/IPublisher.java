package io.github.kubrick.resilient.queue.pub;

import io.github.kubrick.resilient.queue.topic.ITopic;
import io.github.kubrick.resilient.queue.message.Message;
import io.github.kubrick.resilient.queue.promise.MessagePromise;
import org.jetbrains.annotations.NotNull;

public interface IPublisher {

    ITopic getDefaultTopic();

    <CONTENT, VALUE> MessagePromise<VALUE> pub(@NotNull Message<CONTENT> message);

    <CONTENT, VALUE> MessagePromise<VALUE> pub(@NotNull final String topicName,
                                               @NotNull Message<CONTENT> message);
}
