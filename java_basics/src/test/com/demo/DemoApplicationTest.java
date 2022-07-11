package com.demo;

import com.demo.database.entity.DemoEntity;
import com.demo.database.repository.DemoEntityRepository;
import com.demo.util.JsonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;

import javax.annotation.Resource;
import java.util.Date;

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
    void main1() {
        DemoEntity demoEntity = new DemoEntity();
        demoEntity.setName("测试");
        Example<DemoEntity> example = Example.of(demoEntity);
        System.out.println(JsonUtil.objOrListToJsonStr(demoEntityRepository.findAll(example)));
    }
}