package com.demo.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.formula.functions.T;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Json 相关工具
 *
 * @author Song gh on 2022/4/20.
 */
public class JsonUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /** List of Map --> List of Class */
    public static List<T> listToClass(List<Map<Object, Object>> fromList, Class<T> toClass) {
        List<T> toList = new ArrayList<>();
        for (Map<Object, Object> obj : fromList) {
            toList.add(MAPPER.convertValue(obj, toClass));
        }
        return toList;
    }
}
