package com.collin.demo.sharding.sharding.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 系统时间
 *
 * @version 2024/1/12
 */
public class SystemClockUtils {

    private final long period;
    private final AtomicLong now;

    public static long now() {
        return instance().currentTimeMillis();
    }

    private SystemClockUtils(long period) {
        this.period = period;
        this.now = new AtomicLong(System.currentTimeMillis());
        this.scheduleClockUpdating();
    }

    private static SystemClockUtils instance() {
        return InstanceHolder.INSTANCE;
    }

    private void scheduleClockUpdating() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor((runnable) -> {
            Thread thread = new Thread(runnable, "System Clock");
            thread.setDaemon(true);
            return thread;
        });
        scheduler.scheduleAtFixedRate(() -> this.now.set(System.currentTimeMillis()), this.period, this.period, TimeUnit.MILLISECONDS);
    }

    private long currentTimeMillis() {
        return this.now.get();
    }

    private static class InstanceHolder {
        public static final SystemClockUtils INSTANCE = new SystemClockUtils(1L);

        private InstanceHolder() {
        }
    }
}

