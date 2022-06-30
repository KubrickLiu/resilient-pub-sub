package io.github.kubrick.resilient.queue.message;

import io.github.kubrick.resilient.queue.executor.SyncExecutor;
import io.github.kubrick.resilient.queue.promise.MessagePromise;
import io.github.kubrick.resilient.queue.topic.ITopic;

import java.util.concurrent.Executor;

public abstract class Message<CONTENT> implements IMessage<CONTENT> {

    private ITopic topic;

    public abstract MessageKey asMessageKey();

    private final CONTENT content;

    private volatile MessagePromise promise;

    public Message(CONTENT content) {
        this.content = content;
    }

    public abstract Executor getExecutor();

    @Override
    public <VALUE> MessagePromise<VALUE> newPromise() {
        return newPromise(getExecutor());
    }

    @Override
    public <VALUE> MessagePromise<VALUE> newPromise(Executor executor) {
        if (this.promise == null) {
            if (executor == null) {
                executor = SyncExecutor.INSTANCE;
            }

            this.promise = new MessagePromise(executor, this);
        }

        return this.promise;
    }

    @Override
    public ITopic getTopic() {
        return topic;
    }

    @Override
    public CONTENT getContent() {
        return content;
    }

    @Override
    public void setTopic(ITopic topic) {
        this.topic = topic;
    }

}
