package io.github.kubrick.resilient.queue.metrics;

import io.github.kubrick.resilient.queue.message.MessageKey;

public class MetricSnapshot {
    
    private final MessageKey messageKey;
    
    private final int successPercentage;

    private final long messageTotalNums;
    
    private final long messageOverStockNums;
    
    private final long qps;

    public MetricSnapshot(MessageKey messageKey, 
                          int successPercentage,
                          long messageTotalNums,
                          long messageOverStockNums,
                          long qps) {
        this.messageKey = messageKey;
        this.successPercentage = successPercentage;
        this.messageTotalNums = messageTotalNums;
        this.messageOverStockNums = messageOverStockNums;
        this.qps = qps;
    }

    public static Builder builder(MessageKey messageKey) {
        return new Builder(messageKey);
    }

    public MessageKey getMessageKey() {
        return messageKey;
    }

    public int getSuccessPercentage() {
        return successPercentage;
    }

    public long getMessageTotalNums() {
        return messageTotalNums;
    }

    public long getMessageOverStockNums() {
        return messageOverStockNums;
    }

    public long getQps() {
        return qps;
    }
    
    static class Builder {

        private MessageKey messageKey;

        private int successPercentage;

        private long messageTotalNums;

        private long messageOverStockNums;

        private long qps;

        public Builder(MessageKey messageKey) {
            this.messageKey = messageKey;
        }

        public Builder withSuccessPercentage(int successPercentage) {
            this.successPercentage = successPercentage;
            return this;
        }

        public Builder withMessageTotalNums(long messageTotalNums) {
            this.messageTotalNums = messageTotalNums;
            return this;
        }

        public Builder withMessageOverStockNums(long messageOverStockNums) {
            this.messageOverStockNums = messageOverStockNums;
            return this;
        }

        public Builder withQps(long qps) {
            this.qps = qps;
            return this;
        }

        public MetricSnapshot build() {
            return new MetricSnapshot(messageKey, successPercentage, messageTotalNums,
                    messageOverStockNums, qps);
        }
    }
}
