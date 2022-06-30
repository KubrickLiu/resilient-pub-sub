package io.github.kubrick.resilient.queue.metrics.counter;

import java.util.concurrent.atomic.LongAdder;

public class HealthCounter implements ICounter {

    private LongAdder totalCount = new LongAdder();

    private LongAdder errorCount = new LongAdder();

    public HealthCounter() {}

    public void increment() {
        totalCount.increment();
    }

    public void incrementError() {
        errorCount.increment();
    }

    public long totalCount() {
        return totalCount.sum();
    }

    public long errorCount() {
        return errorCount.sum();
    }

    public int getSuccessPercentage() {
        long total = totalCount.sum();
        if (total == 0) {
            return 0;
        }

        long error = errorCount.sum();
        long success = total - error;

        int successPercentage = (int) (((double) success / total) * 100);
        return successPercentage;
    }

    public int getErrorPercentage() {
        long total = totalCount.sum();
        if (total == 0) {
            return 0;
        }

        long error = errorCount.sum();
        int errorPercentage = (int) (((double) error / total) * 100);
        return errorPercentage;
    }

    public void clear() {
        totalCount.reset();
        errorCount.reset();
    }
}
