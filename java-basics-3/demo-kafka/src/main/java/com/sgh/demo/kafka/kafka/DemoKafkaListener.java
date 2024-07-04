package com.sgh.demo.kafka.kafka;

import com.sgh.demo.common.database.db.entity.DemoEntity;
import com.sgh.demo.common.util.JsonUtils;
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
    @KafkaListener(id = "demoBatchListener", groupId = "demoKafkaGroup", topics = "demoKafkaTopic", errorHandler = "batchKafkaListenerErrorHandler")
    public void batchListener(List<String> msg, Acknowledgment acknowledgment) {
        log.info("监听到批量消息, {}", msg);
        List<DemoEntity> entityList = new LinkedList<>();
        msg.forEach(str -> entityList.add(JsonUtils.jsonToBean(str, DemoEntity.class)));
        log.info("批量消息转换: " + JsonUtils.beanToJson(entityList));
//        acknowledgment.acknowledge();
//        throw new UnsupportedOperationException("这是一条批量报错信息");
    }

//    /**
//     * kafka 接收信息
//     *
//     * @param msg            发送时消息格式
//     * @param acknowledgment 发送消息确认
//     */
//    @KafkaListener(id = "demoSingleListener", groupId = "demoKafkaGroup", topics = "demoKafkaTopic", errorHandler = "singleKafkaListenerErrorHandler")
//    public void singleListener(String msg, Acknowledgment acknowledgment) {
//        log.info("监听到单条消息, {}", msg);
////        acknowledgment.acknowledge();
////        throw new UnsupportedOperationException("这是一条单条报错信息");
//    }
}
