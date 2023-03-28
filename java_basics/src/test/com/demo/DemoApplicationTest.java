package com.demo;

import com.demo.database.repository.DemoEntityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.security.SecureRandom;

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

    }
}