package com.sgh.demo.general.quartz.quartz.schedule;

import com.sgh.demo.general.quartz.quartz.service.QuartzJobService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.util.Date;

/**
 * [Quartz 定时任务] 示例(新任务需要初始化至数据库才会激活)
 * <pre>
 * 1. 新任务激活方法: 可在 {@link QuartzInit} 配置, 或使用 {@link QuartzJobService} 手动添加)
 * 2. 如需禁止任务并发执行, 可在当前 Class 上注解 {@link DisallowConcurrentExecution} </pre>
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
            log.info("十分钟 Quartz 定时任务示例, 执行时间: {}", new Date());
        } catch (Exception e) {
            log.error("任务执行失败", e);
        }
    }
}