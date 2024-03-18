package com.demo.kafka;

import com.demo.database.db.entity.DemoEntity;
import com.demo.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * Kafka 监听
 *
 * @author Song gh
 * @version 2024/3/18
 */
@Slf4j
@Component
public class DemoKafkaListener {

    /**
     * kafka 接收信息
     *
     * @param msg            发送时消息格式
     * @param acknowledgment 发送消息确认
     */
    @KafkaListener(id = "demoBatchListener", groupId = "demoKafkaGroup", topics = "demoRegion", errorHandler = "kafkaListenerErrorHandler")
    public void batchListener(List<String> msg, Acknowledgment acknowledgment) {
        log.info("监听到批量消息, {}", msg);
        List<DemoEntity> entityList = new LinkedList<>();
        msg.forEach(str -> entityList.add(JsonUtils.jsonToBean(str, DemoEntity.class)));
        throw new RuntimeException("这是一条批量报错信息");
    }

    /**
     * kafka 接收信息
     *
     * @param msg            发送时消息格式
     * @param acknowledgment 发送消息确认
     */
    @KafkaListener(id = "demoSingleListener", groupId = "demoKafkaGroup", topics = "demoRegion", errorHandler = "kafkaListenerErrorHandler")
    public void singleListener(String msg, Acknowledgment acknowledgment) {
        log.info("监听到单条消息, {}", msg);
        throw new RuntimeException("这是一条单条报错信息");
    }
}
