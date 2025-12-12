package com.sgh.demo.general.quartz.quartz.service.impl;

import com.pubinfo.edu.schedule.quartz.service.QuartzJobService;
import jakarta.annotation.Resource;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * [功能] Quartz 任务调度
 *
 * @author Song gh
 * @since 2024/1/24
 */
@Service
public class QuartzJobServiceImpl implements QuartzJobService {

    @Resource
    private Scheduler scheduler;

    /**
     * [新增/更新] 添加任务并启动(任务已存在时转为更新)
     * <br> 任务严格遵照设定时间执行, 不会在启动时额外执行一次
     *
     * @param clazzName 定时任务 class 名称, 需要 implements {@link Job}
     * @param jobName   定时任务名
     * @param groupName 定时任务组名
     * @param cronExp   时间表达式, {@link CronExpression}
     * @param jobParams 向定时任务传递的参数
     */
    @Override
    public void upsertJob(String clazzName, String jobName, String groupName, String cronExp, Map<String, Object> jobParams) {
        if (checkJobExists(jobName, groupName)) {
            // 任务已存在时更新
            updateJob(jobName, groupName, cronExp, jobParams);
        } else {
            // 任务不存在时新增
            addJobIfNotExists(clazzName, jobName, groupName, cronExp, jobParams);
        }
    }

    /**
     * [新增] 添加任务并启动(任务已存在时不做任何操作)
     * <br> 任务严格遵照设定时间执行, 不会在启动时额外执行一次
     *
     * @param clazzName 定时任务 class 名称, 需要 implements {@link Job}
     * @param jobName   定时任务名
     * @param groupName 定时任务组名
     * @param cronExp   时间表达式, {@link CronExpression}
     * @param jobParams 向定时任务传递的参数
     */
    @Override
    @SuppressWarnings("unchecked")
    public void addJobIfNotExists(String clazzName, String jobName, String groupName, String cronExp, Map<String, Object> jobParams) {
        try {
            // 启动调度器, 默认初始化的时候已经启动
            if (scheduler.isShutdown()) {
                scheduler.start();
            }

            // 任务已存在时直接返回
            if (checkJobExists(jobName, groupName)) {
                return;
            }

            // 构建 job 信息
            Class<? extends Job> jobClass = (Class<? extends Job>) Class.forName(clazzName);
            JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, groupName).build();
            // 配置任务执行时间
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExp).withMisfireHandlingInstructionDoNothing();
            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName, groupName).withSchedule(scheduleBuilder).build();
            // 传入 job 所需参数
            if (jobParams != null) {
                trigger.getJobDataMap().putAll(jobParams);
            }

            // 添加新任务
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            throw new UnsupportedOperationException("[Quartz] 任务组 " + groupName + " 创建任务 " + jobName + " 失败", e);
        }
    }

    /**
     * [更新] 更新任务
     * <br> 任务严格遵照设定时间执行, 不会在启动时额外执行一次
     *
     * @param jobName   定时任务名
     * @param groupName 定时任务组名
     * @param cronExp   时间表达式, {@link CronExpression}
     * @param jobParams 向定时任务传递的参数
     */
    @Override
    public void updateJob(String jobName, String groupName, String cronExp, Map<String, Object> jobParams) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(jobName, groupName);
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);

            // 更新任务执行时间
            if (cronExp != null) {
                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExp).withMisfireHandlingInstructionDoNothing();
                trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
            }
            // 传入 job 所需参数
            if (jobParams != null) {
                trigger.getJobDataMap().putAll(jobParams);
            }

            // 使用新的 trigger 重新执行
            scheduler.rescheduleJob(triggerKey, trigger);
        } catch (Exception e) {
            throw new UnsupportedOperationException("[Quartz] 任务组 " + groupName + " 更新任务 " + jobName + " 失败", e);
        }
    }

    /**
     * 任务是否存在
     *
     * @param jobName   定时任务名
     * @param groupName 定时任务组名
     * @return true-存在, false-不存在
     */
    @Override
    public boolean checkJobExists(String jobName, String groupName) {
        try {
            return scheduler.checkExists(JobKey.jobKey(jobName, groupName));
        } catch (SchedulerException e) {
            throw new UnsupportedOperationException("[Quartz] 任务组 " + groupName + " 查询任务 " + jobName + " 失败", e);
        }
    }

    /**
     * 删除任务
     *
     * @param jobName   定时任务名
     * @param groupName 定时任务组名
     */
    @Override
    public void deleteJob(String jobName, String groupName) {
        try {
            // 暂停 --> 移除 --> 删除
            scheduler.pauseTrigger(TriggerKey.triggerKey(jobName, groupName));
            scheduler.unscheduleJob(TriggerKey.triggerKey(jobName, groupName));
            scheduler.deleteJob(JobKey.jobKey(jobName, groupName));
        } catch (Exception e) {
            throw new UnsupportedOperationException("[Quartz] 任务组 " + groupName + " 删除任务 " + jobName + " 失败", e);
        }
    }

    /**
     * 暂停任务
     *
     * @param jobName   定时任务名
     * @param groupName 定时任务组名
     */
    @Override
    public void pauseJob(String jobName, String groupName) {
        try {
            scheduler.pauseJob(JobKey.jobKey(jobName, groupName));
        } catch (SchedulerException e) {
            throw new UnsupportedOperationException("[Quartz] 任务组 " + groupName + " 暂停任务 " + jobName + " 失败", e);
        }
    }

    /**
     * 恢复任务
     *
     * @param jobName   定时任务名
     * @param groupName 定时任务组名
     */
    @Override
    public void resumeJob(String jobName, String groupName) {
        try {
            scheduler.resumeJob(JobKey.jobKey(jobName, groupName));
        } catch (SchedulerException e) {
            throw new UnsupportedOperationException("[Quartz] 任务组 " + groupName + " 恢复任务 " + jobName + " 失败", e);
        }
    }

    /**
     * 立即运行一次定时任务
     *
     * @param jobName   定时任务名
     * @param groupName 定时任务组名
     */
    @Override
    public void runOnce(String jobName, String groupName) {
        try {
            scheduler.triggerJob(JobKey.jobKey(jobName, groupName));
        } catch (SchedulerException e) {
            throw new UnsupportedOperationException("[Quartz] 任务组 " + groupName + " 单次运行任务 " + jobName + " 失败", e);
        }
    }

    /** 启动所有任务 */
    @Override
    public void startAllJobs() {
        try {
            scheduler.start();
        } catch (Exception e) {
            throw new UnsupportedOperationException("[Quartz] 开启所有任务失败", e);
        }
    }

    /** 暂停所有任务 */
    @Override
    public void pauseAllJobs() {
        try {
            scheduler.pauseAll();
        } catch (Exception e) {
            throw new UnsupportedOperationException("[Quartz] 暂停所有任务失败", e);
        }
    }

    /** 恢复所有任务 */
    @Override
    public void resumeAllJobs() {
        try {
            scheduler.resumeAll();
        } catch (Exception e) {
            throw new UnsupportedOperationException("[Quartz] 恢复所有任务失败", e);
        }
    }

    /** 关闭所有任务 */
    @Override
    public void shutdownAllJobs() {
        try {
            if (!scheduler.isShutdown()) {
                // 需谨慎操作关闭scheduler容器
                // scheduler 生命周期结束，无法再 start() 启动scheduler
                scheduler.shutdown(true);
            }
        } catch (Exception e) {
            throw new UnsupportedOperationException("[Quartz] 关闭所有任务失败", e);
        }
    }
}