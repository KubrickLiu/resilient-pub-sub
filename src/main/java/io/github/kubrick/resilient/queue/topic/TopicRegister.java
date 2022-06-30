package io.github.kubrick.resilient.queue.topic;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TopicRegister {

    private static final Map<String, ITopic> TOPIC_CONCURRENT_MAP = new ConcurrentHashMap<>();

    protected static boolean bind(@NotNull final ITopic topic) {
        final String topicName = topic.getTopicName();

        ITopic previous = TOPIC_CONCURRENT_MAP.putIfAbsent(topicName, topic);
        return previous == null;
    }

    protected static ITopic findTopic(@NotNull final String topicName) {
        return TOPIC_CONCURRENT_MAP.get(topicName);
    }
}
