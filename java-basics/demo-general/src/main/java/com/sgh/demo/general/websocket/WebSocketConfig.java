package com.sgh.demo.general.websocket;

import com.sgh.demo.general.config.Constants;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket 推送消息配置
 *
 * @author Song gh on 2022/6/10.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /** 允许跨域访问 */
    private static final String CROSS_ORIGIN = "http://172.16.30.*:*";

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 向前端推送地址的前缀
        // e.g. 对应 Controller 注解 @SendTo("/push/demo")
        config.enableSimpleBroker(Constants.WS_PUSH_PREFIX);
        // 后端接收消息地址的前缀
        // e.g. 对应 Controller 注解 @MessageMapping("/demo"), 完整地址为 /receive/demo
        config.setApplicationDestinationPrefixes(Constants.WS_RECEIVE_PREFIX);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 前端连接的端点, setAllowedOriginPatterns 控制跨域权限
        registry.addEndpoint(Constants.WS_ENDPOINT)
                .setAllowedOriginPatterns(CROSS_ORIGIN).withSockJS();
    }
}