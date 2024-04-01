package com.collin.demo.common.util;

/** 错误代码及信息 */
public enum ResultStatus {

    //通用状态
    SUCCESS(0, "成功"),
    ERROR(-1, "系统繁忙"),
    REQUEST_NOT_EXIST(-2, "请求地址不存在"),
    PARAM_ERROR(1, "参数异常"),
    TOKEN_INVALID(2, "token无效"),
    REQUEST_DENIED(3, "无权访问"),
    IP_INVALID(4, "当前IP无权访问"),
    REQUEST_TOO_MANY(5, "访问过于频繁"),
    NOT_LOGIN(6, "用户未登录");

    private int code;
    private String message;

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
