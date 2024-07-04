package com.sgh.demo.common.substitution;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.apache.ibatis.logging.LogException;

import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 * Jackson 工具类
 *
 * @version 2024/2/29
 */
public class JacksonUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    }

    /**
     * Java对象 --> Json String
     */
    public static String beanToJson(Object bean) {
        try {
            return MAPPER.writeValueAsString(bean);
        } catch (Exception e) {
            throw new LogException("转换 Java Bean 为 Json 错误", e);
        }
    }

    /** Json String --> Java对象 */
    public static <T> T jsonToBean(String json, Class<T> beanType) {
        try {
            return MAPPER.readValue(json, beanType);
        } catch (Exception e) {
            throw new LogException("转换 Json 为 Java Bean 错误: " + json, e);
        }
    }

    /** JsonArray String --> List(Java对象) */
    public static <T> List<T> jsonToList(String json, Class<T> beanType) {
        try {
            CollectionType type = MAPPER.getTypeFactory().constructCollectionType(List.class, beanType);
            return MAPPER.readValue(json, type);
        } catch (Exception e) {
            throw new LogException("转换 Json 为 JavaList 错误: " + json, e);
        }
    }

    /** Json String --> JsonNode */
    public static JsonNode readTree(String json) {
        try {
            return MAPPER.readTree(json);
        } catch (IOException e) {
            throw new LogException("转换 Json 为 JsonNode 错误: " + json, e);
        }
    }

    /** Json String --> Map */
    public static Map<String, Object> jsonToMap(String json) {
        try {
            return MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            throw new LogException("转换 Json 为 Map 错误: " + json, e);
        }
    }

    /** Java对象 --> Map */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> beanToMap(Object bean) {
        return MAPPER.convertValue(bean, Map.class);
    }

    private JacksonUtil() {
    }
}
