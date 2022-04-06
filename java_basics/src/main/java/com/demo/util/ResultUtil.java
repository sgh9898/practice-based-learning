package com.demo.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Linked Map 返回 request 结果 (用于 controller 层)
 *
 * @author Song gh on 2022/3/28.
 */
public class ResultUtil {

    // 基本配置
    public static final ResultStatus SUCCESS = ResultStatus.SUCCESS;
    public static final ResultStatus FAILURE = ResultStatus.ERROR;

    /** 成功 */
    public static Map<String, Object> success() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", SUCCESS.getCode());
        result.put("message", SUCCESS.getMessage());
        return result;
    }

    /** 成功, 输出 data */
    public static Map<String, Object> success(Object data) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", SUCCESS.getCode());
        result.put("message", SUCCESS.getMessage());
        result.put("data", data);
        return result;
    }

    /** 成功, 自定义输出 */
    public static Map<String, Object> success(String key, Object value) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", SUCCESS.getCode());
        result.put("message", SUCCESS.getMessage());
        result.put(key, value);
        return result;
    }

    /** 失败 */
    public static Map<String, Object> error() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", FAILURE.getCode());
        result.put("message", FAILURE.getMessage());
        return result;
    }

    /** 失败, 自定义错误描述 */
    public static Map<String, Object> error(String message) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", FAILURE.getCode());
        result.put("message", message);
        return result;
    }

    /** 失败, 自定义输出 */
    public static Map<String, Object> error(String key, Object value) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", FAILURE.getCode());
        result.put("message", FAILURE.getMessage());
        result.put(key, value);
        return result;
    }

    /** 失败, 自定义错误描述及输出 */
    public static Map<String, Object> error(String message, String key, Object value) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", FAILURE.getCode());
        result.put("message", message);
        result.put(key, value);
        return result;
    }

    /** 根据 {@link ResultStatus} 输出结果 */
    public static Map<String, Object> info(ResultStatus resultStatus) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", resultStatus.getCode());
        result.put("message", resultStatus.getMessage());
        return result;
    }

    /** 根据 {@link ResultStatus}, 自定义输出结果 */
    public static Map<String, Object> info(ResultStatus resultStatus, String key, Object value) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", resultStatus.getCode());
        result.put("message", resultStatus.getMessage());
        result.put(key, value);
        return result;
    }

}
