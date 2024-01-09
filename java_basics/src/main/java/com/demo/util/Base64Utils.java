package com.demo.util;

import java.util.Base64;

/**
 * Base64 加密/解密工具
 * @author Song gh on 2024/1/4.
 */
public class Base64Utils {

    /** Base64 加密 */
    public static String encode(String inputStr) {
        return Base64.getEncoder().encodeToString(inputStr.getBytes());
    }

    /** Base64 解密 */
    public static String decode(Striung encodedStr) {
        return new String(Base64.getDecoder().decode(encodedStr));
    }
}
