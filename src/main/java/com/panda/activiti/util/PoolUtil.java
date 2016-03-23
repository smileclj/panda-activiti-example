package com.panda.activiti.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.stereotype.Component;

@Component
public class PoolUtil {

    private ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(16);

    public ScheduledExecutorService getScheduledPool() {
        return scheduledPool;
    }

}
