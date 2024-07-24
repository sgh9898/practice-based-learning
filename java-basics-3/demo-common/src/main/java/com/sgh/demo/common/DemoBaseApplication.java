package com.sgh.demo.common;

import com.sgh.demo.common.util.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Demo 应用, 包含通用功能或演示
 *
 * @author Song gh
 * @version 2024/3/21
 */
@Slf4j
@EnableAsync
@EnableScheduling
@SpringBootApplication
//@EnableJpaRepositories("com.sgh.demo.general.database")   // 解决 JPA 与 ElasticSearch 冲突
public class DemoBaseApplication {

    public static void main(String[] args) {
        Environment env = SpringApplication.run(DemoBaseApplication.class, args).getEnvironment();
        IpUtils.printAtStart(env);
    }
}
