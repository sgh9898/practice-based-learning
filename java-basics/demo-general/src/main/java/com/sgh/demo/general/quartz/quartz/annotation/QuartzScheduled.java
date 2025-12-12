package com.sgh.demo.general.quartz.quartz.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * [注解] Quartz 任务调度
 *
 * @author Song gh
 * @since 2025/12/2
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface QuartzScheduled {

    /** 任务名称, 通常需要全局唯一 */
    String jobName() default "";

    /** 任务组名称, 同组任务可以统一管理 */
    String jobGroup() default "";

    /** 时间表达式
     * @see org.quartz.CronExpression */
    String cronExpression();

    /** 任务描述 */
    String description() default "";
}