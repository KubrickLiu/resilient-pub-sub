package io.github.kubrick.resilient.queue.message;

public interface MessageKey {

    int DEFAULT_CAPACITY = 100000;

    default int capacity() {
        return DEFAULT_CAPACITY;
    }

    @Override
    int hashCode();

    @Override
    String toString();

    /**
     * channel 单通道 message key
     */
    MessageKey SINGLE_KEY = new MessageKey() {

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public String toString() {
            return "SINGLE_KEY";
        }
    };
}
