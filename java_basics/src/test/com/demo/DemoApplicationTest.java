package com.demo;

import com.demo.db.entity.DemoEntity;
import com.demo.db.repository.DemoEntityRepository;
import com.demo.pojo.DemoEntityVo;
import com.demo.util.JsonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
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
    void main1() {
        DemoEntityVo vo = demoEntityRepository.getSingleVo("测试1");
        System.out.println(JsonUtil.objOrListToJsonStr(vo));
    }
}