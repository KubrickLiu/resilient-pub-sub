package io.github.kubrick.resilient.queue.promise;

import io.github.kubrick.resilient.queue.listener.GenericFutureListenerGroup;
import io.github.kubrick.resilient.queue.listener.GenericMessageListenerGroup;
import io.github.kubrick.resilient.queue.message.Message;

import java.util.concurrent.Executor;

public class MessagePromise<VALUE> extends GenericPromise<VALUE> {

    private Message message;

    private Stage stage = Stage.INITIALIZE;

    private final GenericMessageListenerGroup listenerGroup;

    public MessagePromise(Message message) {
        this.message = message;
        this.listenerGroup = new GenericMessageListenerGroup(executor);
    }

    public MessagePromise(Executor executor, Message message) {
        super(executor);
        this.message = message;
        this.listenerGroup = new GenericMessageListenerGroup(executor);
    }

    public Message getMessage() {
        return this.message;
    }

    public final Stage getCurrentStage() {
        return stage;
    }

    public void markBuffer() {
        stage = Stage.BUFFER;
    }

    public void markChannel() {
        stage = Stage.CHANNEL;
    }

    public void markConsume() {
        stage = Stage.CONSUME;
    }

    public void markDone() {
        stage = Stage.DONE;
    }

    @Override
    protected GenericFutureListenerGroup getListenerGroup() {
        return listenerGroup;
    }

    public void notifyChannelFull() {
        listenerGroup.notifyChannelFull(this);
    }

    public enum Stage {
        INITIALIZE("initialize"),
        BUFFER("buffer"),
        CHANNEL("channel"),
        CONSUME("consume"),
        DONE("done");

        private String tag;

        Stage(String tag) {
            this.tag = tag;
        }

        public String getTag() {
            return tag;
        }

        @Override
        public String toString() {
            return this.tag;
        }
    }
}
