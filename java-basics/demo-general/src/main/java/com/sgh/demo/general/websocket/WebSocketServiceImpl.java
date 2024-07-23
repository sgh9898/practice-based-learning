package com.sgh.demo.general.websocket;

import com.sgh.demo.general.config.Constants;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * WebSocket Service
 *
 * @author Song gh on 2022/6/14.
 */
@Service
public class WebSocketServiceImpl implements WebSocketService {

    @Resource
    private SimpMessagingTemplate wsTemplate;

    /** 向前端推送消息 */
    @Override
    public void pushToWs(String msg) {
        wsTemplate.convertAndSend(Constants.WS_PUSH_PREFIX + "demo", msg);
    }

}
