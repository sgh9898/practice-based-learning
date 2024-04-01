package com.sgh.demo.canal;

import com.collin.demo.common.util.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

/**
 * 主应用启动
 *
 * @author Song gh
 * @version 2024/3/21
 */
@Slf4j
@SpringBootApplication
public class DemoCanalApplication {

    public static void main(String[] args) {
        Environment env = SpringApplication.run(DemoCanalApplication.class, args).getEnvironment();
        IpUtils.printAtStart(env);
    }
}
