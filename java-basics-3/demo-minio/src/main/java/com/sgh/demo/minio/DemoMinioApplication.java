package com.sgh.demo.minio;

import com.sgh.demo.common.util.IpUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

/**
 * Demo 应用, 包含通用功能或演示
 *
 * @author Song gh
 * @version 2024/3/21
 */
@SpringBootApplication
public class DemoMinioApplication {

    public static void main(String[] args) {
        Environment env = SpringApplication.run(DemoMinioApplication.class, args).getEnvironment();
        IpUtils.printAtStart(env);
    }
}
