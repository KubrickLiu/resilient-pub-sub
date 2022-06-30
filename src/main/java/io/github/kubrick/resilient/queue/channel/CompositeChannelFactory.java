package io.github.kubrick.resilient.queue.channel;

public class CompositeChannelFactory extends ChannelFactory{

    @Override
    protected int priority() {
        return 1;
    }

    @Override
    protected IChannel doNewChannel(int capacity) {
        return new CompositeChannel(capacity);
    }
}
