package com.demo.util;

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

/**
 * AES 加密/解密工具
 *
 * @author Song gh on 2023/3/29.
 */
public class AesUtils {

    /** 十六位密钥(需要前端和后端保持一致) */
    private static final String DEFAULT_KEY_STR = "pcWGuS2nQF11Sf+y";

    /** 十六位密钥偏移量(需要前端和后端保持一致) */
    private static final String DEFAULT_IV_STR = "kvJRbJz7x5ycy+4V";

    /** AES 加密/解密算法 */
    private static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";

    /**
     * AES 解密, 使用默认的 Key 与 IV
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
            throw new RuntimeException(e);
        }
    }

    /**
     * AES 解密, 使用 String 格式的 Key
     *
     * @param encryptedStr base64 加密后的数据
     * @param keyStr       String 格式的 {@link SecretKey}, 必须与加密时相同
     */
    public static String aesDecryptByKeyStr(String encryptedStr, String keyStr) {
        if (StringUtils.isBlank(encryptedStr)) {
            return "";
        }
        try {
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            // key
            SecretKey secretKey = new SecretKeySpec(Arrays.copyOf(DigestUtils.sha1(keyStr), 16), "AES");

            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptBytes = cipher.doFinal(Base64.decodeBase64(encryptedStr));
            return new String(decryptBytes, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * AES 解密, 使用 String 格式的 Key 与 IV
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
            throw new RuntimeException(e);
        }
    }

    /**
     * AES 解密, 使用标准 Key 与 IV
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
            throw new RuntimeException(e);
        }
    }

    /**
     * AES 加密, 使用默认的 Key 与 IV
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
            throw new RuntimeException(e);
        }
        return java.util.Base64.getEncoder().encodeToString(encryptedStr);
    }

    /**
     * AES 加密, 使用 String 格式的 Key 与 IV
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
            throw new RuntimeException(e);
        }
        return java.util.Base64.getEncoder().encodeToString(encryptedStr);
    }

    /**
     * AES 加密, 使用标准 Key 与 IV
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
            throw new RuntimeException(e);
        }
        return java.util.Base64.getEncoder().encodeToString(encryptedStr);
    }

    /**
     * 随机生成 Key
     *
     * @param keySize key 长度: 128, 192, or 256
     */
    public static SecretKey getRandomKey(int keySize) {
        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        keyGenerator.init(keySize);
        return keyGenerator.generateKey();
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
            throw new RuntimeException(e);
        }
    }

    /** Key 转为 String */
    public static String secretKeyToStr(SecretKey secretKey) {
        byte[] rawData = secretKey.getEncoded();
        return java.util.Base64.getEncoder().encodeToString(rawData);
    }

    /** String 转为 Key */
    public static SecretKey StrToSecretKey(String keyStr) {
        byte[] decodedKey = keyStr.getBytes();
        return new SecretKeySpec(decodedKey, "AES");
    }

    /** 随机生成 IV */
    public static IvParameterSpec getRandomIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    /** String 转为 IV */
    public static IvParameterSpec strToIv(String ivStr) {
        byte[] iv = ivStr.getBytes();
        return new IvParameterSpec(iv);
    }

    /** IV 转为 String */
    public static String ivToStr(IvParameterSpec iv) {
        return new String(iv.getIV());
    }
}