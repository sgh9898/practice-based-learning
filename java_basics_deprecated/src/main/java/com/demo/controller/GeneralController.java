package com.demo.controller;

import com.demo.util.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 基础或未分类功能
 *
 * @author Song gh on 2022/5/18.
 */
@Slf4j
@RestController
@RequestMapping("/general")
@Api(value = "General Controller", tags = "基础或未分类功能")
public class GeneralController {

    @Operation(summary = "Swagger 测试 param")
    @PostMapping("/swagger/param")
    @ApiImplicitParam(name = "test", value = "测试")
    public Map<String, Object> swaggerParam(String test) {
        return ResultUtil.success(test);
    }
}
