package com.sgh.demo.general.temp;

import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * AES加密解密工具类
 *
 * @author xuq     wine_7@yeah.net
 * @date 2024-10-29
 */
public class TempAESUtil {

    private static final String ALGORITHM = "AES";
    private static final byte[] KEY = "kN7xI1hS5aF5dJ9j".getBytes();

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        String data = "1";
        String encrypted = encrypt(data);
        System.out.println("Encrypted: " + encrypted);
        System.err.println(System.currentTimeMillis() - start);
        String decrypted = tryDecrypt(encrypted);
        System.out.println("Decrypted: " + decrypted);
        System.err.println(System.currentTimeMillis() - start);
    }

    /**
     * 加密
     *
     * @param data
     */
    public static String encrypt(String data) {

        try {
            SecretKeySpec keySpec = new SecretKeySpec(KEY, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(data.getBytes());
            String encode = Base64.getEncoder().encodeToString(encrypted);
            return encode;
        } catch (Exception e) {
            System.err.println("隐私信息加密异常，入参：" + data + "，异常信息：" + e.getMessage());
            return data;
        }
    }

    /**
     * 尝试解密, 失败会返回原数据
     *
     * @param rawData 原始数据, 加密或未加密均可
     */
    public static String tryDecrypt(String rawData) {
        if (StringUtils.isEmpty(rawData)) {
            return rawData;
        }
        try {
            SecretKeySpec keySpec = new SecretKeySpec(KEY, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decoded = Base64.getDecoder().decode(rawData);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted);
        } catch (Exception e) {
            System.err.println("隐私信息解密异常，入参：" + rawData + "，异常信息：" + e.getMessage());
            return rawData;
        }
    }

}
