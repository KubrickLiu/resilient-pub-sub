package io.github.kubrick.resilient.queue.metrics;

import java.io.Console;
import java.io.PrintWriter;

public class ConsolePrintMetricsExporter extends PrintMetricsExporter {

    public ConsolePrintMetricsExporter(int delaySeconds) {
        super(delaySeconds);
    }

    @Override
    public void print(String s) {
        Console console = System.console();
        if (console != null) {
            PrintWriter writer = console.writer();
            writer.println(s);
        } else {
            System.out.println(s);
        }
    }
}
