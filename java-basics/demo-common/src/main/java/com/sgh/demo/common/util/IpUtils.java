package com.sgh.demo.common.util;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Objects;

/**
 * IP 工具类
 *
 * @author Song gh
 * @version 2023/10/10
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IpUtils {

    /** 应用启动时展示的信息 */
    public static void printAtStart(Environment env) {
        // 仅在开发环境展示 swagger 信息
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

    /** 获取本机内网真实 ip */
    public static String getRealIp() {
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
            throw new UnsupportedOperationException(e);
        }
    }
}
