package com.demo.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * AES 加密/解密工具
 *
 * @author Song gh on 2023/3/29.
 */
@Slf4j
public class AESUtils {

    /**
     * 生成随机 key
     *
     * @param keySize key 长度: 128, 192, or 256
     */
    public static SecretKey getKey(int keySize) {
        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            log.error("生成随机 key 失败", e);
            throw new RuntimeException(e);
        }
        keyGenerator.init(keySize);
        return keyGenerator.generateKey();
    }

    /**
     * 根据明文密码生成 key
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
            log.error("根据明文密码生成 key 失败", e);
            throw new RuntimeException(e);
        }
    }

    /** 将 key 转为 string */
    public static String convertSecretKeyToString(SecretKey secretKey) {
        byte[] rawData = secretKey.getEncoded();
        return Base64.getEncoder().encodeToString(rawData);
    }

    /** 将 string 转为 key */
    public static SecretKey convertStringToSecretKey(String encodedKey) {
        byte[] decodedKey = encodedKey.getBytes();
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    /** 生成初始向量 */
    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    /** 将 string 转为 初始向量 */
    public static IvParameterSpec convertStringToIv(String ivString) {
        byte[] iv = ivString.getBytes();
        return new IvParameterSpec(iv);
    }

    /** 将 初始向量 转为 string */
    public static String convertIvToString(IvParameterSpec iv) {
        return new String(iv.getIV());
    }

    /**
     * 数据加密
     *
     * @param inputText 需要加密的数据
     * @param key       {@link SecretKey}
     * @param iv        {@link IvParameterSpec}
     */
    public static String encrypt(String inputText, SecretKey key, IvParameterSpec iv) {
        byte[] cipherText;
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            cipherText = cipher.doFinal(inputText.getBytes());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            log.error("数据加密失败, 原文: {}", inputText, e);
            throw new RuntimeException(e);
        }
        return Base64.getEncoder().encodeToString(cipherText);
    }

    /**
     * 数据解密
     *
     * @param cipherText 加密后的数据
     * @param key        {@link SecretKey}, 必须与加密时相同
     * @param iv         {@link IvParameterSpec}, 必须与加密时相同
     */
    public static String decrypt(String cipherText, SecretKey key, IvParameterSpec iv) {
        byte[] plainText;
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            log.error("数据解密失败, 密文: {}", cipherText, e);
            throw new RuntimeException(e);
        }
        return new String(plainText);
    }
}
