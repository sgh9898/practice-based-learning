package com.sgh.demo.common.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 中文工具类
 *
 * @author Song gh
 * @version 2024/7/11
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CnStrUtils {

    /** 中文首字母对照表 */
    private static final List<CNtoChar> CN_TO_CHAR_LIST = new LinkedList<>();

    static {
        // 中文首字母对照表
        CN_TO_CHAR_LIST.add(new CNtoChar(-20319, -20284, 'A'));
        CN_TO_CHAR_LIST.add(new CNtoChar(-20283, -19776, 'B'));
        CN_TO_CHAR_LIST.add(new CNtoChar(-19775, -19219, 'C'));
        CN_TO_CHAR_LIST.add(new CNtoChar(-19218, -18711, 'D'));
        CN_TO_CHAR_LIST.add(new CNtoChar(-18710, -18527, 'E'));
        CN_TO_CHAR_LIST.add(new CNtoChar(-18526, -18240, 'F'));
        CN_TO_CHAR_LIST.add(new CNtoChar(-18239, -17923, 'G'));
        CN_TO_CHAR_LIST.add(new CNtoChar(-17922, -17418, 'H'));
        CN_TO_CHAR_LIST.add(new CNtoChar(-17417, -16475, 'J'));
        CN_TO_CHAR_LIST.add(new CNtoChar(-16474, -16213, 'K'));
        CN_TO_CHAR_LIST.add(new CNtoChar(-16212, -15641, 'L'));
        CN_TO_CHAR_LIST.add(new CNtoChar(-15640, -15166, 'M'));
        CN_TO_CHAR_LIST.add(new CNtoChar(-15165, -14923, 'N'));
        CN_TO_CHAR_LIST.add(new CNtoChar(-14922, -14915, 'O'));
        CN_TO_CHAR_LIST.add(new CNtoChar(-14914, -14631, 'P'));
        CN_TO_CHAR_LIST.add(new CNtoChar(-14630, -14150, 'Q'));
        CN_TO_CHAR_LIST.add(new CNtoChar(-14149, -14091, 'R'));
        CN_TO_CHAR_LIST.add(new CNtoChar(-14090, -13319, 'S'));
        CN_TO_CHAR_LIST.add(new CNtoChar(-13318, -12839, 'T'));
        CN_TO_CHAR_LIST.add(new CNtoChar(-12838, -12557, 'W'));
        CN_TO_CHAR_LIST.add(new CNtoChar(-12556, -11848, 'X'));
        CN_TO_CHAR_LIST.add(new CNtoChar(-11847, -11056, 'Y'));
        CN_TO_CHAR_LIST.add(new CNtoChar(-11055, -10247, 'Z'));
    }

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

    /** 获取大写中文首字母(过滤特殊字符) */
    public static String getFirstLettersUpper(String cnStr) {
        try {
            StringBuilder pyStrBd = new StringBuilder();
            for (char c : cnStr.toCharArray()) {
                pyStrBd.append(getChar(c));
            }
            return standardFilter(pyStrBd.toString().replaceAll("\\s*", ""));
        } catch (UnsupportedEncodingException e) {
            log.error("获取中文首字母失败", e);
        }
        return "";
    }

    /** 获取小写中文首字母(过滤特殊字符) */
    public static String getFirstLettersLower(String cnStr) {
        try {
            StringBuilder pyStrBd = new StringBuilder();
            for (char c : cnStr.toCharArray()) {
                pyStrBd.append(getChar(c));
            }
            return standardFilter(pyStrBd.toString().replaceAll("\\s*", "").toLowerCase());
        } catch (UnsupportedEncodingException e) {
            log.error("获取小写中文首字母失败", e);
        }
        return "";
    }

    /** 过滤特殊字符 */
    private static String standardFilter(String str) {
        String regEx = "[`~!@#$%^&*()+=|{}':;,\\[\\].<>/?！￥…（）—【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    /** 遍历获取首字母 */
    private static char getChar(char ch) throws UnsupportedEncodingException {
        byte[] bytes = String.valueOf(ch).getBytes("GBK");
        // 双字节汉字处理
        if (bytes.length == 2) {
            int highByte = 256 + bytes[0];
            int lowByte = 256 + bytes[1];
            int asc = (256 * highByte + lowByte) - 256 * 256;
            // 遍历转换
            for (CNtoChar map : CN_TO_CHAR_LIST) {
                if (asc >= map.getAsciiStart() && asc <= map.getAsciiEnd()) {
                    return map.getCode();
                }
            }
        }
        // 单字节或其他直接输入，不执行编码
        return ch;
    }

    /** 中文首字母对照 */
    @Data
    @AllArgsConstructor
    private static class CNtoChar {
        /** 区间最小值 */
        private int asciiStart;
        /** 区间最大值 */
        private int asciiEnd;
        /** 对应字母 */
        private char code;
    }
}
