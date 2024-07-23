package com.sgh.demo.common.exception;

import com.sgh.demo.common.util.ResultStatus;
import lombok.Getter;
import lombok.Setter;

/** 异常类: 基础异常 */
@Setter
@Getter
public class BaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** 默认错误代码为 1 */
    private int code = 1;

    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, Throwable e) {
        super(message, e);
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
