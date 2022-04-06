package com.demo.exception;

import com.demo.util.ResultStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 基础异常
 *
 * @author Song gh on 2022/4/2.
 */
@Data
@EqualsAndHashCode(callSuper = true)    // .equals() 对 parent 也进行比较
public class BaseException extends RuntimeException {

    /** 默认错误代码为 1 */
    private int code = 1;
    private String message;

    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BaseException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public BaseException(ResultStatus resultStatus) {
        super(resultStatus.getMessage());
        this.code = resultStatus.getCode();
    }
}
