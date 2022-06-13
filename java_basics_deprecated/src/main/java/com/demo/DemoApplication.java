package com.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.oas.annotations.EnableOpenApi;

/**
 * Demo 应用, 包含通用功能或演示
 *
 * @author Song gh on 2022/1/24.
 */
@Slf4j
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        Environment env = SpringApplication.run(DemoApplication.class, args).getEnvironment();
        log.info("--------------------------------------------------\n" +
                        "  Swagger-UI: http://localhost:{}{}/swagger-ui/index.html\n" +
                        "  Profile(s): {}\n" +
                        "--------------------------------------------------",
                env.getProperty("server.port"),
                env.getProperty("server.servlet.context-path"),
                env.getActiveProfiles());
    }
}
