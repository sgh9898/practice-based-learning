package com.demo.util;

import com.demo.exception.BaseException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AES 加密/解密工具
 *
 * @author Song gh on 2023/3/29.
 */
public class AesUtils {

    /** 十六位密钥(需要前端和后端保持一致), 更换项目时必须重新生成 */
    private static final String DEFAULT_KEY_STR = "pcWGuS2nQF11Sf+y";

    /** 十六位密钥偏移量(需要前端和后端保持一致), 更换项目时必须重新生成 */
    private static final String DEFAULT_IV_STR = "kvJRbJz7x5ycy+4V";

    /** AES 加密/解密算法 */
    private static final String AES_ALGORITHM = "AES/GCM/NoPadding”";

    /** 生成随机 Key 与 IV */
    public static void main(String[] args) {
        System.out.println("随机 Key: " + generateRandomStr(16));
        System.out.println("随机 IV: " + generateRandomStr(16));
    }

    /**
     * [AES 解密] 使用默认的 Key 与 IV
     *
     * @param encryptedStr base64 加密后的数据
     */
    public static String aesDecrypt(String encryptedStr) {
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
        } catch (Exception e) {
            throw new BaseException("AES 解密失败", e);
        }
    }

    /**
     * [AES 解密] 使用 String 格式的 Key, 不使用 IV
     *
     * @param encryptedStr base64 加密后的数据
     * @param keyStr       String 格式的 {@link SecretKey}, 必须与加密时相同
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
            throw new BaseException("AES 解密失败", e);
        }
        return new String(result, StandardCharsets.UTF_8);
    }

    /**
     * [AES 解密] 使用 String 格式的 Key 与 IV
     *
     * @param encryptedStr base64 加密后的数据
     * @param keyStr       String 格式的 {@link SecretKey}, 必须与加密时相同
     * @param ivStr        String 格式的 {@link IvParameterSpec}, 必须与加密时相同
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
            throw new BaseException("AES 解密失败", e);
        }
    }

    /**
     * [AES 解密] 使用标准 Key 与 IV
     *
     * @param encryptedStr base64 加密后的数据
     * @param key          {@link SecretKey}, 必须与加密时相同
     * @param iv           {@link IvParameterSpec}, 必须与加密时相同
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
            throw new BaseException("AES 解密失败", e);
        }
    }

    /**
     * [AES 加密] 使用默认的 Key 与 IV
     *
     * @param plainText 明文数据
     */
    public static String aesEncrypt(String plainText) {
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
            throw new BaseException("AES 加密失败", e);
        }
        return java.util.Base64.getEncoder().encodeToString(encryptedStr);
    }

    /**
     * [AES 加密] 使用 String 格式的 Key 与 IV
     *
     * @param plainText 明文数据
     * @param keyStr    String 格式的 {@link SecretKey}, 解密时相同
     * @param ivStr     String 格式的 {@link IvParameterSpec}, 解密时相同
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
            throw new BaseException("AES 加密失败", e);
        }
        return java.util.Base64.getEncoder().encodeToString(encryptedStr);
    }

    /**
     * [AES 加密] 使用标准 Key 与 IV
     *
     * @param plainText 明文数据
     * @param key       {@link SecretKey}, 解密时相同
     * @param iv        {@link IvParameterSpec}, 解密时相同
     */
    public static String aesEncryptByKeyStrAndIvStr(String plainText, SecretKey key, IvParameterSpec iv) {
        byte[] encryptedStr;
        try {
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            encryptedStr = cipher.doFinal(plainText.getBytes());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            throw new BaseException("AES 加密失败", e);
        }
        return java.util.Base64.getEncoder().encodeToString(encryptedStr);
    }

    /**
     * 根据明文密码生成 Key
     *
     * @param password 明文密码
     * @param salt     盐值, null/空时使用随机数代替
     * @param keySize  key 长度: 128, 192, or 256
     */
    public static SecretKey getKeyFromPassword(String password, String salt, int keySize) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec;
            // salt 为空时随机生成
            if (StringUtils.isBlank(salt)) {
                byte[] saltBytes = new byte[100];
                SecureRandom random = new SecureRandom();
                random.nextBytes(saltBytes);
                spec = new PBEKeySpec(password.toCharArray(), saltBytes, 65536, keySize);
            } else {
                spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, keySize);
            }
            return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new BaseException("生成 AES Key 失败", e);
        }
    }

    /** Key 转为 String */
    public static String secretKeyToStr(SecretKey secretKey) {
        byte[] rawData = secretKey.getEncoded();
        return java.util.Base64.getEncoder().encodeToString(rawData);
    }

    /** String 转为 Key */
    public static SecretKey strToSecretKey(String keyStr) {
        byte[] decodedKey = keyStr.getBytes();
        return new SecretKeySpec(decodedKey, "AES");
    }

    /** String 转为 IV */
    public static IvParameterSpec strToIv(String ivStr) {
        byte[] iv = ivStr.getBytes();
        return new IvParameterSpec(iv);
    }

    /** IV 转为 String */
    public static String ivToStr(IvParameterSpec iv) {
        return new String(iv.getIV(), StandardCharsets.UTF_8);
    }

// ------------------------------ Private ------------------------------

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