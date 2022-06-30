package io.github.kubrick.resilient.queue.metrics.counter;

import io.github.kubrick.resilient.queue.metrics.counter.slidingwindow.BucketWindows;

import java.util.concurrent.atomic.LongAdder;

public class MessageCounter implements ICounter {

    private LongAdder in = new LongAdder();

    private LongAdder out = new LongAdder();

    private BucketWindows bucketWindows = new BucketWindows(100, 1000);

    public MessageCounter() {}

    public void incrementIn() {
        in.increment();
    }

    public void incrementOut() {
        out.increment();
        bucketWindows.out();
    }

    public long totalInCount() {
        return in.sum();
    }

    public long totalOutCount() {
        return out.sum();
    }

    public long qpsCounter() {
        return bucketWindows.outCountAndClear();
    }

    public long overstock() {
        return totalInCount() - totalOutCount();
    }

    public void clear() {
        in.reset();
        out.reset();
    }
}
