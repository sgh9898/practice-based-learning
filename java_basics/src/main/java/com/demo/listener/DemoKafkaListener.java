package com.demo.listener;

import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * kafka 消息监听
 *
 * @author Song gh on 2022/4/25.
 */
@Component
@Slf4j
public class DemoKafkaListener {

    /**
     * 基础的 kafka 消息接收
     *
     * @param msg            发送时消息格式
     * @param acknowledgment 发送消息确认
     */
    @KafkaListener(id = "demoGroup", topics = {"demo-topic"}, containerFactory = "BaseContainerFactory")
    public void handleMessage(String msg, Acknowledgment acknowledgment) {
        try {
            log.info("收到消息: {}", msg);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            // 手动提交 offset
            acknowledgment.acknowledge();
        }
    }
}