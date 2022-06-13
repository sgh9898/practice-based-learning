package com.demo.handler;

import com.demo.exception.BaseException;
import com.demo.exception.JsonException;
import com.demo.util.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

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
    public Map<String, Object> handleControllerException(BaseException e) {
        return ResultUtil.error(e.getMessage());
    }

    /** Json 异常处理 */
    @ExceptionHandler(value = JsonException.class)
    public Map<String, Object> jsonExceptionHandler(JsonException jsonException) {
        log.error("[Json 异常]: {}", jsonException.getMessage());
        return ResultUtil.error("Json 出现异常");
    }
}
