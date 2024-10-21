package com.sgh.demo.common.exception;

import com.sgh.demo.common.constant.ResultStatus;
import lombok.Getter;

import java.io.Serial;

/**
 * [异常类] 基础异常
 *
 * @version 2024/7/11
 */
@Getter
public class BaseException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 错误代码 */
    private final int code;

    public BaseException(String message) {
        super(message);
        this.code = ResultStatus.PARAM_ERROR.getCode();
    }

    public BaseException(String message, Throwable e) {
        super(message, e);
        this.code = ResultStatus.PARAM_ERROR.getCode();
    }

    public BaseException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BaseException(int code, String message, Throwable e) {
        super(message, e);
        this.code = code;
    }

    public BaseException(ResultStatus status) {
        super(status.getMessage());
        this.code = status.getCode();
    }

    @Override
    public String toString() {
        return "BaseException [code=" + code + "," + super.getMessage() + "]";
    }

}
