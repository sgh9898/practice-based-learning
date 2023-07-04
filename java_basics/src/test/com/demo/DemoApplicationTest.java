package com.demo;

import com.demo.db.repository.DemoEntityRepository;
import com.demo.util.JacksonUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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
    void mainTest() {
        List<Long> testList = new ArrayList<>();
        testList.add(1L);
        testList.add(2L);
        testList.add(3L);
        testList.add(4L);
        System.out.println(JacksonUtils.beanToJson( demoEntityRepository.sqlGetByList(false, testList)));
    }
}