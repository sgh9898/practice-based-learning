package excluded.kafka;

import excluded.kafka.KafkaService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

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
        String data;
        data = "发送时间: " + new Date() + " [Async] " + (StringUtils.isBlank(msg) ? "Kafka 默认消息" : msg);
        kafkaTemplate.send("demo-topic", data);
    }

    /**
     * 同步发送消息
     *
     * @param msg 发送的消息
     */
    @Override
    public String synchronousSend(String msg) {
        String data;
        data = "发送时间: " + new Date() + " [Sync] " + (StringUtils.isBlank(msg) ? "Kafka 默认消息" : msg);

        // 发送消息并计算耗时
        long start = System.currentTimeMillis();
        try {
            SendResult<String, String> result = kafkaTemplate.send("demo-topic", data).get();
            log.debug("Kafka 同步发送耗时: {}, \n结果: {}", (System.currentTimeMillis() - start), result.toString());
        } catch (Exception e) {
            log.error("kafka 同步发送失败", e);
        }
        return "Kafka Sync 耗时: " + (System.currentTimeMillis() - start);
    }
}
