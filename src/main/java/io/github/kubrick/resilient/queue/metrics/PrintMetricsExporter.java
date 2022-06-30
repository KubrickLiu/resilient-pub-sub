package io.github.kubrick.resilient.queue.metrics;

import io.github.kubrick.resilient.queue.topic.ITopic;

import java.util.List;
import java.util.StringJoiner;

public abstract class PrintMetricsExporter extends PartitionMetricsExporter {

    public PrintMetricsExporter(int delaySeconds) {
        super(delaySeconds);
    }

    @Override
    public void record(List<MetricSnapshot> snapshots) {
        StringJoiner joiner = new StringJoiner(" | ");

        ITopic topic = partition().getTopic();
        joiner.add("topic : " + topic.getTopicName());

        for (MetricSnapshot snapshot : snapshots) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("MessageKey : " + snapshot.getMessageKey())
                    .append("(")
                    .append("suc_percent : " + snapshot.getSuccessPercentage() + " , ")
                    .append("message_nums : " + snapshot.getMessageTotalNums() + " , ")
                    .append("overstock : " + snapshot.getMessageOverStockNums() + " , ")
                    .append("qps : " + snapshot.getQps())
                    .append(")");

            joiner.add(stringBuilder.toString());
        }

        print(joiner.toString());
    }

    public abstract void print(String s);
}
