package com.demo.util;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/** Web 请求的统一返回结果 (Controller 返回格式) */
public class ResultUtil {

    // 基本配置
    public static final ResultStatus SUCCESS = ResultStatus.SUCCESS;
    public static final ResultStatus DEFAULT_ERROR = ResultStatus.ERROR;
    private static final Logger log = LoggerFactory.getLogger(ResultUtil.class);

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

    /** 成功, Key-Value 格式输出 */
    public static Map<String, Object> success(String key, Object value) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", SUCCESS.getCode());
        result.put("message", SUCCESS.getMessage());
        if (StringUtils.isBlank(key)) {
            throw new RuntimeException("参数错误");
        }
        result.put(key, value);
        return result;
    }

    /** 成功, Map 格式输出 */
    public static Map<String, Object> success(Map<String, Object> data) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", SUCCESS.getCode());
        result.put("message", SUCCESS.getMessage());
        if (MapUtils.isEmpty(data)) {
            return success();
        }
        result.putAll(data);
        return result;
    }

    /** 失败 */
    public static Map<String, Object> error() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", DEFAULT_ERROR.getCode());
        result.put("message", DEFAULT_ERROR.getMessage());
        return result;
    }

    /** 失败, 记录报错 */
    public static Map<String, Object> error(Exception e) {
        log.error(e.getMessage());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", DEFAULT_ERROR.getCode());
        result.put("message", DEFAULT_ERROR.getMessage());
        return result;
    }

    /** 失败, 自定义错误信息 */
    public static Map<String, Object> error(String message) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", DEFAULT_ERROR.getCode());
        result.put("message", message);
        return result;
    }

    /** 失败, 自定义输出 */
    public static Map<String, Object> error(String key, Object value) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", DEFAULT_ERROR.getCode());
        result.put("message", DEFAULT_ERROR.getMessage());
        if (StringUtils.isBlank(key)) {
            throw new RuntimeException("参数错误");
        }
        result.put(key, value);
        return result;
    }

    /** 失败, 自定义错误描述及输出 */
    public static Map<String, Object> error(String message, String key, Object value) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", DEFAULT_ERROR.getCode());
        result.put("message", message);
        result.put(key, value);
        return result;
    }

    /** 自定义输出结果 */
    public static Map<String, Object> info(int code, String message) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", code);
        if (StringUtils.isBlank(message)) {
            result.put("message", DEFAULT_ERROR.getMessage());
        } else {
            result.put("message", message);
        }
        return result;
    }

    /** 根据 {@link ResultStatus} 输出结果 */
    public static Map<String, Object> info(ResultStatus resultStatus) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", resultStatus.getCode());
        result.put("message", resultStatus.getMessage());
        return result;
    }
}
