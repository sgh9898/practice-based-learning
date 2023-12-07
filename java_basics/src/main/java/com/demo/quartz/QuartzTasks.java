package com.demo.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.util.Date;

/**
 * Quartz 定时任务
 *
 * @author Song gh on 2023/9/28.
 */
public class QuartzTasks implements Job {

    public void execute(JobExecutionContext context) {
        System.out.println("这是一个 Quartz 定时任务示例" + new Date());
    }
}
