package io.github.kubrick.resilient.queue.sub;

import io.github.kubrick.resilient.queue.topic.ITopic;
import io.github.kubrick.resilient.queue.topic.Topic;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractSubscriber<VALUE> implements ISubscriber<VALUE> {

    private ITopic topic;

    private SubscribeCallback<VALUE> callback;

    public AbstractSubscriber(@NotNull Object topicArg) {
        if (topicArg instanceof ITopic) {
            this.topic = (ITopic) topicArg;
        } else if (topicArg instanceof String) {
            Topic topic = Topic.find((String) topicArg);
            if (topic == null) {
                throw new NullPointerException("can not create subscriber, topic : "
                        + topicArg + " is null.");
            }
        } else {
            throw new NullPointerException();
        }
    }

    @Override
    public ITopic getTopic() {
        return topic;
    }

    @Override
    public void setSubscribeCallback(SubscribeCallback<VALUE> callback) {
        this.callback = callback;
    }

    @Override
    public SubscribeCallback<VALUE> getSubscribeCallback() {
        return this.callback;
    }
}
