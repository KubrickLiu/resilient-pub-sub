package io.github.kubrick.resilient.queue.metrics.counter.slidingwindow;

public class WindowWrap<VALUE> {

    private final long windowLengthInMs;

    private long windowStart;

    private VALUE value;

    public WindowWrap(long windowLengthInMs, long windowStart, VALUE value) {
        this.windowLengthInMs = windowLengthInMs;
        this.windowStart = windowStart;
        this.value = value;
    }

    public long windowLength() {
        return windowLengthInMs;
    }

    public long windowStart() {
        return windowStart;
    }

    public VALUE value() {
        return value;
    }

    public void setValue(VALUE value) {
        this.value = value;
    }

    public WindowWrap<VALUE> resetWindowStartTime(long windowStartTime) {
        this.windowStart = windowStartTime;
        return this;
    }
}
