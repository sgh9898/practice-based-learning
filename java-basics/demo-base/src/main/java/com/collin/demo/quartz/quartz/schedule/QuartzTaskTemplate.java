package com.collin.demo.quartz.quartz.schedule;

import com.collin.demo.quartz.quartz.service.QuartzJobService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.util.Date;

/**
 * [Quartz 定时任务] 示例(新任务需要初始化至数据库才会激活)
 * <br> 新任务激活方法: 1. 在 {@link QuartzInit} 配置
 * <br>               2. 使用 {@link QuartzJobService} 手动添加)
 * <br> 禁止任务并发执行: 在当前 Class 上注解 {@link DisallowConcurrentExecution}
 *
 * @author Song gh
 * @version 2024/01/24
 */
@Slf4j
@DisallowConcurrentExecution
public class QuartzTaskTemplate implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        try {
            log.info("一分钟 Quartz 定时任务示例, 执行时间: " + new Date());
        } catch (Exception e) {
            log.error("任务执行失败", e);
        }
    }
}