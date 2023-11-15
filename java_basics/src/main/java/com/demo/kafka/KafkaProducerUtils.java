package com.demo.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Kafka Producer 工具类
 *
 * @author Song gh on 2023/10/8.
 */
@Slf4j
@Component
public class KafkaProducerUtils {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 同步发送消息
     *
     * @param topic   消息所属的 topic
     * @param message 消息内容
     */
    public void syncSend(String topic, String message) {
        try {
            kafkaTemplate.send(topic, message).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Kafka 消息同步发送失败, 消息: {}\n报错: {}", message, e.getMessage());
        }
    }

    /**
     * 同步发送消息
     *
     * @param topic   消息所属的 topic
     * @param key     消息所属的 key
     * @param message 消息内容
     */
    public void syncSend(String topic, String key, String message) {
        try {
            kafkaTemplate.send(topic, message).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Kafka 消息同步发送失败, 消息: {}\n报错: {}", message, e.getMessage());
        }
    }

    /**
     * 异步发送消息并回调
     *
     * @param topic   消息所属的 topic
     * @param message 消息内容
     */
    public void sendWithCallback(String topic, String message) {
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, message).completable();
        // 回调, 记录报错
        future.whenCompleteAsync((result, e) -> {
            if (e != null) {
                log.error("Kafka 消息发送失败, 消息: {}\n报错: {}", message, e.getMessage());
            }
        });
    }

    /**
     * 异步发送消息并回调
     *
     * @param topic   消息所属的 topic
     * @param key     消息所属的 key
     * @param message 消息内容
     */
    public void sendWithCallback(String topic, String key, String message) {
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, key, message).completable();
        // 回调, 记录报错
        future.whenCompleteAsync((result, e) -> {
            if (e != null) {
                log.error("Kafka 消息发送失败, 消息: {}\n报错: {}", message, e.getMessage());
            }
        });
    }
}
