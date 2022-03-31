package com.demo.util;


/**
 * 常用错误代码及信息
 */
public enum ResultStatus {

    // 基本配置
    SUCCESS(0, "成功"),
    ERROR(-1, "系统繁忙"),

    // 自定义
    PARAM_ERROR(1, "参数异常");

    int code;
    String message;

    // constructor
    ResultStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
