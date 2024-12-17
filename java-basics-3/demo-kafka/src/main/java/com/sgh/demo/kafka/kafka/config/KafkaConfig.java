package com.sgh.demo.kafka.kafka.config;

import com.sgh.demo.common.util.DateUtils;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.listener.ConsumerAwareListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import java.util.*;

/**
 * Kafka 配置
 *
 * @author Song gh
 * @version 2024/12/13
 */
@Slf4j
@EnableKafka
@Configuration
public class KafkaConfig {

    /**
     * 自动创建 Topic
     * <pre>
     * 1. 主要用于指定 partition(分区) 与 replication factor(复制)
     * 2. 也可以用于修改已存在的同名 topic (数据不会丢失)
     * 3. 创建多个 topic 需要配置多个不同名的本方法, 保证返回类型为 NewTopic 即可
     * </pre>
     */
    @Bean
    public NewTopic createTopic() {
        return new NewTopic("demoKafkaTopic1", 10, (short) 1);
    }

    @Bean
    public NewTopic createTopic2() {
        return new NewTopic("demoKafkaTopic2", 9, (short) 1);
    }

    /** [单条监听模式] Kafka 监听报错处理 */
    @Bean
    public ConsumerAwareListenerErrorHandler singleKafkaListenerErrorHandler() {
        return createSingleErrorHandler(false);
    }

    /** [单条监听模式] Kafka 监听报错处理, 会立即重置 offset 至报错前的节点 */
    @Bean
    public ConsumerAwareListenerErrorHandler singleResetKafkaListenerErrorHandler() {
        return createSingleErrorHandler(true);
    }

    /** [批量监听模式] Kafka 监听报错处理 */
    @Bean
    public ConsumerAwareListenerErrorHandler batchKafkaListenerErrorHandler() {
        return createBatchErrorHandler(false);
    }

    /** [批量监听模式] Kafka 监听报错处理, 会立即重置 offset 至报错前的节点 */
    @Bean
    public ConsumerAwareListenerErrorHandler batchResetKafkaListenerErrorHandler() {
        return createBatchErrorHandler(true);
    }

// ------------------------------ Private ------------------------------

    /**
     * [单条监听模式] 创建 Kafka 报错处理器
     *
     * @param resetOffset 是否立即重置 offset
     */
    private ConsumerAwareListenerErrorHandler createSingleErrorHandler(boolean resetOffset) {
        return (message, exception, consumer) -> {
            // 获取报错信息
            MessageHeaders headers = message.getHeaders();
            String topic = headers.get(KafkaHeaders.RECEIVED_TOPIC, String.class);
            Integer partition = headers.get(KafkaHeaders.RECEIVED_PARTITION, Integer.class);
            Long offset = headers.get(KafkaHeaders.OFFSET, Long.class);
            Long timestamp = headers.get(KafkaHeaders.RECEIVED_TIMESTAMP, Long.class);
            assert partition != null;
            assert offset != null;
            assert timestamp != null;
            String dateStr = DateUtils.dateToStr(new Date(timestamp));

            // 记录报错信息
            log.error("[Kafka 监听报错] 消息内容: {},\n所属消费者分组: {},\n所属 topic: {},\n所属分区: {},\n消息接收时间: {}",
                    message.getPayload(), consumer.groupMetadata().groupId(), topic, partition, dateStr, exception);

            // 立即重置 offset 至报错前的节点, 而不是在重启时回退
            if (resetOffset) {
                consumer.seek(new TopicPartition(topic, partition), offset);
            }

            return null;
        };
    }

    /**
     * [批量监听模式] 创建 Kafka 报错处理器
     *
     * @param resetOffset 是否立即重置 offset
     */
    @SuppressWarnings({"unchecked"})
    private ConsumerAwareListenerErrorHandler createBatchErrorHandler(boolean resetOffset) {
        return new ConsumerAwareListenerErrorHandler() {
            @Override
            @Nonnull
            public Object handleError(@Nonnull Message<?> message, @Nonnull ListenerExecutionFailedException exception, @Nonnull Consumer<?, ?> consumer) {
                // 获取报错信息
                MessageHeaders headers = message.getHeaders();
                List<String> topicList = headers.get(KafkaHeaders.RECEIVED_TOPIC, List.class);
                List<Integer> partitionList = headers.get(KafkaHeaders.RECEIVED_PARTITION, List.class);
                List<Long> offsetList = headers.get(KafkaHeaders.OFFSET, List.class);
                List<Long> timestampList = headers.get(KafkaHeaders.RECEIVED_TIMESTAMP, List.class);
                // 报错时间需要格式转换
                List<String> dateStrList = new LinkedList<>();
                if (timestampList != null) {
                    for (Long timestamp : timestampList) {
                        dateStrList.add(DateUtils.dateToStr(new Date(timestamp)));
                    }
                }

                // 记录报错信息
                log.error("[Kafka 监听报错] 消息内容: {},\n所属消费者分组: {},\n所属 topic: {},\n所属分区: {},\n消息接收时间: {}",
                        message.getPayload(), consumer.groupMetadata().groupId(), topicList, partitionList, dateStrList, exception);

                // 立即重置 offset 至报错前的节点, 而不是在重启时回退
                if (resetOffset) {
                    Map<TopicPartition, Long> offsetsToReset = new HashMap<>();
                    for (int topicIdx = 0; topicIdx < Objects.requireNonNull(topicList).size(); topicIdx++) {
                        int index = topicIdx;
                        offsetsToReset.compute(new TopicPartition(topicList.get(topicIdx), Objects.requireNonNull(partitionList).get(topicIdx)),
                                (topicPartition, offset) ->
                                        offset == null ? Objects.requireNonNull(offsetList).get(index) : Math.min(offset, Objects.requireNonNull(offsetList).get(index)));
                    }
                    offsetsToReset.forEach(consumer::seek);
                }

                return null;
            }
        };
    }
}
