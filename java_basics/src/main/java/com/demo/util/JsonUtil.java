package com.demo.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.apache.poi.ss.formula.functions.T;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Json 相关工具
 *
 * @author Song gh on 2022/4/20.
 */
public class JsonUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        // 所有属性都可以访问到 (private, public)
        MAPPER.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 反序列化时, 没有 set 方法不报错
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 自动缩进生成的 Json
        MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /** Class 或 List<Class> 转为 Json String */
    public static String objOrListToJsonStr(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /** Json String 转为 Java Object */
    public static <T> T jsonStrToObj(String jsonStr, Class<T> toClass) {
        try {
            return MAPPER.readValue(jsonStr, toClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /** Json Array String 转为 List<Class> */
    public static <T> List<T> jsonArrStrToList(String jsonArrayStr, Class<T> toClass) {
        try {
            CollectionType toType = MAPPER.getTypeFactory().constructCollectionType(List.class, toClass);
            return MAPPER.readValue(jsonArrayStr, toType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /** Json String 转为 Map */
    public static Map<String, Object> jsonStrToMap(String jsonStr) {
        try {
            TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
            };
            return MAPPER.readValue(jsonStr, typeRef);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /** Map --> Class */
    public static <T> T mapToClass(Object fromMap, Class<T> toClass) {
        return MAPPER.convertValue(fromMap, toClass);
    }

    /** List<Map> --> List<Class> */
    public static List<T> listMapToListClass(List<Map<Object, Object>> mapList, Class<T> toClass) {
        List<T> classList = new ArrayList<>();
        for (Map<Object, Object> obj : mapList) {
            classList.add(MAPPER.convertValue(obj, toClass));
        }
        return classList;
    }

    /**
     * 将sql查询结果转为map
     *
     * @param sqlMapList sql查询结果
     * @param keyName    key 字段名
     * @param valueName  value 字段名
     */
    public static Map<?, ?> sqlMapListToMap(List<Map<String, Object>> sqlMapList, String keyName, String valueName) {
        Map<Object, Object> resultMap = new HashMap<>();
        for (Map<String, Object> currMap : sqlMapList) {
            resultMap.put(currMap.get(keyName), currMap.get(valueName));
        }
        return resultMap;
    }
}
