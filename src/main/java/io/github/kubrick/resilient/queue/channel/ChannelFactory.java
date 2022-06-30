package io.github.kubrick.resilient.queue.channel;

import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

public abstract class ChannelFactory {

    private static final ChannelFactory FACTORY = load(ChannelFactory.class.getClassLoader());

    private static final ChannelFactory load(ClassLoader classLoader) {
        ServiceLoader<ChannelFactory> providers = ServiceLoader.load(ChannelFactory.class, classLoader);

        ChannelFactory best = null;
        int priority = -1;

        for (ChannelFactory factory : providers) {
            if (factory.priority() > priority) {
                best = factory;
                priority = factory.priority();
            }
        }

        return best;
    }

    protected abstract int priority();

    protected abstract IChannel doNewChannel(final int capacity);

    public static IChannel newChannel(final int capacity) {
        if (FACTORY == null) {
            throw new ServiceConfigurationError("No functional channel factory found.");
        }

        return FACTORY.doNewChannel(capacity);
    }
}
