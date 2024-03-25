package com.collin.demo.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 中文相关的 String 工具类
 *
 * @author Song gh on 2023/11/15.
 */
public class ChineseStrUtils {

    /** 判断字符串是否存在乱码 */
    public static boolean hasMessyCode(String str) {
        Pattern pattern = Pattern.compile("\\s*|t*|r*|n*");
        Matcher matcher = pattern.matcher(str);
        String after = matcher.replaceAll("");
        String temp = after.replaceAll("\\p{P}", "");
        char[] ch = temp.trim().toCharArray();
        for (char c : ch) {
            if (!Character.isLetterOrDigit(c) && !isChinese(c)) {
                return true;
            }
        }
        return false;
    }

    /** 判断字符串是否全部为乱码 */
    public static boolean allMessyCode(String str) {
        Pattern pattern = Pattern.compile("\\s*|t*|r*|n*");
        Matcher matcher = pattern.matcher(str);
        String after = matcher.replaceAll("");
        // 去除标点字符
        String temp = after.replaceAll("\\p{P}", "");
        char[] ch = temp.trim().toCharArray();
        for (char c : ch) {
            if (Character.isLetterOrDigit(c) || isChinese(c)) {
                return false;
            }
        }
        return true;
    }

    /** 判断字符是否为中文 */
    public static boolean isChinese(char ch) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(ch);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
    }
}
