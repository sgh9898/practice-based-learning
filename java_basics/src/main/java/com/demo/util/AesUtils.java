package com.demo.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AES 加密/解密工具
 * <br> {@link #DEFAULT_KEY_STR} 与 {@link #DEFAULT_IV_STR} 在更换项目时必须重新生成
 *
 * @author Song gh
 * @version 2024/1/19
 */
public class AesUtils {

    /** 十六位密钥(需要前端和后端保持一致), 更换项目时必须重新生成 */
    private static final String DEFAULT_KEY_STR = "pcWGuS2nQF11Sf+y";
    /** 十六位密钥偏移量(需要前端和后端保持一致), 更换项目时必须重新生成 */
    private static final String DEFAULT_IV_STR = "kvJRbJz7x5ycy+4V";

    /** AES 加密/解密算法 */
    private static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";

//    /** 生成随机 Key 与 IV */
//    public static void main(String[] args) {
//        System.out.println("随机 Key: " + generateRandomStr(16));
//        System.out.println("随机 IV: " + generateRandomStr(16));
//    }

    /**
     * [AES 解密] 使用默认的 Key 与 IV
     *
     * @param encryptedStr base64 加密后的数据
     * @return 解密后的内容; 输入为空则输出为 空白string
     */
    public static String aesDecrypt(String encryptedStr) {
        if (StringUtils.isBlank(encryptedStr)) {
            return "";
        }
        try {
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            // iv
            byte[] ivBytes = DEFAULT_IV_STR.getBytes(StandardCharsets.UTF_8);
            IvParameterSpec iv = new IvParameterSpec(ivBytes);
            // key
            SecretKey secretKey = new SecretKeySpec(DEFAULT_KEY_STR.getBytes(), "AES");

            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            byte[] decryptBytes = cipher.doFinal(Base64.decodeBase64(encryptedStr));
            return new String(decryptBytes, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            throw new IllegalArgumentException("AES 解密失败", e);
        }
    }

    /**
     * [AES 解密] 使用 String 格式的 Key, 不使用 IV
     * <br> 通常用于 Sharding JDBC 解密
     *
     * @param encryptedStr base64 加密后的数据
     * @param keyStr       String 格式的 {@link SecretKey}, 必须与加密时相同
     * @return 解密后的内容; 输入为空则输出为 空白string
     */
    public static String aesDecryptByKeyStr(String encryptedStr, String keyStr) {
        if (StringUtils.isBlank(encryptedStr)) {
            return "";
        }
        byte[] result;
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(Arrays.copyOf(DigestUtils.sha1(keyStr), 16), "AES"));
            result = cipher.doFinal(java.util.Base64.getDecoder().decode(encryptedStr.trim()));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            throw new IllegalArgumentException("AES 解密失败", e);
        }
        return new String(result, StandardCharsets.UTF_8);
    }

    /**
     * [AES 解密] 使用 String 格式的 Key 与 IV
     *
     * @param encryptedStr base64 加密后的数据
     * @param keyStr       String 格式的 {@link SecretKey}, 必须与加密时相同
     * @param ivStr        String 格式的 {@link IvParameterSpec}, 必须与加密时相同
     * @return 解密后的内容; 输入为空则输出为 空白string
     */
    public static String aesDecryptByKeyStrAndIvStr(String encryptedStr, String keyStr, String ivStr) {
        if (StringUtils.isBlank(encryptedStr)) {
            return "";
        }
        try {
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            // iv
            byte[] ivBytes = ivStr.getBytes(StandardCharsets.UTF_8);
            IvParameterSpec iv = new IvParameterSpec(ivBytes);
            // key
            SecretKey secretKey = new SecretKeySpec(keyStr.getBytes(), "AES");

            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            byte[] decryptBytes = cipher.doFinal(Base64.decodeBase64(encryptedStr));
            return new String(decryptBytes, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            throw new IllegalArgumentException("AES 解密失败", e);
        }
    }

    /**
     * [AES 解密] 使用标准 Key 与 IV
     *
     * @param encryptedStr base64 加密后的数据
     * @param key          {@link SecretKey}, 必须与加密时相同
     * @param iv           {@link IvParameterSpec}, 必须与加密时相同
     * @return 解密后的内容; 输入为空则输出为 空白string
     */
    public static String aesDecryptByKeyAndIv(String encryptedStr, SecretKey key, IvParameterSpec iv) {
        if (StringUtils.isBlank(encryptedStr)) {
            return "";
        }
        try {
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            byte[] decryptBytes = cipher.doFinal(Base64.decodeBase64(encryptedStr));
            return new String(decryptBytes, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            throw new IllegalArgumentException("AES 解密失败", e);
        }
    }

    /**
     * [AES 加密] 使用默认的 Key 与 IV
     *
     * @param plainText 明文数据
     * @return 加密后的内容; 输入为空则输出为 空白string
     */
    public static String aesEncrypt(String plainText) {
        if (StringUtils.isBlank(plainText)) {
            return "";
        }
        byte[] encryptedStr;
        try {
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            // iv
            byte[] ivBytes = DEFAULT_IV_STR.getBytes(StandardCharsets.UTF_8);
            IvParameterSpec iv = new IvParameterSpec(ivBytes);
            // key
            SecretKey secretKey = new SecretKeySpec(DEFAULT_KEY_STR.getBytes(), "AES");

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            encryptedStr = cipher.doFinal(plainText.getBytes());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            throw new IllegalArgumentException("AES 加密失败", e);
        }
        return java.util.Base64.getEncoder().encodeToString(encryptedStr);
    }

    /**
     * [AES 加密] 使用 String 格式的 Key 与 IV
     *
     * @param plainText 明文数据
     * @param keyStr    String 格式的 {@link SecretKey}, 解密时相同
     * @param ivStr     String 格式的 {@link IvParameterSpec}, 解密时相同
     * @return 加密后的内容; 输入为空则输出为 空白string
     */
    public static String aesEncryptByKeyStrAndIvStr(String plainText, String keyStr, String ivStr) {
        byte[] encryptedStr;
        try {
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            // iv
            byte[] ivBytes = ivStr.getBytes(StandardCharsets.UTF_8);
            IvParameterSpec iv = new IvParameterSpec(ivBytes);
            // key
            SecretKey secretKey = new SecretKeySpec(keyStr.getBytes(), "AES");

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            encryptedStr = cipher.doFinal(plainText.getBytes());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            throw new IllegalArgumentException("AES 加密失败", e);
        }
        return java.util.Base64.getEncoder().encodeToString(encryptedStr);
    }

    /**
     * [AES 加密] 使用标准 Key 与 IV
     *
     * @param plainText 明文数据
     * @param key       {@link SecretKey}, 解密时相同
     * @param iv        {@link IvParameterSpec}, 解密时相同
     * @return 加密后的内容; 输入为空则输出为 空白string
     */
    public static String aesEncryptByKeyStrAndIvStr(String plainText, SecretKey key, IvParameterSpec iv) {
        byte[] encryptedStr;
        try {
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            encryptedStr = cipher.doFinal(plainText.getBytes());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            throw new IllegalArgumentException("AES 加密失败", e);
        }
        return java.util.Base64.getEncoder().encodeToString(encryptedStr);
    }

// ------------------------------ Private ------------------------------

    private AesUtils() {
    }

    /** 随机生成指定长度的 string */
    private static String generateRandomStr(int length) {
        SecureRandom random = new SecureRandom();
        int strLength = (int) Math.round(length * 4.0 / 3.0);
        List<Character> tokenList = random.ints(strLength, 32, 1025).mapToObj(i -> (char) i).collect(Collectors.toList());
        Collections.shuffle(tokenList);
        String token = tokenList.stream().map(String::valueOf).collect(Collectors.joining());
        String base64Token = java.util.Base64.getEncoder().encodeToString(token.getBytes());
        return base64Token.substring(0, Math.min(length, base64Token.length()));
    }
}