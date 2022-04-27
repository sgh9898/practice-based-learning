package com.demo.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Kafka 配置
 *
 * @author Song gh on 2022/4/18.
 */
@EnableKafka
@Configuration
public class KafkaConfig {

    @Resource
    private KafkaProperties props;

    /** 主配置, 用于创建 topic */
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> adminProps = new HashMap<>();
        // 服务器地址
        adminProps.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, this.props.getBootstrapServers());
        return new KafkaAdmin(adminProps);
    }

    /** 创建 topic */
    @Bean
    public NewTopic topic1() {
        return new NewTopic("baeldung", 1, (short) 1);
    }

    /** 消息推送配置 */
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> producerProps = new HashMap<>();

        // 通用配置
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, props.getBootstrapServers());

        // 自定义配置
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return new DefaultKafkaProducerFactory<>(producerProps);
    }

    /** 模板, 用于发送消息 */
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /** 消息接收配置 */
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> consumerProps = new HashMap<>();

        // 通用配置
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, props.getBootstrapServers());
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, props.getConsumer().getGroupId());

        // 自定义配置
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(consumerProps);
    }

    @Bean("BaseContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setAckMode(props.getListener().getAckMode());
        return factory;
    }
}
