![Apache License, Version 2.0, January 2004](https://img.shields.io/github/license/apache/maven.svg?label=License)
[![Maven Central](https://img.shields.io/maven-central/v/org.apache.maven/apache-maven.svg?label=Maven%20Central)](https://search.maven.org/artifact/org.apache.maven/apache-maven)

# What this is
resilient stored message queue with pub sub function.
* The main function is to publish and subscribe messages;
  * 主要功能是订阅和发布的消息队列；
* Different from the general queue, it has better scalability;
  * 和一般消息队列不同的是具有更好的弹性；
* The elastic implementation logic is the mixed storage of memory and disk;
  * 其弹性的原理是内存和磁盘混合使用，可以尽可能接收更多消息；
* Thanks for spark RDD's elastic logic and Kafka's logsegment disk operation;
  * 感谢 Spark RDD 的弹性逻辑和 Kafka 的 LogSegment 磁盘操作；

## Quick-start
Maven:

```maven
<dependency>
    <groupId>io.github.kubrick</groupId>
    <artifactId>resilient-pub-sub</artifactId>
    <version>1.0.1</version>
</dependency>
```

### Example Code : 

```java
 Topic topic = new Topic("test-1"); // Build a topic
 
 Partition partition = new Partition(); // Build a patition and binding with the topic 
 topic.bindPartition(partition);
 
  // Build a publisher with topic
  Publisher publisher = new Publisher(topic);

  // Build a subscriber
  VoidSubscriber subscriber = new VoidSubscriber(topic, promise -> {
      Message<Long> message = promise.getMessage();
      Long value = message.getContent();
      
      // It can show the receiving process of each message
      System.out.println("get content duration : " + (System.nanoTime() - value));
  });
  
  // Subscribers subscribe to a topic and a related MessageKey
  boolean flag = topic.bindSubscriber(MessageKey.SINGLE_KEY, subscriber);

   /****  推送阶段  ****/
   for (int i = 0 ; i < 1000 ; i++) {
      Message<Long> message = new DefaultMessage<>(System.nanoTime());
      MessagePromise<Void> promise = publisher.pub(message); // Get promise after actual push
      promise.await(); // Wait for the promise to be consumed by subscribers
      Thread.yield();
   }
   
   System.out.println("finish ~~~~");
```

