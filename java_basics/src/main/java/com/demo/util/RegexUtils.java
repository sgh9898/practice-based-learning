package com.demo.util;

/**
 * 正则表达式相关工具
 *
 * @author Song gh
 * @version 2024/1/18
 */
public class RegexUtils {

    /** 获取末尾的数字 */
    public static String getTailingNum(String str) {
        return str.replaceAll(".*\\D", "");
    }

    /**
     * 替换指定位置的 substring
     *
     * @param str      初始 string
     * @param startIdx 开始替换的位置(从 0 开始计), 包含当前位置
     * @param endIdx   结束替换的位置(从 0 开始计), 不包含当前位置
     * @param newStr   替换后的 substring
     */
    public static String replaceSubstr(String str, int startIdx, int endIdx, String newStr) {
        StringBuilder stringBuilder = new StringBuilder(str);
        stringBuilder.replace(startIdx, endIdx, newStr);
        return stringBuilder.toString();
    }

    private RegexUtils() {
    }
}
