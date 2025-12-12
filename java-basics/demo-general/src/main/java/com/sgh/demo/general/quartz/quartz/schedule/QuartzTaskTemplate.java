package com.sgh.demo.general.quartz.quartz.schedule;

import com.sgh.demo.general.quartz.quartz.annotation.QuartzScheduled;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * [定时任务] 任务调度示例
 * <pre>
 *   1. 任务激活方法: 在类上注解 {@link QuartzScheduled}, 并配置相关参数
 *   2. 如需禁止任务并发执行, 可在类上注解 {@link DisallowConcurrentExecution}
 * </pre>
 *
 * @author Song gh
 * @version 2024/1/24
 */
@Slf4j
@DisallowConcurrentExecution
@QuartzScheduled(jobName = "demoTask", jobGroup = "quartzDemoGroup", cronExpression = "0 0/10 * * * ?")
@Component
class QuartzTaskTemplate implements Job {

    /**
     * 执行定时任务
     *
     * @param context 上下文
     */
    @Override
    public void execute(JobExecutionContext context) {
        try {
            log.info("执行定时任务 [示例], 执行时间: {}", LocalDateTime.now());
        } catch (Exception e) {
            log.error("任务 [示例] 执行失败", e);
        }
    }
}