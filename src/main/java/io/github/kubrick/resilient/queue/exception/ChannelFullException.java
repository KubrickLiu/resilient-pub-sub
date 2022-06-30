package io.github.kubrick.resilient.queue.exception;

public class ChannelFullException extends RuntimeException {

    public ChannelFullException() {
    }

    public ChannelFullException(String message) {
        super(message);
    }
}
