//package com.sgh.demo.kafka.kafka.config;
//
//import jakarta.annotation.Resource;
//import org.apache.kafka.clients.admin.NewTopic;
//import org.apache.kafka.common.TopicPartition;
//import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
//import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.annotation.EnableKafka;
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
//import org.springframework.kafka.core.ConsumerFactory;
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
//import org.springframework.kafka.listener.ConsumerAwareListenerErrorHandler;
//import org.springframework.kafka.support.KafkaHeaders;
//import org.springframework.messaging.MessageHeaders;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//
/// **
// * Kafka 配置
// *
// * @author Song gh
// * @version 2024/3/14
// */
//@EnableKafka
//@Configuration
//public class KafkaConfigBefore {
//
//    /** 自动读取配置文件中 spring.kafka 内容 */
//    @Resource
//    private KafkaProperties kafkaProperties;
//
//    /**
//     * 自动创建 Topic
//     * <pr>
//     * 1. 主要用于指定 partition(分区) 与 replication factor(复制)
//     * 2. 也可以用于修改已存在的同名 topic (数据不会丢失)
//     * 3. 创建多个 topic 需要配置多个不同名的本方法
//     * </pr>
//     */
//    @Bean
//    public NewTopic createTopic() {
//        return new NewTopic("new_kafka_topic_name", 10, (short) 1);
//    }
//
//    @Bean
//    public NewTopic createTopic2() {
//        return new NewTopic("new_kafka_topic_name2", 9, (short) 1);
//    }
//
//    /** 默认的 Listener 配置 (常规参数在配置文件中调整即可) */
//    @Bean("kafkaListenerContainerFactory")
//    public ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(ConcurrentKafkaListenerContainerFactoryConfigurer configurer) {
//        // 读取 spring.kafka 配置, 可以在此修改 kafka 配置
//        Map<String, Object> properties = kafkaProperties.buildConsumerProperties();
//
//        // 读取 spring.kafka.consumer 配置, 可以在此修改 consumer 配置
//        KafkaProperties.Consumer consumer = kafkaProperties.getConsumer();
//        properties.putAll(consumer.buildProperties());
//
//        // 应用修改后的配置
//        ConsumerFactory<Object, Object> kafkaConsumerFactory = new DefaultKafkaConsumerFactory<>(properties);
//        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        configurer.configure(factory, kafkaConsumerFactory);
//        return factory;
//    }
//
//    /**
//     * 默认的单条数据异常处理, 会将 offset 回退
//     */
//    @Bean(name = "singleKafkaListenerErrorHandler")
//    public ConsumerAwareListenerErrorHandler singleKafkaListenerErrorHandler() {
//        return (m, e, c) -> {
//            MessageHeaders headers = m.getHeaders();
//            c.seek(new TopicPartition(
//                            headers.get(KafkaHeaders.RECEIVED_TOPIC, String.class),
//                            headers.get(KafkaHeaders.RECEIVED_PARTITION_ID, Integer.class)),
//                    headers.get(KafkaHeaders.OFFSET, Long.class));
//            return null;
//        };
//    }
//
//    /**
//     * 默认的批量数据异常处理, 会将 offset 回退
//     */
//    @SuppressWarnings("unchecked")
//    @Bean(name = "batchKafkaListenerErrorHandler")
//    public ConsumerAwareListenerErrorHandler batchKafkaListenerErrorHandler() {
//        return (m, e, c) -> {
//            MessageHeaders headers = m.getHeaders();
//            List<String> topics = headers.get(KafkaHeaders.RECEIVED_TOPIC, List.class);
//            List<Integer> partitions = headers.get(KafkaHeaders.RECEIVED_PARTITION_ID, List.class);
//            List<Long> offsets = headers.get(KafkaHeaders.OFFSET, List.class);
//            Map<TopicPartition, Long> offsetsToReset = new HashMap<>();
//            // 确保出现异常的消息不会被自动提交
//            for (int i = 0; i < Objects.requireNonNull(topics).size(); i++) {
//                int index = i;
//                offsetsToReset.compute(new TopicPartition(topics.get(i), Objects.requireNonNull(partitions).get(i)),
//                        (k, v) -> v == null ? Objects.requireNonNull(offsets).get(index) : Math.min(v, Objects.requireNonNull(offsets).get(index)));
//            }
//            offsetsToReset.forEach(c::seek);
//            return null;
//        };
//    }
//}
