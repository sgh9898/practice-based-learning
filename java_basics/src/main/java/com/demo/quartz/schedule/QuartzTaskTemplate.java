package com.demo.quartz.schedule;

import com.demo.quartz.service.QuartzJobService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.util.Date;

/**
 * [Quartz 定时任务] 示例(新任务需要初始化至数据库才会激活)
 * <br> 新任务激活方法: 1. 在 {@link QuartzInit} 配置
 * <br>               2. 使用 {@link QuartzJobService} 手动添加)
 *
 * @author Song gh on 2023/12/11.
 */
@Slf4j
public class QuartzTaskTemplate implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        try {
            System.out.println("这是一个 Quartz 定时任务示例" + new Date());
        } catch (Exception e) {
            log.error("任务执行失败", e);
        }
    }
}