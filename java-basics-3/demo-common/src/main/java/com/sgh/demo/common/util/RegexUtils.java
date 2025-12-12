package com.sgh.demo.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式相关工具
 *
 * @author Song gh
 * @since 2024/1/18
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RegexUtils {

    /** 不可见的 UNICODE 字符集 */
    private static final String INVISIBLE_UNICODE = "[\\u200b-\\u200f]|[\\u200e-\\u200f]|[\\u202a-\\u202e]|[\\u2066-\\u2069]|\ufeff|\u06ec";

    /** 较短的姓名长度 */
    private static final int SHORT_NAME_LENGTH = 2;

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
        if (StringUtils.isNotEmpty(str)) {
            StringBuilder stringBuilder = new StringBuilder(str);
            stringBuilder.replace(startIdx, endIdx, newStr);
            return stringBuilder.toString();
        }
        return str;
    }

    /** 姓名脱敏, 两位时仅保留首位, 两位以上保留首尾两位 */
    public static String nameMask(String name) {
        if (StringUtils.isBlank(name)) {
            return name;
        }
        if (name.length() == SHORT_NAME_LENGTH) {
            return name.charAt(0) + "*";
        } else {
            return name.charAt(0) + "*" + name.charAt(name.length() - 1);
        }
    }

    /** 手机号码脱敏, 保留前三后四 */
    public static String mobileMask(String mobile) {
        if (StringUtils.isNotEmpty(mobile)) {
            mobile = mobile.replaceAll("(\\d{3})\\d*(\\d{4})", "$1****$2");
        }
        return mobile;
    }

    /** 去除不可见的 unicode 字符 */
    public static String trimUnicode(String source) {
        Pattern compile = Pattern.compile(INVISIBLE_UNICODE);
        Matcher matcher = compile.matcher(source);
        if (matcher.find()) {
            return matcher.replaceAll("");
        }
        return source;
    }

    /** 去除空格 */
    public static String trimSpace(String source) {
        return source.replaceAll("\\s*", "");
    }
}
