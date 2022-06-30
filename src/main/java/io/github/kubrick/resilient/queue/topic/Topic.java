package io.github.kubrick.resilient.queue.topic;

import io.github.kubrick.resilient.queue.channel.IPartition;
import io.github.kubrick.resilient.queue.message.Message;
import io.github.kubrick.resilient.queue.message.MessageKey;
import io.github.kubrick.resilient.queue.promise.MessagePromise;
import io.github.kubrick.resilient.queue.sequencer.Sequencer;
import io.github.kubrick.resilient.queue.sub.ISubscriber;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicReference;

public class Topic implements ITopic {

    private static final int DEFAULT_MAX_CONCURRENT_NUM = 2 << 8;

    private final String topicName;

    private final Sequencer sequencer;

    private final AtomicReference<IPartition> partitionRefence =
            new AtomicReference<>(null);

    private ISubscriber subscriber;

    public static Topic find(@NotNull final String topicName) {
        return (Topic) TopicRegister.findTopic(topicName);
    }

    public Topic(@NotNull final String topicName) {
        this.topicName = topicName;
        this.sequencer = new Sequencer(DEFAULT_MAX_CONCURRENT_NUM);

        bind();
    }

    public Topic(@NotNull final String topicName,
                 @NotNull final int concurrentNum) {
        this.topicName = topicName;
        this.sequencer = new Sequencer(concurrentNum);

        bind();
    }

    private void bind() {
        if (!TopicRegister.bind(this)) {
            throw new RuntimeException("topic " + topicName + " already exists.");
        }
    }

    @Override
    public final String getTopicName() {
        return topicName;
    }

    @Override
    public boolean bindPartition(@NotNull final IPartition partition) {
        if (!partitionRefence.compareAndSet(null, partition)) {
            return false;
        }

        partition.setTopic(this);
        sequencer.bindTransferPartition(partition);
        return true;
    }

    @Override
    public boolean bindSubscriber(@NotNull MessageKey messageKey, @NotNull ISubscriber subscriber) {
        IPartition partition = partitionRefence.get();
        if (partition == null) {
            return false;
        }

        return partition.tryRegisterSubscriber(messageKey, subscriber);
    }

    @Override
    public IPartition getPartition() {
        return partitionRefence.get();
    }

    public <CONTENT, VALUE> MessagePromise<VALUE> pub(@NotNull Message<CONTENT> message) {
        message.setTopic(this);
        return sequencer.pub(message);
    }
}
