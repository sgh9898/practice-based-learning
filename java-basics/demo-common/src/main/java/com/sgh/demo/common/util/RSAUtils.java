package com.sgh.demo.common.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * RSA 加密/解密工具
 *
 * @author Song gh on 2022/5/7.
 */
public class RSAUtils {

    /** RSA 密钥长度 */
    public static final int RSA_KEY_SIZE = 1024;
    /** RSA 公钥 */
    private PublicKey rsaPublicKey;
    /** RSA 私钥 */
    private PrivateKey rsaPrivateKey;

    /** constructor */
    public RSAUtils() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(RSA_KEY_SIZE);
            // 生成 key
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            this.rsaPrivateKey = keyPair.getPrivate();
            this.rsaPublicKey = keyPair.getPublic();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /** 生成默认长度的 RSA 密钥 */
    public static Map<String, String> generateRsa() {
        return generateRsaWithKeySize(RSA_KEY_SIZE);
    }

    /**
     * 生成 RSA 密钥
     *
     * @param keySize RSA 密钥长度(64 的倍数, 在 512 ~ 65536 之间)
     */
    public static Map<String, String> generateRsaWithKeySize(int keySize) {
        Map<String, String> keyPairs = new HashMap<>();
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(keySize);
            // 生成 key
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PrivateKey rsaPrivateKey = keyPair.getPrivate();
            PublicKey rsaPublicKey = keyPair.getPublic();

            // 转为 string
            String privateStr = Base64.encodeBase64String(rsaPrivateKey.getEncoded());
            String publicStr = Base64.encodeBase64String(rsaPublicKey.getEncoded());

            keyPairs.put("privateKey", privateStr);
            keyPairs.put("publicKey", publicStr);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return keyPairs;
    }

    public byte[] rsaEncryption(String src) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(src));
            this.rsaPublicKey = keyFactory.generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

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
