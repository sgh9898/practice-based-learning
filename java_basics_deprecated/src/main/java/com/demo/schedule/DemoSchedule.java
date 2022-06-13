package com.demo.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 定时任务
 *
 * @author Song gh on 2022/4/15.
 */
@Component
public class DemoSchedule {

    /** 定时任务 (every minute) */
    @Scheduled(fixedDelayString = "PT1M")
    public void simpleTask() {
        System.out.println("一分钟定时任务 " + new Date());
    }
}
