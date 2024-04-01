package com.collin.demo.common.util;


import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

import javax.servlet.http.HttpServletRequest;
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

    /**
     * 获取 IP 地址
     * <pre>
     * 使用 Nginx 等反向代理软件, 则不能通过 request.getRemoteAddr() 获取IP地址
     * 如果使用了多级反向代理的话, X-Forwarded-For的值并不止一个, 而是一串IP地址, X-Forwarded-For 中第一个非 unknown 的有效 IP 字符串, 则为真实 IP 地址</pre>
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip;
        try {
            ip = request.getHeader("x-forwarded-for");
            boolean validIp = ip != null && !ip.isEmpty();
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            if (validIp && !"unknown".equalsIgnoreCase(ip) && (ip.contains(","))) {
                ip = ip.split(",")[0];
            }
            if (!validIp || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (!validIp || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (!validIp || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (!validIp || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (!validIp || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("X-Real-IP");
            }
            if (!validIp || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            // 对于通过多个代理的情况, 第一个 ip 为客户端真实 ip, 多个 ip 逗号分割
            if (ip != null && ip.length() > 15 && ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        } catch (Exception e) {
            throw new UnsupportedOperationException("获取ip失败");
        }

        return ip;
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

    private IpUtils() {
    }
}
