package com.sgh.demo.general;

import com.sgh.demo.common.util.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.Nullable;

/**
 * Demo 应用, 包含通用功能或演示
 * <pre>
 *     1. 引入其他功能模块时, 可能需要手动编辑 spring 自动装配的范围:
 *   {@link EntityScan}, {@link EnableJpaRepositories}, {@link SpringBootApplication#scanBasePackages()} </pre>
 *
 * @author Song gh
 * @version 2024/3/21
 */
@Nullable
@Slf4j
@EnableAsync
@EnableScheduling
@SpringBootApplication
//@EnableJpaRepositories("com.sgh.demo.general.database")   // 解决 JPA 与 ElasticSearch 冲突
public class DemoGeneralApplication {

    public static void main(String[] args) {
        Environment env = SpringApplication.run(DemoGeneralApplication.class, args).getEnvironment();
        IpUtils.printAtStart(env);
    }
}
