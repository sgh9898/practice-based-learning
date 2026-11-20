package com.sgh.demo.common.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Json 工具类
 * (基于 JacksonUtil 工具类优化)
 *
 * @author Song gh
 * @since 2026/1/15
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUtils {

    /** 默认 */
    private static final ObjectMapper MAPPER = new ObjectMapper();
    /** 紧凑格式的输出 */
    private static final ObjectMapper COMPACT_MAPPER;
    /** 忽略值为 null 的字段 */
    private static final ObjectMapper NON_NULL_MAPPER;

    /** 默认日期时间格式 */
    private static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /** 默认仅日期格式 */
    private static final String DEFAULT_DATE_ONLY_FORMAT = "yyyy-MM-dd";
    /** 默认仅时间格式 */
    private static final String DEFAULT_TIME_ONLY_FORMAT = "HH:mm:ss";

    static {
        // 创建 JavaTimeModule 并配置 LocalDateTime 的序列化和反序列化
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        // 配置 LocalDateTime 序列化器
        LocalDateTimeSerializer localDateTimeSerializer = new LocalDateTimeSerializer(
                DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)
        );
        LocalDateSerializer localDateSerializer = new LocalDateSerializer(
                DateTimeFormatter.ofPattern(DEFAULT_DATE_ONLY_FORMAT)
        );
        LocalTimeSerializer localTimeSerializer = new LocalTimeSerializer(
                DateTimeFormatter.ofPattern(DEFAULT_TIME_ONLY_FORMAT)
        );

        // 配置 LocalDateTime 反序列化器
        LocalDateTimeDeserializer localDateTimeDeserializer = new LocalDateTimeDeserializer(
                DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)
        );
        LocalDateDeserializer localDateDeserializer = new LocalDateDeserializer(
                DateTimeFormatter.ofPattern(DEFAULT_DATE_ONLY_FORMAT)
        );
        LocalTimeDeserializer localTimeDeserializer = new LocalTimeDeserializer(
                DateTimeFormatter.ofPattern(DEFAULT_TIME_ONLY_FORMAT)
        );

        // 注册序列化和反序列化器
        javaTimeModule.addSerializer(LocalDateTime.class, localDateTimeSerializer);
        javaTimeModule.addSerializer(LocalDate.class, localDateSerializer);
        javaTimeModule.addSerializer(LocalTime.class, localTimeSerializer);
        javaTimeModule.addDeserializer(LocalDateTime.class, localDateTimeDeserializer);
        javaTimeModule.addDeserializer(LocalDate.class, localDateDeserializer);
        javaTimeModule.addDeserializer(LocalTime.class, localTimeDeserializer);

        // 所有属性都可以访问到(private/public)
        MAPPER.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 反序列化时, 未知字段不报错
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 自动缩进生成的 Json
        MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
        // 注册 JavaTimeModule
        MAPPER.registerModule(javaTimeModule);
        // 禁用日期时间转换为时间戳
        MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 忽略值为 null 的字段
        NON_NULL_MAPPER = MAPPER.copy();
        NON_NULL_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // 紧凑格式的输出
        COMPACT_MAPPER = MAPPER.copy();
        COMPACT_MAPPER.configure(SerializationFeature.INDENT_OUTPUT, false);
    }

// ------------------------------ Java 对象 转换其他类型 ------------------------------

    /** Java 对象 --> Json String */
    public static String beanToJson(Object bean) {
        try {
            return MAPPER.writeValueAsString(bean);
        } catch (Exception e) {
            throw new UnsupportedOperationException("转换 Java Bean 为 Json 错误", e);
        }
    }

    /** Java 对象 --> 紧凑的 Json String */
    public static String beanToCompactJson(Object bean) {
        try {
            return COMPACT_MAPPER.writeValueAsString(bean);
        } catch (Exception e) {
            throw new UnsupportedOperationException("转换 Java Bean 为 Json 错误", e);
        }
    }

    /** Java 对象 --> Json String; 忽略值为null的字段 */
    public static String beanToJsonIgnoreNull(Object bean) {
        try {
            return NON_NULL_MAPPER.writeValueAsString(bean);
        } catch (Exception e) {
            throw new UnsupportedOperationException("转换 Java Bean 为 Json 错误", e);
        }
    }

    /** Java 对象 --> Map */
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

    /** Json String --> Java 对象 */
    public static <T> T jsonToBean(String json, Class<T> beanType) {
        try {
            return MAPPER.readValue(json, beanType);
        } catch (Exception e) {
            throw new UnsupportedOperationException("转换 Json 为 Java Bean 错误: " + json, e);
        }
    }

    /** JsonArray String --> List(Java 对象) */
    public static <T> List<T> jsonToList(String json, Class<T> beanType) {
        try {
            CollectionType type = MAPPER.getTypeFactory().constructCollectionType(List.class, beanType);
            return MAPPER.readValue(json, type);
        } catch (Exception e) {
            throw new UnsupportedOperationException("转换 Json 为 JavaList 错误: " + json, e);
        }
    }

    /** Java 对象 --> Map */
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

    /** JsonNode --> Java 对象 */
    public static <T> T jsonNodeToBean(JsonNode jsonNode, Class<T> beanType) {
        return MAPPER.convertValue(jsonNode, beanType);
    }

    /** JsonNode --> Java 对象, 次级结构包含泛型 */
    public static <T> T jsonNodeToBean(JsonNode jsonNode, Class<T> beanType, Class<?> subBeanType) {
        JavaType type = MAPPER.getTypeFactory().constructParametricType(beanType, subBeanType);
        return MAPPER.convertValue(jsonNode, type);
    }
}
