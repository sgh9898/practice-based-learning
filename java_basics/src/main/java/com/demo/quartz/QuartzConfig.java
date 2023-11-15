package com.demo.quartz;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Quartz 配置
 *
 * @author Song gh on 2023/9/28.
 */
@Configuration
public class QuartzConfig {

    /** 定时任务触发 */
//    @Bean
    public Trigger trigger() {
        return TriggerBuilder.newTrigger()
                // 绑定工作任务
                .forJob(jobDetail())
                // 每隔 5 秒执行一次 job
                .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(5))
                .build();
    }

    /** 定时任务构建 */
    @Bean
    public JobDetail jobDetail() {
        return JobBuilder.newJob(QuartzTasks.class)
                // 指定任务的名称
                .withIdentity("JobExample")
                // 任务描述
                .withDescription("任务描述: 这是一个 Quartz 定时任务示例")
                // 每次任务执行后进行存储
                .storeDurably()
                .build();
    }
}