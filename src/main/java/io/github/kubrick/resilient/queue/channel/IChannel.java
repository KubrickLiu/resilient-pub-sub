package io.github.kubrick.resilient.queue.channel;

import io.github.kubrick.resilient.queue.sub.ISubscriber;

public interface IChannel<M> {

    boolean offer(M promise) throws Exception;

    int capacity();

    int size();

    boolean isEmpty();

    boolean isFull();

    void bindSubscriber(ISubscriber subscriber);
}
