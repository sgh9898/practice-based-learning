package com.collin.sharding.demo;

import com.collin.sharding.demo.util.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.transaction.jta.JtaAutoConfiguration;
import org.springframework.core.env.Environment;

import java.util.Objects;

/**
 * 主应用启动
 *
 * @author Song gh
 * @version 2024/3/21
 */
@Slf4j
@SpringBootApplication(exclude = JtaAutoConfiguration.class)
public class DemoShardingJdbcApplication {

    public static void main(String[] args) {
        Environment env = SpringApplication.run(DemoShardingJdbcApplication.class, args).getEnvironment();
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
