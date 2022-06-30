package io.github.kubrick.resilient.queue.channel;

import io.github.kubrick.resilient.queue.promise.MessagePromise;
import io.github.kubrick.resilient.queue.sub.ISubscriber;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class InMemoryChannel implements IChannel<MessagePromise> {

    private final int capacity;

    private ArrayList<MessagePromise> list;

    private ISubscriber subscriber;

    private volatile boolean isLock = false;

    private ReentrantLock lock = new ReentrantLock(false);

    public InMemoryChannel(@NotNull int capacity) {
        this.capacity = capacity;
        this.list = new ArrayList<>(capacity);
    }

    @Override
    public boolean offer(MessagePromise promise) throws InterruptedException {
        if (isFull()) {
            return false;
        }

        boolean flag = false;
        if (isLock) {
            lock.lock();
            try {
                flag = list.add(promise);
            } finally {
                lock.unlock();
            }
        } else {
            flag = list.add(promise);
        }
        return flag;
    }

    public List<MessagePromise> drainAll() {
        isLock = true;
        lock.lock();
        try {
            List<MessagePromise> retList = this.list;
            this.list = new ArrayList<>(capacity);
            return retList;
        } finally {
            isLock = false;
            lock.unlock();
        }
    }

    @Override
    public int capacity() {
        return capacity;
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean isFull() {
        return list.size() == capacity;
    }

    @Override
    public void bindSubscriber(ISubscriber subscriber) {
        this.subscriber = subscriber;
    }
}
