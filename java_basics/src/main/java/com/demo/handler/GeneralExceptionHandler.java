package com.demo.handler;

import com.demo.exception.JsonException;
import com.demo.util.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * 泛用异常处理类
 *
 * @author Song gh on 2022/4/2.
 */
@Slf4j
@RestControllerAdvice   // 等同于 @ControllerAdvice + @ResponseBody, 可返回 json 数据
public class GeneralExceptionHandler {

    /** Json 异常处理 */
    @ExceptionHandler(value = JsonException.class)
    public Map<String, Object> JsonExceptionHandler(JsonException jsonException) {
        log.error("[Json 异常]: {}", jsonException.getMessage());
        return ResultUtil.error("Json 出现异常");
    }
}
