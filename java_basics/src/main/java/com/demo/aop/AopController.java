package com.demo.aop;

import com.alibaba.fastjson2.JSONObject;
import com.demo.exception.JsonException;
import com.demo.util.ApiResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 切面功能, 见 {@link LogAspect}
 *
 * @author Song gh on 2022/3/28.
 */
@Slf4j
@RestController
@RequestMapping("/aop")
@Api(tags = "切面功能")
public class AopController {

    @ApiOperation("AOP 记录日志")
    @PostMapping("/log")
    public ApiResp aopLog() {
        return ApiResp.success();
    }

    @ApiOperation("Json Exception 处理")
    @PostMapping("/exception/json")
    public ApiResp handleJsonException() {
        throw new JsonException("这是 Json 异常信息");
    }

    @ApiOperation(value = "Interceptor", notes = "Json 参数用 Postman 进行测试")
    @PostMapping("/interceptor/normal")
    public ApiResp interceptorNormal(@RequestAttribute("processedMsg") JSONObject processedMsg, String message) {
        return new ApiResp.Data(processedMsg);
    }

    @ApiOperation(value = "Filter", notes = "Json 参数用 Postman 进行测试")
    @PostMapping("/filter/normal")
    public ApiResp filterNormal(@RequestAttribute("processedMsg") JSONObject processedMsg, String message) {
        return new ApiResp.Data(processedMsg);
    }
}
