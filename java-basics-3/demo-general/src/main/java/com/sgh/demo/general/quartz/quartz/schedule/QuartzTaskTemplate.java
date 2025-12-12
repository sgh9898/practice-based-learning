package com.sgh.demo.general.quartz.quartz.schedule;

import com.sgh.demo.general.quartz.quartz.annotation.QuartzScheduled;
import com.sgh.demo.general.quartz.quartz.service.QuartzJobService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * [Quartz 定时任务] 示例(新任务需要初始化至数据库才会激活)
 * <pre>
 * 1. 新任务激活方法: 1) 在 {@link com.sgh.demo.general.quartz.quartz.schedule.QuartzInit} 配置
 *                  2) 使用 {@link QuartzJobService} 手动添加)
 * 2. 禁止任务并发执行: 在当前 Class 上注解 {@link DisallowConcurrentExecution}
 * </pre>
 *
 * @author Song gh
 * @version 2024/1/24
 */
@Slf4j
@DisallowConcurrentExecution
@QuartzScheduled(
        jobName = "demoTask",
        jobGroup = "quartzDemoGroup",
        cronExpression = "0 0/10 * * * ?",
        description = "定时任务示例")
@Component
public class QuartzTaskTemplate implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        try {
            log.info("执行定时任务 [定时任务示例], 执行时间: {}", LocalDateTime.now());
        } catch (Exception e) {
            log.error("任务 [定时任务示例] 执行失败", e);
        }
    }
}