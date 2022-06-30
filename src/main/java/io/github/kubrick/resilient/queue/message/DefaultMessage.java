package io.github.kubrick.resilient.queue.message;

import java.util.concurrent.Executor;

public class DefaultMessage<CONTENT> extends Message<CONTENT> {

    public DefaultMessage(CONTENT content) {
        super(content);
    }

    @Override
    public Executor getExecutor() {
        return null;
    }

    @Override
    public MessageKey asMessageKey() {
        return MessageKey.SINGLE_KEY;
    }
}
