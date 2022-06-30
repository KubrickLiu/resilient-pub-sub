package io.github.kubrick.resilient.queue.pub;

import io.github.kubrick.resilient.queue.topic.ITopic;
import io.github.kubrick.resilient.queue.topic.Topic;
import io.github.kubrick.resilient.queue.message.Message;
import io.github.kubrick.resilient.queue.promise.MessagePromise;
import org.jetbrains.annotations.NotNull;

public class Publisher implements IPublisher {

    private volatile Topic defaultTopic;

    public Publisher() {
    }

    public Publisher(Topic defaultTopic) {
        this.defaultTopic = defaultTopic;
    }

    public Publisher(String topicName) {
        Topic tmp = Topic.find(topicName);
        if (tmp == null) {
            throw new NullPointerException("can not create publisher, topic : " + topicName + " is null.");
        }
        this.defaultTopic = tmp;
    }

    @Override
    public ITopic getDefaultTopic() {
        return defaultTopic;
    }

    public void updateTopic(ITopic iTopic) {
        this.defaultTopic = (Topic) iTopic;
    }

    @Override
    public <CONTENT, VALUE> MessagePromise<VALUE> pub(@NotNull Message<CONTENT> message) {
        if (defaultTopic == null) {
            throw new NullPointerException("publisher default topic is null");
        }

        return doPub(defaultTopic, message);
    }

    @Override
    public <CONTENT, VALUE> MessagePromise<VALUE> pub(@NotNull final String topicName,
                                                      @NotNull Message<CONTENT> message) {
        Topic tmpTopic = Topic.find(topicName);
        if (tmpTopic == null) {
            throw new NullPointerException("publisher topic : " + topicName + " is null");
        }

        return doPub(defaultTopic, message);
    }

    private <CONTENT, VALUE> MessagePromise<VALUE> doPub(@NotNull Topic topic,
                                                         @NotNull Message<CONTENT> message) {
        MessagePromise<VALUE> promise = topic.pub(message);
        return promise;
    }
}
