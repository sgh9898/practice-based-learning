package com.sgh.demo.general.aop;

import com.alibaba.fastjson2.JSONObject;
import com.sgh.demo.common.constant.ApiResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.json.JsonException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 切面功能, 见 {@link com.sgh.demo.common.aop.LogAspect}
 *
 * @author Song gh on 2022/3/28.
 */
@Slf4j
@RestController
@RequestMapping("/aop")
@Tag(name = "切面功能")
public class AopController {

    @Operation(summary = "AOP 记录日志")
    @PostMapping("/log")
    public ApiResp aopLog() {
        return ApiResp.success();
    }

    @Operation(summary = "Json Exception 处理")
    @PostMapping("/exception/json")
    public ApiResp handleJsonException() {
        throw new JsonException("这是 Json 异常信息");
    }

    @Operation(summary = "Interceptor")
    @PostMapping("/interceptor/normal")
    public ApiResp interceptorNormal(@RequestAttribute("processedMsg") JSONObject processedMsg, String message) {
        return new ApiResp.Data(processedMsg);
    }

    @Operation(summary = "Filter")
    @PostMapping("/filter/normal")
    public ApiResp filterNormal(@RequestAttribute("processedMsg") JSONObject processedMsg, String message) {
        return new ApiResp.Data(processedMsg);
    }
}
