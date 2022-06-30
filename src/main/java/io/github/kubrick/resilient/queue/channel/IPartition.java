package io.github.kubrick.resilient.queue.channel;

import io.github.kubrick.resilient.queue.metrics.PartitionMetrics;
import io.github.kubrick.resilient.queue.promise.MessagePromise;
import io.github.kubrick.resilient.queue.sub.ISubscriber;
import io.github.kubrick.resilient.queue.topic.ITopic;
import io.github.kubrick.resilient.queue.message.MessageKey;

public interface IPartition {

    boolean send(MessagePromise promise) throws Exception;

    boolean tryRegisterSubscriber(MessageKey messageKey, ISubscriber subscriber);

    void forceRegisterSubscriber(MessageKey messageKey, ISubscriber subscriber);

    void setTopic(ITopic topic);

    ITopic getTopic();

    PartitionMetrics getPartitionMetrics();
}
