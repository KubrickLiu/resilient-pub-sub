package io.github.kubrick.resilient.queue.sequencer;

import io.github.kubrick.resilient.queue.message.Message;
import io.github.kubrick.resilient.queue.promise.MessagePromise;
import com.lmax.disruptor.RingBuffer;
import org.jetbrains.annotations.NotNull;

public class SequencedEventProducer {

    private final RingBuffer<SequencedEvent> buffer;

    public SequencedEventProducer(@NotNull RingBuffer<SequencedEvent> buffer) {
        this.buffer = buffer;
    }

    public <CONTENT, VALUE> MessagePromise<VALUE> pub(Message<CONTENT> message) {
        MessagePromise<VALUE> promise = message.newPromise();
        promise.markBuffer();

        final long bufferSequenceIndex = buffer.next();
        try {
            SequencedEvent sequencedEvent = buffer.get(bufferSequenceIndex);
            sequencedEvent.setValue(promise);
            return promise;
        } finally {
            buffer.publish(bufferSequenceIndex);
        }
    }
}
