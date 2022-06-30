package io.github.kubrick.resilient.queue.sequencer;

import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;
import org.jetbrains.annotations.NotNull;

public class DisruptorInitializer {

    protected static Disruptor<SequencedEvent> initialize(@NotNull final int bufferSize,
                                                       @NotNull final SequencedEventTransfer transfer) {
        Disruptor<SequencedEvent> disruptor = new Disruptor<SequencedEvent>(
                SequencedEventFactory.INSTANCE,
                bufferSize,
                DaemonThreadFactory.INSTANCE);

        disruptor.handleEventsWith(transfer);

        disruptor.start();

        return disruptor;
    }
}
