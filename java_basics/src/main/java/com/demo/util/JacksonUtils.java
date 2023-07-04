package com.demo.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/** Jackson 工具类 */
public class JacksonUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        // 所有属性都可以访问到 (private/public)
        MAPPER.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 反序列化时, 没有 set 方法不报错
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 自动缩进生成的 Json
        MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /** JavaBean 转 JsonObject */
    public static String beanToJson(Object o) {
        try {
            return MAPPER.writeValueAsString(o);
        } catch (Exception e) {
            throw new RuntimeException("转换 JavaBean 为 Json 错误", e);
        }
    }

    /** JsonObject 转 JavaBean */
    public static <T> T jsonToBean(String json, Class<T> t) {
        try {
            return MAPPER.readValue(json, t);
        } catch (Exception e) {
            throw new RuntimeException("转换 Json 为 JavaBean 错误: " + json, e);
        }
    }


    /** JsonArray 转 List */
    public static <T> List<T> jsonToList(String json, Class<T> beanType) {
        try {
            CollectionType type = MAPPER.getTypeFactory().constructCollectionType(List.class, beanType);
            return MAPPER.readValue(json, type);
        } catch (Exception e) {
            throw new RuntimeException("转换 Json 为 JavaList 错误: " + json, e);
        }
    }


    /** 返回一个 JsonNode 对象 */
    public static JsonNode readTree(String json) {
        try {
            return MAPPER.readTree(json);
        } catch (IOException e) {
            throw new RuntimeException("转换 Json 为 JsonNode 错误: " + json, e);
        }
    }


    /** JavaBean 转 Map */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> beanToMap(Object bean) {
        return MAPPER.convertValue(bean, Map.class);
    }

    /** JsonNode 转 JavaBean */
    public static <T> T jsonNodeToBean(JsonNode jsonNode, Class<T> beanType) {
        return MAPPER.convertValue(jsonNode, beanType);
    }

    /** JsonNode 转 JavaBean, 次级结构包含泛型 */
    public static <T> T jsonNodeToBean(JsonNode jsonNode, Class<T> beanType, Class<?> subBeanType) {
        JavaType type = MAPPER.getTypeFactory().constructParametricType(beanType, subBeanType);
        return MAPPER.convertValue(jsonNode, type);
    }
}
