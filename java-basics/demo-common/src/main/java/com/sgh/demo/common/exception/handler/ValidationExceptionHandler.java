package com.sgh.demo.common.exception.handler;

import com.sgh.demo.common.constant.ResultStatus;
import com.sgh.demo.common.constant.ApiResp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.annotation.Order;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * [异常处理类] 处理参数校验异常
 *
 * @author Song gh
 * @version 2024/7/15
 */
@Slf4j
@Order(0)
@RestControllerAdvice
public class ValidationExceptionHandler {

    /** 接口参数未通过 {@link javax.validation.Valid} 校验时, 进行统一的异常处理 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResp handleMethodArgumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException e) throws IOException {
        String body = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
        log.error("接口参数未通过校验, 路径: {}, 方法: {}, 参数: {}", request.getRequestURI(), request.getMethod(), body, e);
        List<ObjectError> errorList = e.getBindingResult().getAllErrors();
        String message = errorList.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(";"));
        return ApiResp.error(ResultStatus.PARAM_ERROR.getCode(), message);
    }

    /** 接口参数转换失败时(多发生于参数值与枚举类不匹配), 进行统一的异常处理 */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ApiResp handleMethodArgumentTypeMismatchException(HttpServletRequest request, MethodArgumentTypeMismatchException e) throws IOException {
        log.error("接口参数未通过校验, 路径: {}, 方法: {}", request.getRequestURI(), request.getMethod(), e);
        return ApiResp.error(ResultStatus.PARAM_ERROR.getCode(), "参数类型或参数值错误");
    }
}
