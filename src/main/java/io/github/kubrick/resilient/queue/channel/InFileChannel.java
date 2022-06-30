package io.github.kubrick.resilient.queue.channel;

import io.github.kubrick.resilient.queue.promise.MessagePromise;
import io.github.kubrick.resilient.queue.sub.ISubscriber;

public class InFileChannel implements IChannel<MessagePromise> {

    private final String filePath;

    public InFileChannel(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public boolean offer(MessagePromise promise) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int capacity() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isFull() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void bindSubscriber(ISubscriber subscriber) {
        throw new UnsupportedOperationException();
    }
}
