package com.collin.demo.common.util;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 拼音工具
 *
 * @author Song gh on 2023/5/8.
 */
public class PinYinUtils {

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

    /** 获取中文首字母 */
    public static String getHeadLetters(String cnStr) {
        try {
            StringBuilder pyStrBd = new StringBuilder();
            for (char c : cnStr.toCharArray()) {
                pyStrBd.append(getChar(c));
            }
            return standardFilter(pyStrBd.toString().replaceAll("\\s*", ""));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /** 获取中文首字母, 小写 */
    public static String getHeadLettersLowerCase(String cnStr) {
        try {
            StringBuilder pyStrBd = new StringBuilder();
            for (char c : cnStr.toCharArray()) {
                pyStrBd.append(getChar(c));
            }
            return standardFilter(pyStrBd.toString().replaceAll("\\s*", "").toLowerCase());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /** 过滤特殊字符 */
    private static String standardFilter(String str) {
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
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
}
