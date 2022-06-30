package io.github.kubrick.resilient.queue.sequencer;

import com.lmax.disruptor.EventFactory;

public class SequencedEventFactory implements EventFactory<SequencedEvent> {

    public static final SequencedEventFactory INSTANCE = new SequencedEventFactory();

    @Override
    public SequencedEvent newInstance() {
        return new SequencedEvent();
    }
}
