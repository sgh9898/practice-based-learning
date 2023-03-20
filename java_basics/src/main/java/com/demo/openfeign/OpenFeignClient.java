package com.demo.openfeign;

import com.demo.database.entity.DemoEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * OpenFeign 调用接口
 *
 * @author Song gh on 2023/3/16.
 */
@FeignClient(value = "demoOpenFeignClient", url = "http://localhost:8080/demo")
public interface OpenFeignClient {

//    @PostMapping(value = "/demoEntity/get", produces = "application/json")
    @GetMapping("/demoEntity/get")
    DemoEntity getDemoEntity(Long id);
}