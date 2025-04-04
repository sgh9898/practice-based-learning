package com.sgh.demo.common.websocket;

import com.sgh.demo.common.config.Constants;
import jakarta.annotation.Resource;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * WebSocket Service
 *
 * @author Song gh on 2022/6/14.
 */
@Service
public class WebSocketServiceImpl implements com.sgh.demo.common.websocket.WebSocketService {

    @Resource
    private SimpMessagingTemplate wsTemplate;

    /** 向前端推送消息 */
    @Override
    public void pushToWs(String msg) {
        wsTemplate.convertAndSend(Constants.WS_PUSH_PREFIX + "demo", msg);
    }

}
