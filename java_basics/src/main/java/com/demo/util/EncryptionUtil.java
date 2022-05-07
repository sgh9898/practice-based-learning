package com.demo.util;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * [加密/解密]工具
 *
 * @author Song gh on 2022/5/7.
 */
public class EncryptionUtil {

    /**
     * 使用 Mac 算法进行加密
     *
     * @param content    需要加密的内容
     * @param encryptKey key
     * @param algorithm  算法名, e.g. HmacSHA1
     * @return hex string
     */
    public static String macEncryption(String content, String encryptKey, String algorithm) {
        String cipher = "";
        try {
            byte[] data = encryptKey.getBytes(StandardCharsets.UTF_8);
            // 生成 key
            SecretKey secretKey = new SecretKeySpec(data, algorithm);
            // 生成指定 Mac 算法的对象
            Mac mac = Mac.getInstance(algorithm);
            // 用给定 key 初始化 Mac 对象
            mac.init(secretKey);
            byte[] text = content.getBytes(StandardCharsets.UTF_8);
            byte[] encryptByte = mac.doFinal(text);
            // 转为 hex string
            cipher = bytesToHexStr(encryptByte);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return cipher;
    }

    // bytes --> hex string
    public static String bytesToHexStr(byte[] bytes) {
        StringBuilder hexStr = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            hexStr.append(hex);
        }
        return hexStr.toString();
    }

    // md5 加密 string
    private byte[] simpleMD5(String content) {
        try {
            byte[] btInput = content.getBytes(StandardCharsets.UTF_8);
            // 获得 MD5 摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            return mdInst.digest();
        } catch (Exception e) {
            return new byte[0];
        }
    }
}
