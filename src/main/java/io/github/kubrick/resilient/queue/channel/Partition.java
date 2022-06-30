package io.github.kubrick.resilient.queue.channel;

import io.github.kubrick.resilient.queue.metrics.PartitionMetrics;
import io.github.kubrick.resilient.queue.promise.MessagePromise;
import io.github.kubrick.resilient.queue.sub.ISubscriber;
import io.github.kubrick.resilient.queue.sub.SubscribeCallback;
import io.github.kubrick.resilient.queue.topic.ITopic;
import io.github.kubrick.resilient.queue.message.Message;
import io.github.kubrick.resilient.queue.message.MessageKey;
import io.github.kubrick.resilient.queue.metrics.IMetricsExporter;
import io.github.kubrick.resilient.queue.metrics.PartitionMetricsExporter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class Partition implements IPartition {

    private final AtomicReference<ITopic> topicRefence =
            new AtomicReference<>(null);

    private final Map<MessageKey, IChannel> partitions = new HashMap<>();

    private final Map<MessageKey, ISubscriber> subscribers = new ConcurrentHashMap<>();

    private PartitionMetrics partitionMetrics;

    private IMetricsExporter metricsExporter;

    public Partition() {
    }

    public Partition(@NotNull IMetricsExporter exporter) {
        this.partitionMetrics = new PartitionMetrics(this);
        this.metricsExporter = exporter;

        if (this.metricsExporter instanceof PartitionMetricsExporter) {
            ((PartitionMetricsExporter) this.metricsExporter).setPartition(this);
        }

        this.metricsExporter.start();
    }

    @Override
    public boolean send(MessagePromise promise) throws Exception {
        Message message = promise.getMessage();
        MessageKey key = message.asMessageKey();

        IChannel channel = getChannel(key);
        boolean flag = channel.offer(promise);

        if (flag && partitionMetrics != null) {
            partitionMetrics.messageIn(key);
        }
        return flag;
    }

    private IChannel getChannel(MessageKey key) {
        IChannel channel = partitions.get(key);
        if (channel == null) {
            channel = ChannelFactory.newChannel(key.capacity());
            IChannel previous = partitions.putIfAbsent(key, channel);

            if (previous != null) {
                return previous;
            }
        }
        return channel;
    }

    @Override
    public boolean tryRegisterSubscriber(MessageKey messageKey,
                                         ISubscriber subscriber) {
        IChannel channel = getChannel(messageKey);

        if (channel != null) {
            synchronized (channel) {
                ISubscriber previous = subscribers.putIfAbsent(messageKey, subscriber);
                if (previous == null) {
                    channelBindSubscriber(channel, subscriber);
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    @Override
    public void forceRegisterSubscriber(MessageKey messageKey, ISubscriber subscriber) {
        IChannel channel = partitions.get(messageKey);
        subscribers.put(messageKey, subscriber);
        channelBindSubscriber(channel, subscriber);
    }

    private void channelBindSubscriber(IChannel channel, ISubscriber subscriber) {
        if (partitionMetrics == null) {
            subscriber.setSubscribeCallback(
                    SubscribeCallback.AnonymousSubscribeCallback.INSTANCE);
        } else {
            MetricsCallback callback = new MetricsCallback(partitionMetrics);
            subscriber.setSubscribeCallback(callback);
        }

        channel.bindSubscriber(subscriber);
    }

    @Override
    public void setTopic(ITopic topic) {
        if (topicRefence.compareAndSet(null, topic)) {
            return;
        }

        throw new IllegalStateException();
    }

    @Override
    public ITopic getTopic() {
        return topicRefence.get();
    }

    @Override
    public PartitionMetrics getPartitionMetrics() {
        return this.partitionMetrics;
    }

    static class MetricsCallback extends SubscribeCallback {
        private final PartitionMetrics partitionMetrics;

        public MetricsCallback(PartitionMetrics partitionMetrics) {
            this.partitionMetrics = partitionMetrics;
        }

        @Override
        public void callbackPass(MessagePromise promise) {
            MessageKey messageKey = promise.getMessage().asMessageKey();
            partitionMetrics.consumerPass(messageKey);
            partitionMetrics.messageOut(messageKey);
        }

        @Override
        public void callbackError(MessagePromise promise, Throwable t) {
            MessageKey messageKey = promise.getMessage().asMessageKey();
            partitionMetrics.consumerError(messageKey);
            partitionMetrics.messageOut(messageKey);
        }
    }
}
