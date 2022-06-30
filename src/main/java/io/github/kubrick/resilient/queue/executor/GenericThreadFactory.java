package io.github.kubrick.resilient.queue.executor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class GenericThreadFactory implements ThreadFactory {

    private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
    private final Object group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;
    private boolean daemon = true;

    public GenericThreadFactory(String poolName) {
        SecurityManager s = System.getSecurityManager();
        this.group = (s != null) ? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
        this.namePrefix = poolName + "-" + POOL_NUMBER.getAndIncrement() + "-";
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread((ThreadGroup) group, r,
                namePrefix + threadNumber.getAndIncrement(),
                0);
        if (t.isDaemon() != daemon) {
            t.setDaemon(daemon);
        }
        t.setPriority(Thread.NORM_PRIORITY);
        return t;
    }
}
