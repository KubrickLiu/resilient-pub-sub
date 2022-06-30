package io.github.kubrick.resilient.queue.sequencer;

import io.github.kubrick.resilient.queue.channel.IPartition;
import io.github.kubrick.resilient.queue.message.Message;
import io.github.kubrick.resilient.queue.promise.MessagePromise;
import com.lmax.disruptor.dsl.Disruptor;
import org.jetbrains.annotations.NotNull;

public class Sequencer {

    private final int concurrentNum;

    private final SequencedEventProducer sequencedEventProducer;

    private final SequencedEventTransfer sequencedEventTransfer;

    private final Disruptor<SequencedEvent> disruptor;

    public Sequencer(@NotNull final int concurrentNum) {
        this.concurrentNum = concurrentNum;
        this.sequencedEventTransfer = new SequencedEventTransfer();
        this.disruptor = DisruptorInitializer
                .initialize(this.concurrentNum, this.sequencedEventTransfer);
        this.sequencedEventProducer = new SequencedEventProducer(this.disruptor.getRingBuffer());
    }

    public void bindTransferPartition(IPartition partition) {
        sequencedEventTransfer.bindTransferPartition(partition);
    }

    public <CONTENT, VALUE> MessagePromise<VALUE> pub(Message<CONTENT> message) {
        return sequencedEventProducer.pub(message);
    }

    public int getConcurrentNum() {
        return concurrentNum;
    }

    public SequencedEventProducer getSequencedEventProducer() {
        return sequencedEventProducer;
    }

    public SequencedEventTransfer getSequencedEventTransfer() {
        return sequencedEventTransfer;
    }

    public Disruptor<SequencedEvent> getDisruptor() {
        return disruptor;
    }
}
