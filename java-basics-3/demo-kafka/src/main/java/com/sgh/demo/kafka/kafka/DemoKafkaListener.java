package com.sgh.demo.kafka.kafka;

import com.sgh.demo.common.database.db.entity.DemoEntity;
import com.sgh.demo.common.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

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
     * kafka 接收单条信息
     *
     * @param msg            单条消息内容
     * @param acknowledgment 消息确认
     * @param topic          消息所属 topic
     * @param partition      消息所属分区
     * @param timestamp      消息时间戳
     */
    @KafkaListener(id = "demoSingleListener", groupId = "demoKafkaGroup",
            topics = {"demoKafkaTopic1", "demoKafkaTopic2"}, errorHandler = "singleKafkaListenerErrorHandler")
    public void singleListener(String msg, Acknowledgment acknowledgment,
                               @Header(KafkaHeaders.GROUP_ID) String groupId,
                               @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                               @Header(KafkaHeaders.RECEIVED_PARTITION) String partition,
                               @Header(KafkaHeaders.RECEIVED_TIMESTAMP) String timestamp) {
        log.info("监听到单条消息, {}", msg);
        try {
            JsonUtils.jsonToBean(msg, DemoEntity.class);
        } catch (Exception e) {
            throw new UnsupportedOperationException("这是单条报错信息", e);
        }
        acknowledgment.acknowledge();
        log.info("[Kafka 单条数据监听记录] 成功处理, 所属消费者分组: {}, 所属 topic: {}", groupId, topic);
    }

    /**
     * kafka 接收批量信息
     *
     * @param msgList        批量消息内容
     * @param acknowledgment 消息确认
     * @param topicList      消息所属 topic
     * @param partitionList  消息所属分区
     * @param timestampList  消息时间戳
     */
    @KafkaListener(id = "demoBatchListener", groupId = "demoKafkaGroup",
            topics = {"demoKafkaTopic1", "demoKafkaTopic2"}, errorHandler = "batchResetKafkaListenerErrorHandler")
    public void batchListener(List<String> msgList, Acknowledgment acknowledgment,
                              @Header(KafkaHeaders.GROUP_ID) String groupId,
                              @Header(KafkaHeaders.RECEIVED_TOPIC) Set<String> topicList,
                              @Header(KafkaHeaders.RECEIVED_PARTITION) List<String> partitionList,
                              @Header(KafkaHeaders.RECEIVED_TIMESTAMP) List<String> timestampList) {
        log.info("监听到批量消息, {}", msgList);
        try {
            for (String msg : msgList) {
                JsonUtils.jsonToBean(msg, DemoEntity.class);
            }
        } catch (Exception e) {
            throw new UnsupportedOperationException("这是批量报错信息", e);
        }
        acknowledgment.acknowledge();
        log.info("[Kafka 批量数据监听记录] 成功处理 {} 条消息, 所属消费者分组: {}, 所属 topic: {}", msgList.size(), groupId, topicList);
    }
}
