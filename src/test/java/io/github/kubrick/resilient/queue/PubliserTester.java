package io.github.kubrick.resilient.queue;

import io.github.kubrick.resilient.queue.channel.Partition;
import io.github.kubrick.resilient.queue.message.DefaultMessage;
import io.github.kubrick.resilient.queue.message.Message;
import io.github.kubrick.resilient.queue.message.MessageKey;
import io.github.kubrick.resilient.queue.metrics.ConsolePrintMetricsExporter;
import io.github.kubrick.resilient.queue.promise.MessagePromise;
import io.github.kubrick.resilient.queue.pub.Publisher;
import io.github.kubrick.resilient.queue.sub.VoidSubscriber;
import io.github.kubrick.resilient.queue.topic.Topic;
import org.junit.Test;

public class PubliserTester {

    @Test
    public void testTopicEquals() {
        Topic topic = new Topic("test-1");

        Topic topic1 = Topic.find("test-1");
        System.out.println(topic1 == topic);
    }

//    @Test
    public void testPush() throws Exception {
        /****  构造阶段  ****/
        // 构造 topic
        Topic topic = new Topic("test-1");

        // 构造 partition
        ConsolePrintMetricsExporter exporter = new ConsolePrintMetricsExporter(2);
        Partition partition = new Partition(exporter);
        topic.bindPartition(partition);

        // 构造 publisher
        Publisher publisher = new Publisher(topic);

        // 构造 subscriber
        VoidSubscriber subscriber = new VoidSubscriber(topic, promise -> {
            Message<Long> message = promise.getMessage();
            Long value = message.getContent();
            System.out.println("get content duration : " + (System.nanoTime() - value));
        });
        boolean flag = topic.bindSubscriber(MessageKey.SINGLE_KEY, subscriber);
        System.out.println("bind subscriber " + (flag ? "success" : "fail"));

        /****  推送阶段  ****/
        for (int i = 0 ; i < 1000 ; i++) {
            Message<Long> message = new DefaultMessage<>(System.nanoTime());
            MessagePromise<Void> promise = publisher.pub(message);
//            promise.await();
//            Thread.yield();
        }
        System.out.println("finish ~~~~");


        Thread.sleep(1000000);
    }

    public static void main(String[] args) {
        System.out.println(Integer.BYTES);
    }
}
