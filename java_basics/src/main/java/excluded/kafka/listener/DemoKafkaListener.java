package excluded.kafka.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * kafka 消息监听
 * <br> 需要手动启用的项目: 1. {@link #basicListener}
 *
 * @author Song gh on 2022/4/25.
 */
@Slf4j
@Component
public class DemoKafkaListener {

    /**
     * 基础的 kafka 消息接收
     *
     * @param msg            发送时消息格式
     * @param acknowledgment 发送消息确认
     */
    @KafkaListener(id = "demoGroup", topics = {"demo-topic"}, containerFactory = "BaseContainerFactory")
    public void basicListener(String msg, Acknowledgment acknowledgment) {
        try {
            log.info("收到消息: {}, 当前时间: {}", msg, new Date());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            // 手动提交 offset
            acknowledgment.acknowledge();
        }
    }
}