package io.github.kubrick.resilient.queue.sequencer;

import io.github.kubrick.resilient.queue.promise.MessagePromise;

public class SequencedEvent {

    private MessagePromise<?> value;

    public <T extends MessagePromise> T getValueAndClear() {
        MessagePromise tmp = value;
        value = null;
        return (T) tmp;
    }

    public <T extends MessagePromise> void setValue(T value) {
        if (this.value == null) {
            this.value = value;
            return;
        }

        throw new IllegalStateException("sequence event value already fill.");
    }
}
