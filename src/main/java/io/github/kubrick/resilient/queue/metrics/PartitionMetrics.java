package io.github.kubrick.resilient.queue.metrics;

import io.github.kubrick.resilient.queue.channel.Partition;
import io.github.kubrick.resilient.queue.executor.SafeExecutor;
import io.github.kubrick.resilient.queue.message.MessageKey;
import io.github.kubrick.resilient.queue.metrics.counter.HealthCounter;
import io.github.kubrick.resilient.queue.metrics.counter.ICounter;
import io.github.kubrick.resilient.queue.metrics.counter.MessageCounter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PartitionMetrics {

    private final Partition partition;

    private final Executor receiveExecutor;

    private final ConcurrentHashMap<MessageKey, ICounter[]> counterMap = new ConcurrentHashMap<>();

    enum CounterEvent {
        HEALTH,
        MESSAGE
    }

    public PartitionMetrics(Partition partition) {
        this.partition = partition;
        this.receiveExecutor = new SafeExecutor(new ThreadPoolExecutor(2, 2,
                1, TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(20000)));
    }

    public void consumerPass(@NotNull final MessageKey messageKey) {
        receiveExecutor.execute(() -> {
            HealthCounter healthCounter = getCounter(messageKey, CounterEvent.HEALTH);
            healthCounter.increment();
        });
    }

    public void consumerError(@NotNull final MessageKey messageKey) {
        receiveExecutor.execute(() -> {
            HealthCounter healthCounter = getCounter(messageKey, CounterEvent.HEALTH);
            healthCounter.incrementError();
        });
    }

    public void messageIn(@NotNull final MessageKey messageKey) {
        receiveExecutor.execute(() -> {
            MessageCounter messageCounter = getCounter(messageKey, CounterEvent.MESSAGE);
            messageCounter.incrementIn();
        });
    }

    public void messageOut(@NotNull final MessageKey messageKey) {
        receiveExecutor.execute(() -> {
            MessageCounter messageCounter = getCounter(messageKey, CounterEvent.MESSAGE);
            messageCounter.incrementOut();
        });
    }

    public List<MetricSnapshot> snapshots() {
        List<MetricSnapshot> snapshots = new ArrayList<>();
        for (MessageKey messageKey : counterMap.keySet()) {
            snapshots.add(snapshot(messageKey));
        }
        return snapshots;
    }

    private MetricSnapshot snapshot(MessageKey messageKey) {
        ICounter[] counters = counterMap.get(messageKey);
        if (counters == null) {
            return null;
        }

        HealthCounter healthCounter = (HealthCounter) counters[CounterEvent.HEALTH.ordinal()];
        MessageCounter messageCounter = (MessageCounter) counters[CounterEvent.MESSAGE.ordinal()];

        MetricSnapshot snapshot = MetricSnapshot.builder(messageKey)
                .withSuccessPercentage(healthCounter.getSuccessPercentage())
                .withMessageTotalNums(messageCounter.totalInCount())
                .withMessageOverStockNums(messageCounter.overstock())
                .withQps(messageCounter.qpsCounter())
                .build();
        return snapshot;
    }

    private <T extends ICounter> T getCounter(MessageKey messageKey, CounterEvent event) {
        ICounter[] counters = counterMap.get(messageKey);
        if (counters == null) {
            counters = new ICounter[2];
            counters[CounterEvent.HEALTH.ordinal()] = new HealthCounter();
            counters[CounterEvent.MESSAGE.ordinal()] = new MessageCounter();

            ICounter[] previous = counterMap.putIfAbsent(messageKey, counters);
            if (previous != null) {
                return (T) previous[event.ordinal()];
            }
        }

        return (T) counters[event.ordinal()];
    }
}
