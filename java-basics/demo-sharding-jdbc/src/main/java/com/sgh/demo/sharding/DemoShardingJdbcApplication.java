package com.sgh.demo.sharding;

import com.sgh.demo.common.util.IpUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.transaction.jta.JtaAutoConfiguration;
import org.springframework.core.env.Environment;

/**
 * 主应用启动
 *
 * @author Song gh
 * @version 2024/3/21
 */
@SpringBootApplication(exclude = JtaAutoConfiguration.class)
public class DemoShardingJdbcApplication {

    public static void main(String[] args) {
        Environment env = SpringApplication.run(DemoShardingJdbcApplication.class, args).getEnvironment();
        IpUtils.printAtStart(env);
    }
}
