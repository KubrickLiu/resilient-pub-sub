package io.github.kubrick.resilient.queue.topic;

import io.github.kubrick.resilient.queue.channel.IPartition;
import io.github.kubrick.resilient.queue.message.MessageKey;
import io.github.kubrick.resilient.queue.sub.ISubscriber;
import org.jetbrains.annotations.NotNull;

public interface ITopic {

    String getTopicName();

    boolean bindPartition(IPartition partition);

    boolean bindSubscriber(@NotNull MessageKey messageKey, @NotNull ISubscriber subscriber);

    IPartition getPartition();

}
