package io.github.kubrick.resilient.queue.metrics.counter.slidingwindow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.Lock;

public class BucketWindows {

    private final long windowLengthInMs;

    private final long intervalInMs;

    private final int windowCount;

    private final AtomicReferenceArray<WindowWrap<Bucket>> slidingWindows;

    private final LockGroup lockGroup;

    public BucketWindows(long windowLengthInMs, long intervalInMs) {
        this.windowLengthInMs = windowLengthInMs;
        this.intervalInMs = intervalInMs;

        this.windowCount = (int) ((intervalInMs / windowLengthInMs) + (intervalInMs % windowLengthInMs));
        this.lockGroup = new LockGroup(this.windowCount);
        this.slidingWindows = new AtomicReferenceArray<WindowWrap<Bucket>>(this.windowCount);
    }

    public void in() {
        WindowWrap<Bucket> windowWrap = currentWindow();
        Bucket bucket = windowWrap.value();
        bucket.incrementIn();
    }

    public void out() {
        WindowWrap<Bucket> windowWrap = currentWindow();
        Bucket bucket = windowWrap.value();
        bucket.incrementOut();
    }

    public long inCountAndClear() {
        long count = 0;
        List<WindowWrap<Bucket>> bucketList = listBuckets();
        long endTimeMs = System.currentTimeMillis() - intervalInMs;

        for (WindowWrap<Bucket> wrap : bucketList) {
            Bucket bucket = wrap.value();
            count += bucket.inCount();

            if (endTimeMs > (wrap.windowStart() + windowLengthInMs)) {
                bucket.reset();
            }
        }

        return count;
    }

    public long outCountAndClear() {
        long count = 0;
        List<WindowWrap<Bucket>> bucketList = listBuckets();
        long endTimeMs = System.currentTimeMillis() - intervalInMs;

        for (WindowWrap<Bucket> wrap : bucketList) {
            Bucket bucket = wrap.value();
            count += bucket.outCount();

            if (endTimeMs > (wrap.windowStart() + windowLengthInMs)) {
                bucket.reset();
            }
        }

        return count;
    }

    public List<WindowWrap<Bucket>> listBuckets() {
        int length = slidingWindows.length();
        List<WindowWrap<Bucket>> bucketList = new ArrayList<>();

        for (int i = 0; i < length; i++) {
            WindowWrap<Bucket> windowWrap = slidingWindows.get(i);
            if (windowWrap != null) {
                bucketList.add(windowWrap);
            }
        }

        return bucketList;
    }

    private WindowWrap<Bucket> currentWindow() {
        long timeMills = System.currentTimeMillis();

        int slidingWindowOffset = calculateWindowsOffset(timeMills);

        long timeStart = calculateTimeStart(timeMills);

        while (true) {
            WindowWrap<Bucket> window = slidingWindows.get(slidingWindowOffset);
            if (window == null) {
                WindowWrap<Bucket> newWindow =
                        new WindowWrap<Bucket>(windowLengthInMs, timeStart, new Bucket());
                if (slidingWindows.compareAndSet(slidingWindowOffset, null, newWindow)) {
                    return newWindow;
                } else {
                    Thread.yield();
                }
            } else if (window.windowStart() == timeStart) {
                return window;
            } else if (timeStart > window.windowStart()) {
                Lock lock = lockGroup.getLock(slidingWindowOffset);
                if (lock.tryLock()) {
                    try {
                        window.resetWindowStartTime(timeStart);
                        window.value().reset();
                    } finally {
                        lock.unlock();
                    }
                } else {
                    Thread.yield();
                }
            } else {
                // Should not go through here, as the provided time is already behind.
                return new WindowWrap<Bucket>(windowLengthInMs, timeStart, new Bucket());
            }
        }
    }

    private int calculateWindowsOffset(long timeMills) {
        long timeId = timeMills / windowLengthInMs;
        int offset = (int) (timeId % slidingWindows.length());
        return offset;
    }

    private long calculateTimeStart(long timeMills) {
        long timeStart = timeMills - (timeMills % windowLengthInMs);
        return timeStart;
    }
}
