package com.collin.demo.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Base64 加密/解密工具
 *
 * @author Song gh
 * @version 2024/1/4
 */
public class Base64Utils {

    /** Base64 加密, UTF-8 */
    public static String encode(String inputStr) {
        return Base64.getEncoder().encodeToString(inputStr.getBytes(StandardCharsets.UTF_8));
    }

    /** Base64 加密 */
    public static String encode(String inputStr, Charset charset) {
        return Base64.getEncoder().encodeToString(inputStr.getBytes(charset));
    }

    /** Base64 解密, UTF-8 */
    public static String decode(String encodedStr) {
        return new String(Base64.getDecoder().decode(encodedStr), StandardCharsets.UTF_8);
    }

    /** Base64 解密 */
    public static String decode(String encodedStr, Charset charset) {
        return new String(Base64.getDecoder().decode(encodedStr), charset);
    }

    private Base64Utils() {
    }
}
