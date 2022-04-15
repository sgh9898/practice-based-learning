package com.demo.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Test Controller (测试功能)
 *
 * @author Song gh on 2022/4/15.
 */
@Tag(name = "General Controller", description = "测试功能")
@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {
}
