package com.sgh.demo.common.quartz.quartz.config;

import org.quartz.Scheduler;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Objects;

/**
 * Quartz 定时任务配置
 *
 * @author Song gh
 * @version 2024/01/24
 */
@Configuration
public class QuartzConfig {

    /** 配置文件路径, Resource 目录下 */
    private static final String PROP_PATH = "quartz.properties";

    @Resource
    private QuartzJobFactory jobFactory;

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
        // 获取配置属性
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource(PROP_PATH));
        // 在 quartz.properties 中的属性被读取并注入后再初始化对象
        propertiesFactoryBean.afterPropertiesSet();

        // 创建 SchedulerFactoryBean
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setQuartzProperties(Objects.requireNonNull(propertiesFactoryBean.getObject()));
        // 支持在 job 实例中注入其他的业务对象
        factory.setJobFactory(jobFactory);
        factory.setApplicationContextSchedulerContextKey("applicationContextKey");
        // spring 关闭时, 会先等待所有已经启动的 quartz job 结束, 然后再完全关闭 spring
        factory.setWaitForJobsToCompleteOnShutdown(true);
        // 是否覆盖己存在的 job
        factory.setOverwriteExistingJobs(false);
        // QuartzScheduler 延时启动, 应用启动完后 QuartzScheduler 再启动
        factory.setStartupDelay(10);

        return factory;
    }

    /** 通过 SchedulerFactoryBean 获取 Scheduler 实例 */
    @Bean(name = "scheduler")
    public Scheduler scheduler() throws IOException {
        return schedulerFactoryBean().getScheduler();
    }
}