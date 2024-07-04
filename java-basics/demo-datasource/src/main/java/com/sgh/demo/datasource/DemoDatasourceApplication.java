package com.sgh.demo.datasource;

import com.sgh.demo.common.util.IpUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class DemoDatasourceApplication {

    public static void main(String[] args) {
        Environment env = SpringApplication.run(DemoDatasourceApplication.class, args).getEnvironment();
        IpUtils.printAtStart(env);
    }
}
