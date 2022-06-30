package io.github.kubrick.resilient.queue.metrics.counter.slidingwindow;

import java.util.concurrent.atomic.LongAdder;

public class Bucket {

    private final LongAdder[] counters;

    public Bucket() {
        BucketEvent[] events = BucketEvent.values();
        this.counters = new LongAdder[events.length];
        for (BucketEvent event : events) {
            this.counters[event.ordinal()] = new LongAdder();
        }
    }

    public void incrementIn() {
        this.counters[BucketEvent.IN.ordinal()].increment();
    }

    public void incrementOut() {
        this.counters[BucketEvent.OUT.ordinal()].increment();
    }

    public long inCount() {
        return get(BucketEvent.IN);
    }

    public long outCount() {
        return get(BucketEvent.OUT);
    }

    private long get(BucketEvent event) {
        return this.counters[event.ordinal()].sum();
    }

    public void reset() {
        for (BucketEvent event : BucketEvent.values()) {
            this.counters[event.ordinal()].reset();
        }
    }
}
