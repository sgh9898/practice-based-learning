package com.demo;

import com.demo.sample.entity.DemoEntity;
import com.demo.sample.repository.DemoEntityRepository;
import com.demo.easyexcel.service.ExcelService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
class DemoApplicationTest {

    @Resource
    private DemoEntityRepository demoEntityRepository;
    @Resource
    private ExcelService excelService;

    @Test
    void queryTest() {
        List<Long> idList = new ArrayList<>();
        idList.add(1L);
        idList.add(2L);
        List<DemoEntity> demoList = demoEntityRepository.sqlGetByList(idList);
        demoList.forEach(demoEntity ->
                System.out.println(demoEntity.getName()));
    }
}