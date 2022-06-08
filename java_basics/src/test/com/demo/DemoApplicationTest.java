package com.demo;

import com.demo.database.entity.DemoEntity;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * Spring 相关测试
 *
 * @author Song gh on 2022/5/6.
 */
@SpringBootTest
class DemoApplicationTest {

    @Resource
    RedisProperties properties;

    @Test
    void main1() {
        DemoEntity demoEntity = new DemoEntity();
        demoEntity.setName("111");
    }
}