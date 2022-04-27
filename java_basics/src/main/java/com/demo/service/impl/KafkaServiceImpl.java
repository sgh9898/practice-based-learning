package com.demo.service.impl;

import com.demo.service.KafkaService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Kafka Service
 *
 * @author Song gh on 2022/4/27.
 */
@Slf4j
@Service
public class KafkaServiceImpl implements KafkaService {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 异步发送消息
     *
     * @param msg 发送的消息
     */
    @Override
    public void asynchronousSend(String msg) {
        if (StringUtils.isBlank(msg)) {
            kafkaTemplate.send("demo-topic", "(default message) hello, kafka");
        } else {
            kafkaTemplate.send("demo-topic", msg);
        }
    }

    /**
     * 同步发送消息
     *
     * @param msg 发送的消息
     */
    @Override
    public String synchronousSend(String msg) {
        long start = System.currentTimeMillis();
        if (StringUtils.isBlank(msg)) {
            kafkaTemplate.send("demo-topic", "(default message) hello, kafka");
        } else {
            try {
                SendResult<String, String> result = kafkaTemplate.send("demo-topic", msg).get();
                return "耗时: " + (System.currentTimeMillis() - start) + "\n" + result.toString();
            } catch (Exception e) {
                log.error("kafka 同步发送失败", e);
            }
        }
        return "kafka 同步发送失败";
    }
}
