package com.sgh.demo.common.quartz.quartz.schedule;

import com.sgh.demo.common.quartz.quartz.service.QuartzJobService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * Quartz 定时任务初始化(新任务需要初始化至数据库才会激活)
 * <br> 新任务激活方法: 1. 在 {@link #autoInitJob} 配置
 * <br>               2. 使用 {@link QuartzJobService} 手动添加)
 *
 * @author Song gh
 * @version 2024/01/24
 */
@Service
class QuartzInit {

    @Resource
    private QuartzJobService quartzJobService;

    /**
     * 初始化指定的定时任务, 任务名/组名可以随便取
     *
     * @see QuartzJobService#upsertJob
     */
    @PostConstruct
    private void autoInitJob() {
        quartzJobService.upsertJob(com.sgh.demo.common.quartz.quartz.schedule.QuartzTaskTemplate.class.getName(), "demoTask", "quartzDemoGroup", "0 0/10 * * * ?", null);
    }
}
