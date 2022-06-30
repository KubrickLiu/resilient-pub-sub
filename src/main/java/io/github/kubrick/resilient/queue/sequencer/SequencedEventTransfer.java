package io.github.kubrick.resilient.queue.sequencer;


import io.github.kubrick.resilient.queue.channel.IPartition;
import io.github.kubrick.resilient.queue.promise.MessagePromise;
import com.lmax.disruptor.EventHandler;

public class SequencedEventTransfer implements EventHandler<SequencedEvent> {

    private IPartition partition;

    @Override
    public void onEvent(SequencedEvent event, long sequence, boolean endOfBatch) throws Exception {
        if (partition == null) {
            throw new NullPointerException("accept message partition is not exists.");
        }

        MessagePromise promise = event.getValueAndClear();
        if (partition.send(promise)) {
            promise.markChannel();
        } else {
            // channel 已经打满
            promise.notifyChannelFull();
        }
    }

    protected void bindTransferPartition(IPartition partition) {
        this.partition = partition;
    }
}
