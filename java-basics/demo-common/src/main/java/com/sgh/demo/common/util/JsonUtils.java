package com.sgh.demo.common.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.type.CollectionType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Json 工具类
 * (基于 JacksonUtil 工具类优化)
 *
 * @author Song gh
 * @version 2024/3/21
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUtils {

    /** 默认 */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /** 忽略值为null的字段 */
    private static final ObjectMapper NON_NULL_MAPPER;

    static {
        // 所有属性都可以访问到 (private/public)
        MAPPER.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 反序列化时, 未知字段不报错
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 自动缩进生成的 Json
        MAPPER.enable(SerializationFeature.INDENT_OUTPUT);

        NON_NULL_MAPPER = MAPPER.copy();
        // 忽略值为null的字段
        NON_NULL_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

// ------------------------------ Java对象 转换其他类型 ------------------------------

    /** Java对象 --> Json String */
    public static String beanToJson(Object bean) {
        try {
            return MAPPER.writeValueAsString(bean);
        } catch (Exception e) {
            throw new UnsupportedOperationException("转换 Java Bean 为 Json 错误", e);
        }
    }

    /** Java对象 --> Json String; 忽略值为null的字段 */
    public static String beanToJsonIgnoreNull(Object bean) {
        try {
            return NON_NULL_MAPPER.writeValueAsString(bean);
        } catch (Exception e) {
            throw new UnsupportedOperationException("转换 Java Bean 为 Json 错误", e);
        }
    }

    /** Java对象 --> Map */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> beanToMap(Object bean) {
        return MAPPER.convertValue(bean, Map.class);
    }

// ------------------------------ Json 转换其他类型 ------------------------------

    /** Json String --> Map */
    public static Map<String, Object> jsonToMap(String json) {
        try {
            return MAPPER.readValue(json, new TypeReference<HashMap<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            throw new UnsupportedOperationException("转换 Json 为 Map 错误:" + json, e);
        }
    }

    /** Json String --> Java对象 */
    public static <T> T jsonToBean(String json, Class<T> beanType) {
        try {
            return MAPPER.readValue(json, beanType);
        } catch (Exception e) {
            throw new UnsupportedOperationException("转换 Json 为 Java Bean 错误: " + json, e);
        }
    }

    /** JsonArray String --> List(Java对象) */
    public static <T> List<T> jsonToList(String json, Class<T> beanType) {
        try {
            CollectionType type = MAPPER.getTypeFactory().constructCollectionType(List.class, beanType);
            return MAPPER.readValue(json, type);
        } catch (Exception e) {
            throw new UnsupportedOperationException("转换 Json 为 JavaList 错误: " + json, e);
        }
    }

    /** Java对象 --> Map */
    public static <T> T mapToBean(Object bean, Class<T> tClass) {
        return MAPPER.convertValue(bean, tClass);
    }

// ------------------------------ JsonNode 转换其他类型 ------------------------------

    /** Json String --> JsonNode */
    public static JsonNode readTree(String json) {
        try {
            return MAPPER.readTree(json);
        } catch (IOException e) {
            throw new UnsupportedOperationException("转换 Json 为 JsonNode 错误: " + json, e);
        }
    }

    /** JsonNode --> Java对象 */
    public static <T> T jsonNodeToBean(JsonNode jsonNode, Class<T> beanType) {
        return MAPPER.convertValue(jsonNode, beanType);
    }

    /** JsonNode --> Java对象, 次级结构包含泛型 */
    public static <T> T jsonNodeToBean(JsonNode jsonNode, Class<T> beanType, Class<?> subBeanType) {
        JavaType type = MAPPER.getTypeFactory().constructParametricType(beanType, subBeanType);
        return MAPPER.convertValue(jsonNode, type);
    }
}
