package io.github.kubrick.resilient.queue.channel;

import io.github.kubrick.resilient.queue.exception.ChannelFullException;
import io.github.kubrick.resilient.queue.executor.GenericThreadFactory;
import io.github.kubrick.resilient.queue.promise.MessagePromise;
import io.github.kubrick.resilient.queue.sub.ISubscriber;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class CompositeChannel implements IChannel<MessagePromise> {

    private static final int DEFAULT_MEMORY_CHANNEL_CAPACITY = 2000;

    private static final int DEFAULT_BUFFER_SIZE = 100;

    private boolean isAllowCompositeStorage = false;

    private final InMemoryChannel memoryChannel;

    private InFileChannel fileChannel;

    private final GenericThreadFactory factory;

    private ISubscriber subscriber;

    private final BatchMessageDrainer drainer;

    public CompositeChannel(int memoryChannelCapacity) {
        if (memoryChannelCapacity < 0) {
            memoryChannelCapacity = DEFAULT_MEMORY_CHANNEL_CAPACITY;
        }

        this.memoryChannel = new InMemoryChannel(memoryChannelCapacity);
        this.factory = new GenericThreadFactory("CompositeChannel");
        this.drainer = new BatchMessageDrainer(DEFAULT_BUFFER_SIZE);
        Thread thread = this.factory.newThread(this.drainer);

        this.drainer.onCreate(thread);
    }

    @Override
    public boolean offer(MessagePromise promise) throws Exception {
        boolean flag = doOffer(promise);
        if (flag) {
            drainer.tryUnlock();
        }
        return flag;
    }

    private boolean doOffer(MessagePromise promise) throws Exception {
        if (isAllowCompositeStorage) {
            // TODO 从 file channel 中 copy 一部分数据到内存队列
            throw new IllegalAccessException();
        }

        if (memoryChannel.isFull()) {
            boolean isSuccess = false;
            if (isAllowCompositeStorage) {
                isSuccess = fileChannel.offer(promise);
            }

            if (!isSuccess) {
                promise.setFailure(
                        new ChannelFullException("channel is full, can not pub message"));
                promise.cancel(false);
            }
            return isSuccess;
        } else {
            return memoryChannel.offer(promise);
        }
    }

    @Override
    public int capacity() {
        int capacity = memoryChannel.capacity();
        if (isAllowCompositeStorage) {
            capacity += fileChannel.capacity();
        }
        return capacity;
    }

    @Override
    public int size() {
        int size = memoryChannel.size();
        if (isAllowCompositeStorage) {
            size += fileChannel.size();
        }
        return size;
    }

    @Override
    public boolean isEmpty() {
        boolean isEmpty = memoryChannel.isEmpty();
        if (!isEmpty) {
            return false;
        }

        if (isAllowCompositeStorage) {
            isEmpty = fileChannel.isEmpty();
        }
        return isEmpty;
    }

    @Override
    public boolean isFull() {
        boolean isFull = memoryChannel.isFull();
        if (!isFull) {
            return false;
        }

        if (isAllowCompositeStorage) {
            throw new RuntimeException("");
        }
        return false;
    }

    @Override
    public void bindSubscriber(@NotNull ISubscriber subscriber) {
        this.subscriber = subscriber;
        memoryChannel.bindSubscriber(subscriber);
        if (isAllowCompositeStorage) {
            fileChannel.bindSubscriber(subscriber);
        }
    }

    private class BatchMessageDrainer implements Runnable {

        private final int bufferSize;

        private Thread runningThread;

        private BatchMessageDrainer(@NotNull final int bufferSize) {
            this.bufferSize = bufferSize;
        }

        protected void onCreate(@NotNull final Thread runningThread) {
            this.runningThread = runningThread;
            this.runningThread.start();
        }

        @Override
        public void run() {
            while (true) {
                if (isEmpty()) {
                    LockSupport.parkNanos(this, TimeUnit.MILLISECONDS.toNanos(1));
                    continue;
                }

                List<MessagePromise> list = memoryChannel.drainAll();
                for (MessagePromise promise : list) {
                    if (promise == null) {
                        continue;
                    }

                    promise.markConsume();
                    subscriber.sub(promise);
                    promise.markDone();
                    promise.ack();
                }
            }
        }

        protected void tryUnlock() {
            LockSupport.unpark(runningThread);
        }
    }
}
