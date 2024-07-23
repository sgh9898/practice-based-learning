package com.sgh.demo.general.config;

/**
 * 常量
 *
 * @author Song gh on 2022/6/11.
 */
public interface Constants {

    // Websocket 配置
    String WS_ENDPOINT = "/demo";
    String WS_PUSH_PREFIX = "/push";
    String WS_RECEIVE_PREFIX = "/receive";

    // Websocket topics
    String WS_PUSH_DATA = "/push/data";
}
