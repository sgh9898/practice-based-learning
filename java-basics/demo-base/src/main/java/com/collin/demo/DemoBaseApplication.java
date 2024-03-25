package com.collin.demo;

import com.collin.demo.util.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Objects;

/**
 * Demo 应用, 包含通用功能或演示
 *
 * @author Song gh on 2022/1/24.
 */
@Slf4j
@EnableAsync
@EnableScheduling
@EnableFeignClients
@SpringBootApplication
//@EnableJpaRepositories("com.demo.database")   // 解决 JPA 与 ElasticSearch 冲突
public class DemoBaseApplication {

    public static void main(String[] args) {
        Environment env = SpringApplication.run(DemoBaseApplication.class, args).getEnvironment();
        // 仅在开发环境展示 swagger
        if (Objects.equals(env.getActiveProfiles()[0], "dev")) {
            String ip = IpUtils.getRealIp();
            log.info("\n--------------------------------------------------\n" +
                            "  Swagger(local):   http://localhost:{}{}/swagger-ui.html\n" +
                            "  Swagger(network): http://{}:{}{}/swagger-ui.html\n" +
                            "  Active Profiles:  {}\n" +
                            "--------------------------------------------------",
                    env.getProperty("server.port"),
                    env.getProperty("server.servlet.context-path"),

                    ip,
                    env.getProperty("server.port"),
                    env.getProperty("server.servlet.context-path"),

                    env.getActiveProfiles());
        } else {
            log.info("\n--------------------------------------------------" +
                    "\n                    启动成功" +
                    "\n--------------------------------------------------");
        }
    }
}
