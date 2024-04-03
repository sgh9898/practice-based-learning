package com.sgh.demo.kafka;

import com.collin.demo.common.util.IpUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

/**
 * 主应用启动
 *
 * @author Song gh
 * @version 2024/3/21
 */
@SpringBootApplication
public class DemoKafkaApplication {

    public static void main(String[] args) {
        Environment env = SpringApplication.run(DemoKafkaApplication.class, args).getEnvironment();
        IpUtils.printAtStart(env);
    }

}
