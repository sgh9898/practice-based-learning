package com.demo.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.TopicPartition;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConsumerAwareListenerErrorHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Kafka 配置
 *
 * @author Song gh on 2023/10/11.
 */
@EnableKafka
@Configuration
public class KafkaConfig {

    /** 自动读取配置文件中 spring.kafka 内容 */
    @Resource
    private KafkaProperties kafkaProperties;

    /**
     * 自动创建 Topic, 也可以用于修改已存在的同名 Topic (数据不会丢失)
     * 多个 Topic 需要复制本方法并分别配置
     */
    @Bean
    public NewTopic createTopic() {
        return new NewTopic("new_topic_name", 10, (short) 1);
    }

    /**
     * 自定义的 listener 配置 (通常并不需要使用自定义配置, 常规参数在配置文件中调整即可)
     * <br>使用时在监听方法上标注 @KafkaListener(containerFactory = "customizedContainerFactory")
     * <br>自定义方法多用于[强制锁定/动态修改]部分参数
     */
    @Bean("customizedContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<?, ?> customizedContainerFactory(ConcurrentKafkaListenerContainerFactoryConfigurer configurer) {
        // 读取 spring.kafka 配置
        Map<String, Object> properties = kafkaProperties.buildConsumerProperties();
        // 可以在此修改 kafka 配置 -- properties

        // 读取 spring.kafka.consumer 配置
        Map<String, Object> consumerProperties = kafkaProperties.getConsumer().buildProperties();
        properties.putAll(consumerProperties);
        // 可以在此修改 consumer 配置 -- consumerProperties

        // 应用修改后的配置
        ConsumerFactory<Object, Object> kafkaConsumerFactory = new DefaultKafkaConsumerFactory<>(properties);
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, kafkaConsumerFactory);
        return factory;
    }

    /**
     * 自定义的 listener 异常处理 (通常并不需要使用自定义配置)
     * <br>使用时在监听方法上标注 @KafkaListener(errorHandler = "customizedListenerErrorHandler")
     * <br>默认的异常处理器会在失败 9 次后自动提交 {@link DefaultErrorHandler}
     */
    @SuppressWarnings("unchecked")
    @Bean(name = "customizedListenerErrorHandler")
    public ConsumerAwareListenerErrorHandler customizedListenerErrorHandler() {
        return (m, e, c) -> {
            // 获取信息
            MessageHeaders headers = m.getHeaders();
            List<String> topics = headers.get(KafkaHeaders.RECEIVED_TOPIC, List.class);
            List<Integer> partitions = headers.get(KafkaHeaders.RECEIVED_PARTITION_ID, List.class);
            List<Long> offsets = headers.get(KafkaHeaders.OFFSET, List.class);
            Map<TopicPartition, Long> offsetsToReset = new HashMap<>();
            // 确保出现异常的消息不会被自动提交
            for (int i = 0; i < Objects.requireNonNull(topics).size(); i++) {
                int index = i;
                offsetsToReset.compute(new TopicPartition(topics.get(i), Objects.requireNonNull(partitions).get(i)),
                        (k, v) -> v == null ? Objects.requireNonNull(offsets).get(index) : Math.min(v, Objects.requireNonNull(offsets).get(index)));
            }
            offsetsToReset.forEach(c::seek);
            return null;
        };
    }
}
