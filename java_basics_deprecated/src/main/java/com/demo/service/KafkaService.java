package com.demo.service;

/**
 * Kafka Service
 *
 * @author Song gh on 2022/4/27.
 */
public interface KafkaService {

    void asynchronousSend(String msg);

    String synchronousSend(String msg);
}
