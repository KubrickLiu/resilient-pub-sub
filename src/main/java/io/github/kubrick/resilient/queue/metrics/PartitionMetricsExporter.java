package io.github.kubrick.resilient.queue.metrics;

import io.github.kubrick.resilient.queue.executor.GenericThreadFactory;
import io.github.kubrick.resilient.queue.channel.IPartition;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class PartitionMetricsExporter implements IMetricsExporter {

    private final int delaySeconds;

    private IPartition partition;

    private ScheduledExecutorService executor;

    public PartitionMetricsExporter(int delaySeconds) {
        this.delaySeconds = delaySeconds;
        this.executor = Executors.newSingleThreadScheduledExecutor(
                new GenericThreadFactory("MetricsExporter"));
    }

    public List<MetricSnapshot> snapshots() {
        PartitionMetrics partitionMetrics = partition.getPartitionMetrics();
        return partitionMetrics.snapshots();
    }

    @Override
    public void start() {
        executor.scheduleAtFixedRate(() -> {
            List<MetricSnapshot> snapshots = snapshots();
            record(snapshots);
        }, delaySeconds, delaySeconds, TimeUnit.SECONDS);
    }

    public abstract void record(List<MetricSnapshot> snapshots);

    public void setPartition(IPartition partition) {
        this.partition = partition;
    }

    public IPartition partition() {
        return this.partition;
    }
}
