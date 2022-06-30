package io.github.kubrick.resilient.queue.metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogPrintMetricsExporter extends PrintMetricsExporter {

    private static final Logger METRICS_LOGGER =
            LoggerFactory.getLogger("METRICS_LOGGER");

    public LogPrintMetricsExporter(int delaySeconds) {
        super(delaySeconds);
    }

    @Override
    public void print(String s) {
        METRICS_LOGGER.info(s);
    }
}
