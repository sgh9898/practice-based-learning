package com.sgh.demo.general.websocket;

/**
 * WebSocket Service
 *
 * @author Song gh on 2022/6/14.
 */
public interface WebSocketService {

    /** 向前端推送消息 */
    void pushToWs(String msg);
}
