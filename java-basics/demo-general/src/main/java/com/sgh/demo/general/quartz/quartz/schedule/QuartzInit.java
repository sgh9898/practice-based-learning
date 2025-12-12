package com.sgh.demo.general.quartz.quartz.schedule;

import com.sgh.demo.general.quartz.quartz.annotation.QuartzScheduled;
import com.sgh.demo.general.quartz.quartz.service.QuartzJobService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;

/**
 * [定时任务] Quartz 定时任务初始化
 *
 * @author Song gh
 * @version 2024/1/24
 */
@Component
class QuartzInit {

    @Resource
    private QuartzJobService quartzJobService;

    @Resource
    private ApplicationContext applicationContext;

    /**
     * 项目启动后自动初始化注解了 {@link QuartzScheduled} 的定时任务
     *
     * @see QuartzJobService#upsertJob
     */
    @PostConstruct
    private void autoInitJob() {
        // 获取所有带有 QuartzScheduled 注解的 bean
        Map<String, Object> jobBeans = applicationContext.getBeansWithAnnotation(QuartzScheduled.class);
        for (Object jobBean : jobBeans.values()) {
            Class<?> jobClass = jobBean.getClass();
            QuartzScheduled annotation = jobClass.getAnnotation(QuartzScheduled.class);
            if (annotation != null) {
                // 使用注解信息创建或更新 Quartz 任务
                quartzJobService.upsertJob(jobClass.getName(), annotation.jobName(), annotation.jobGroup(), annotation.cronExpression(), null);
            }
        }
    }
}
