package com.demo;

import com.demo.db.entity.DemoEntity;
import com.demo.db.repository.DemoEntityRepository;
import com.demo.util.JsonUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

/**
 * Spring 相关测试
 *
 * @author Song gh on 2022/5/6.
 */
@SpringBootTest
class DemoApplicationTest {

    @Resource
    private DemoEntityRepository demoEntityRepository;

    @Test
    void main() {
        DemoEntity demoEntity = new DemoEntity();
        demoEntity.setName("测试111");
        System.out.println(JsonUtils.beanOrListToJsonStr(demoEntity));
        demoEntityRepository.save(demoEntity);
        System.out.println(JsonUtils.beanOrListToJsonStr(demoEntity));
    }
}