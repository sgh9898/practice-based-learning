package com.collin.demo.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * MD5 加密工具
 *
 * @author Song gh on 2024/1/17.
 */
public class MD5Utils {

    /** 生成 32 位小写 MD5 */
    public static String md5Lower(String str) {
        return DigestUtils.md5Hex(str);
    }

    /** 生成 32 位大写 MD5 */
    public static String md5Upper(String str) {
        return DigestUtils.md5Hex(str).toUpperCase();
    }

    /** 对文件进行加密, 生成 32 位小写 MD5 */
    public static String fileMd5Lower(String filePath) {
        try (InputStream is = Files.newInputStream(Paths.get(filePath))) {
            return DigestUtils.md5Hex(is);
        } catch (IOException e) {
            throw new IllegalArgumentException("MD5 加密失败, 文件路径: " + filePath, e);
        }
    }

    private MD5Utils() {
    }
}
