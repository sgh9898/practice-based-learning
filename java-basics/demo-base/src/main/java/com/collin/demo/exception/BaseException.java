package com.collin.demo.exception;

import com.collin.demo.util.ResultStatus;
import lombok.Getter;

/** 基础异常 */
@Getter
public class BaseException extends RuntimeException {

    /** 默认错误代码为 1 */
    private int code = 1;
    private final String message;

    public BaseException(String message) {
        super(message);
        this.message = message;
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    public BaseException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BaseException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    public BaseException(ResultStatus resultStatus) {
        super(resultStatus.getMessage());
        this.code = resultStatus.getCode();
        this.message = resultStatus.getMessage();
    }
}
