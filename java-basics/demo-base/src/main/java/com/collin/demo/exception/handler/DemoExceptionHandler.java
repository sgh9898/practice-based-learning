package com.collin.demo.exception.handler;

import com.collin.demo.exception.BaseException;
import com.collin.demo.exception.JsonException;
import com.collin.demo.util.ApiResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 异常处理类
 *
 * @author Song gh on 2022/5/12.
 */
@Slf4j
@RestControllerAdvice   // 等同于 @ControllerAdvice + @ResponseBody, 可返回 json 数据
public class DemoExceptionHandler {

    /** 基础异常处理 */
    @ExceptionHandler(value = BaseException.class)
    public ApiResp handleControllerException(BaseException e) {
        return ApiResp.error(e.getMessage());
    }

    /** Json 异常处理 */
    @ExceptionHandler(value = JsonException.class)
    public ApiResp jsonExceptionHandler(JsonException jsonException) {
        log.error("[Json 异常]: {}", jsonException.getMessage());
        return ApiResp.error("Json 出现异常");
    }
}
