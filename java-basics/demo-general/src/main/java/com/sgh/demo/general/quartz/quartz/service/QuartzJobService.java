package com.sgh.demo.general.quartz.quartz.service;

import java.util.Map;

/**
 * Quartz 定时任务
 *
 * @author Song gh
 * @version 2024/01/24
 */
public interface QuartzJobService {

    /**
     * [新增/更新] 添加任务并启动(任务已存在时转为更新)
     * <br> 任务严格遵照设定时间执行, 不会在启动时执行一次
     *
     * @param clazzName 定时任务 class 名称, 需要 implements {@link org.quartz.Job}
     * @param jobName   定时任务名
     * @param groupName 定时任务组名
     * @param cronExp   时间表达式, {@link org.quartz.CronExpression}
     * @param jobParams 向定时任务传递的参数
     */
    void upsertJob(String clazzName, String jobName, String groupName, String cronExp, Map<String, Object> jobParams);

    /**
     * [新增] 添加任务并启动(任务已存在时不做任何操作)
     * <br> 任务严格遵照设定时间执行, 不会在启动时额外执行一次
     *
     * @param clazzName 定时任务 class 名称, 需要 implements {@link org.quartz.Job}
     * @param jobName   定时任务名
     * @param groupName 定时任务组名
     * @param cronExp   时间表达式, {@link org.quartz.CronExpression}
     * @param jobParams 向定时任务传递的参数
     */
    void addJobIfNotExists(String clazzName, String jobName, String groupName, String cronExp, Map<String, Object> jobParams);

    /**
     * [更新] 更新任务
     * <br> 任务严格遵照设定时间执行, 不会在启动时额外执行一次
     *
     * @param jobName   定时任务名
     * @param groupName 定时任务组名
     * @param cronExp   时间表达式, {@link org.quartz.CronExpression}
     * @param jobParams 向定时任务传递的参数
     */
    void updateJob(String jobName, String groupName, String cronExp, Map<String, Object> jobParams);

    /**
     * 任务是否存在
     *
     * @param jobName   定时任务名
     * @param groupName 定时任务组名
     */
    boolean checkJobExists(String jobName, String groupName);

    /**
     * 删除任务
     *
     * @param jobName   定时任务名
     * @param groupName 定时任务组名
     */
    void deleteJob(String jobName, String groupName);

    /**
     * 暂停任务
     *
     * @param jobName   定时任务名
     * @param groupName 定时任务组名
     */
    void pauseJob(String jobName, String groupName);

    /**
     * 恢复任务
     *
     * @param jobName   定时任务名
     * @param groupName 定时任务组名
     */
    void resumeJob(String jobName, String groupName);

    /**
     * 立即运行一次定时任务
     *
     * @param jobName   定时任务名
     * @param groupName 定时任务组名
     */
    void runOnce(String jobName, String groupName);

    /** 启动所有任务 */
    void startAllJobs();

    /** 暂停所有任务 */
    void pauseAllJobs();

    /** 恢复所有任务 */
    void resumeAllJobs();

    /** 关闭所有任务 */
    void shutdownAllJobs();
}