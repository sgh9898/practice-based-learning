package com.sgh.demo.common.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Json 工具类
 * (基于 JacksonUtil 工具类优化)
 *
 * @author Song gh
 * @version 2025/3/5
 */
public class JsonUtils {

    private static final Logger log = LoggerFactory.getLogger(JsonUtils.class);

    /** 默认 mapper */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /** mapper: 忽略值为 null 的字段 */
    private static final ObjectMapper NON_NULL_MAPPER;

    static {
        // 所有属性都可以访问到 (private/public)
        MAPPER.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 禁止检测所有 get 方法
        MAPPER.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        // 仅序列化字段
        MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        // 反序列化时, 未知字段不报错
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 自动缩进生成的 Json
        MAPPER.enable(SerializationFeature.INDENT_OUTPUT);

        NON_NULL_MAPPER = MAPPER.copy();
        // 忽略值为 null 的字段
        NON_NULL_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

// ------------------------------ Java Bean 转换其他类型 ------------------------------

    /** Java Bean --> Json String */
    public static String beanToJson(Object bean) {
        try {
            return MAPPER.writeValueAsString(bean);
        } catch (Exception e) {
            throw new UnsupportedOperationException("转换 Java Bean 为 Json 错误", e);
        }
    }

    /** Java Bean --> Json String; 忽略值为 null 的字段 */
    public static String beanToJsonIgnoreNull(Object bean) {
        try {
            return NON_NULL_MAPPER.writeValueAsString(bean);
        } catch (Exception e) {
            throw new UnsupportedOperationException("转换 Java Bean 为 Json 错误", e);
        }
    }

    /** Java Bean --> Map(String, Object) */
    @SuppressWarnings("unchecked")
    public static <T, U> Map<T, U> beanToMap(Object bean) {
        return MAPPER.convertValue(bean, Map.class);
    }

    /** Java Bean --> Map(String, Object), 移除值为 null 的字段 */
    @SuppressWarnings("unchecked")
    public static <T, U> Map<T, U> beanToMapNonnull(Object bean) {
        Map<T, U> map = MAPPER.convertValue(bean, Map.class);
        map.entrySet().removeIf(entry -> entry.getValue() == null);
        return map;
    }

// ------------------------------ Json 转换其他类型 ------------------------------

    /** Json String --> Map */
    public static Map<String, Object> jsonToMap(String json) {
        try {
            return MAPPER.readValue(json, new TypeReference<HashMap<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            log.error("转换 Json 为 Map 错误:{}", json, e);
            throw new UnsupportedOperationException("转换 Json 为 Map 错误:" + json, e);
        }
    }

    /**
     * Json String --> Java Bean
     * <pre>
     * 1. 返回数据类型不可包含泛型, 如 {@code OuterClass<InnerClass>}, 需要调用 {@link #jsonToBean(String, TypeReference)} </pre>
     */
    public static <T> T jsonToBean(String json, Class<T> beanType) {
        try {
            return MAPPER.readValue(json, beanType);
        } catch (Exception e) {
            log.error("转换 Json 为 JavaBean 错误:{}", json, e);
            throw new UnsupportedOperationException("转换 Json 为 JavaBean 错误: " + json, e);
        }
    }

    /**
     * Json String --> Java Bean
     * <pre>
     * 1. 返回数据类型可以包含泛型, 如 {@code OuterClass<InnerClass>} </pre>
     */
    public static <T> T jsonToBean(String json, TypeReference<T> typeReference) {
        try {
            return MAPPER.readValue(json, typeReference);
        } catch (Exception e) {
            log.error("转换 Json 为 JavaBean 错误:{}", json, e);
            throw new UnsupportedOperationException("转换 Json 为 JavaBean 错误: " + json, e);
        }
    }

    /** JsonArray String --> List(Java Bean) */
    public static <T> List<T> jsonToList(String json, Class<T> beanType) {
        try {
            CollectionType type = MAPPER.getTypeFactory().constructCollectionType(List.class, beanType);
            return MAPPER.readValue(json, type);
        } catch (Exception e) {
            log.error("转换 Json 为 JavaList 错误:{}", json, e);
            throw new UnsupportedOperationException("转换 Json 为 JavaList 错误: " + json, e);
        }
    }

    /** Java Bean --> Map */
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

    /** JsonNode --> Java Bean */
    public static <T> T jsonNodeToBean(JsonNode jsonNode, Class<T> beanType) {
        return MAPPER.convertValue(jsonNode, beanType);
    }

    /** JsonNode --> Java Bean, 次级结构包含泛型 */
    public static <T> T jsonNodeToBean(JsonNode jsonNode, Class<T> beanType, Class<?> subBeanType) {
        JavaType type = MAPPER.getTypeFactory().constructParametricType(beanType, subBeanType);
        return MAPPER.convertValue(jsonNode, type);
    }

    private JsonUtils() {
    }
}
