package com.demo.dict;

import com.demo.dict.db.entity.DictData;
import com.demo.dict.db.repository.DictDataRepository;
import lombok.AccessLevel;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.DependsOn;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据字典工具类
 *
 * @author Song gh
 * @version 2024/3/12
 */
@Component
@DependsOn("dictDataRepository")
public class DictionaryUtils {

    /** 编码字典, Map(目录编码, Map(词条编码, 词条中文)) */
    @Setter(AccessLevel.PRIVATE)
    private static Map<String, Map<String, String>> codeToCnMap;

    /** 中文字典, Map(目录编码, Map(词条中文, 词条编码)) */
    @Setter(AccessLevel.PRIVATE)
    private static Map<String, Map<String, String>> cnToCodeMap;

    @Setter(AccessLevel.PRIVATE)
    private static DictDataRepository dictDataRepository;

    /**
     * 根据目录 获取 中文转编码字典
     *
     * @param dictCode 目录编码
     */
    @NonNull
    public static Map<String, String> getCnToCodeDict(String dictCode) {
        try {
            return cnToCodeMap.getOrDefault(dictCode, new HashMap<>());
        } catch (NullPointerException e) {
            updateDictMap();
            return cnToCodeMap.getOrDefault(dictCode, new HashMap<>());
        }
    }

    /**
     * 根据中文 获取 词条编码
     *
     * @param dictCode     目录编码
     * @param dictDataName 词条中文名
     * @return 词条对应的编码, 无对应编码则返回 null
     */
    @Nullable
    public static String getCode(String dictCode, String dictDataName) {
        try {
            return cnToCodeMap.getOrDefault(dictCode, new HashMap<>()).get(dictDataName);
        } catch (NullPointerException e) {
            updateDictMap();
            return cnToCodeMap.getOrDefault(dictCode, new HashMap<>()).get(dictDataName);
        }
    }

    /**
     * 根据中文 获取 词条编码, 获取失败时使用默认值
     *
     * @param dictCode     目录编码
     * @param dictDataName 词条中文名
     * @param defaultVal   词条无对应编码时, 返回的默认值
     */
    @SuppressWarnings("unchecked")
    public static <T> T getCodeOrDefault(String dictCode, String dictDataName, T defaultVal) {
        String val = getCode(dictCode, dictDataName);
        if (StringUtils.isBlank(val)) {
            return defaultVal;
        }
        // 根据默认值类型切换返回类型
        if (defaultVal instanceof Boolean) {
            if ("1".equals(val) || "true".equalsIgnoreCase(val)) {
                return (T) Boolean.TRUE;
            } else {
                return (T) Boolean.FALSE;
            }
        } else if (defaultVal instanceof Integer) {
            return (T) Integer.valueOf(val);
        } else if (defaultVal instanceof Long) {
            return (T) Long.valueOf(val);
        } else if (defaultVal instanceof Double) {
            return (T) Double.valueOf(val);
        } else {
            return (T) val;
        }
    }

    /**
     * 根据目录 获取 编码转中文字典
     *
     * @param dictCode 目录编码
     */
    @NonNull
    public static Map<String, String> getCodeToCnDict(String dictCode) {
        try {
            return codeToCnMap.getOrDefault(dictCode, new HashMap<>());
        } catch (NullPointerException e) {
            updateDictMap();
            return codeToCnMap.getOrDefault(dictCode, new HashMap<>());
        }
    }

    /**
     * 根据词条编码 获取 中文
     *
     * @param dictCode     目录编码
     * @param dictDataCode 词条编码
     * @return 词条对应的编码, 无对应编码则返回 null
     */
    @Nullable
    public static String getName(String dictCode, Object dictDataCode) {
        String key;
        if (dictDataCode instanceof Integer) {
            key = ((Integer) dictDataCode).toString();
        } else if (dictDataCode instanceof Long) {
            key = ((Long) dictDataCode).toString();
        } else if (dictDataCode instanceof Boolean) {
            key = Boolean.TRUE.equals(dictDataCode) ? "1" : "0";
        } else {
            key = (String) dictDataCode;
        }
        try {
            return codeToCnMap.getOrDefault(dictCode, new HashMap<>()).get(key);
        } catch (NullPointerException e) {
            updateDictMap();
            return codeToCnMap.getOrDefault(dictCode, new HashMap<>()).get(key);
        }
    }

    /**
     * 根据词条编码 获取 中文, 获取失败时使用默认值
     *
     * @param dictCode     目录编码
     * @param dictDataCode 词条编码
     * @param defaultVal   获取失败时使用的默认值
     */
    public static String getNameOrDefault(String dictCode, Object dictDataCode, String defaultVal) {
        String val = getName(dictCode, dictDataCode);
        if (StringUtils.isBlank(val)) {
            return defaultVal;
        }
        return val;
    }

    private DictionaryUtils(DictDataRepository dictDataRepository) {
        setDictDataRepository(dictDataRepository);
    }

    /** 定期更新数据字典 */
    @Scheduled(fixedDelayString = "PT1H")
    private static synchronized void updateDictMap() {
        Map<String, Map<String, String>> tempCodeToCnMap = new HashMap<>();
        Map<String, Map<String, String>> tempCnToCodeMap = new HashMap<>();
        List<DictData> dictList = dictDataRepository.findAll();
        for (DictData dict : dictList) {
            tempCodeToCnMap.computeIfAbsent(dict.getDictCode(), k -> new HashMap<>()).put(dict.getDictDataCode(), dict.getDictDataName());
            tempCnToCodeMap.computeIfAbsent(dict.getDictCode(), k -> new HashMap<>()).put(dict.getDictDataName(), dict.getDictDataCode());
        }
        setCodeToCnMap(tempCodeToCnMap);
        setCnToCodeMap(tempCnToCodeMap);
    }
}
