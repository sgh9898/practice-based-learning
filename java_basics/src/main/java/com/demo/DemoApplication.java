package com.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
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
public class DemoApplication {

    public static void main(String[] args) {
        Environment env = SpringApplication.run(DemoApplication.class, args).getEnvironment();
        // 仅在开发环境展示 swagger
        if (Objects.equals(env.getActiveProfiles()[0], "dev")) {
            String ip = getRealIp();
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
                    "\n  启动成功" +
                    "\n--------------------------------------------------");
        }
    }

    /** 获取本机真实 ip */
    private static String getRealIp() {
        try {
            Enumeration<NetworkInterface> networkEnum = NetworkInterface.getNetworkInterfaces();
            while (networkEnum.hasMoreElements()) {
                NetworkInterface currNetwork = networkEnum.nextElement();
                Enumeration<InetAddress> inetEnum = currNetwork.getInetAddresses();
                while (inetEnum.hasMoreElements()) {
                    InetAddress currInet = inetEnum.nextElement();
                    if (!currInet.isLoopbackAddress() && currInet.isSiteLocalAddress()) {
                        return currInet.getHostAddress();
                    }
                }
            }
            return InetAddress.getLocalHost().getHostAddress();
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
