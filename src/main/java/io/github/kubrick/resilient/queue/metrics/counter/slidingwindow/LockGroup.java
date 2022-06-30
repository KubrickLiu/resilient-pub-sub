package io.github.kubrick.resilient.queue.metrics.counter.slidingwindow;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class LockGroup {

    private static final int MAX_POWER_OF_TWO = 1 << (Integer.SIZE - 2);

    private static final int ALL_SET = ~0;

    private final int size;

    private final int mask;

    private final ConcurrentHashMap<Integer, ReentrantLock> locks;

    public LockGroup(int size) {
        this.size = size;
        this.mask = (size > MAX_POWER_OF_TWO ? ALL_SET : ceilToPowerOfTwo(size)) - 1;
        this.locks = new ConcurrentHashMap<>();
    }

    private static int ceilToPowerOfTwo(int x) {
        return 1 << Integer.SIZE - Integer.numberOfLeadingZeros(x - 1);
    }

    public ReentrantLock getLock(@NotNull Object o) {
        int index = getIndexFromObject(o);
        ReentrantLock lock = locks.get(index);
        if (lock != null) {
            return lock;
        }

        lock = new ReentrantLock(false);
        ReentrantLock previous = locks.putIfAbsent(index, lock);
        if (previous != null) {
            return previous;
        } else {
            if (lock == null) {
                throw new NullPointerException();
            }
            return lock;
        }
    }

    private int getIndexFromObject(Object o) {
        int hashCode = o.hashCode();
        hashCode ^= (hashCode >>> 20) ^ (hashCode >>> 12);
        hashCode = hashCode ^ (hashCode >>> 7) ^ (hashCode >>> 4);

        return hashCode & mask;
    }
}
