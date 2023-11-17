package com.demo;

import com.demo.db.repository.DemoEntityRepository;
import com.demo.kafka.KafkaProducerUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

/**
 * Spring 相关测试
 *
 * @author Song gh on 2022/5/6.
 */
@SpringBootTest
class DemoApplicationTest {

    @Resource
    private KafkaProducerUtils kafkaService;
    @Resource
    private DemoEntityRepository demoEntityRepository;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void mainTest() throws InterruptedException {
    }

    @Test
    void test1() throws InterruptedException {
        boolean result = stringRedisTemplate.hasKey("testKey");
        if (!result) {
            System.out.println("1111");
        }
    }
}