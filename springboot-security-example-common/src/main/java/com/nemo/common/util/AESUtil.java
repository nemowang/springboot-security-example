package com.nemo.common.util;

import cn.hutool.core.util.StrUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * @description：AES加解密、生成密钥
 */
public class AESUtil {

    /**
     * 加密算法AES
     */
    private static final String KEY_ALGORITHM = "AES";

    /**
     * 算法名称/加密模式/数据填充方式
     * AES/CBC/PKCS5Padding
     */
    private static final String ALGORITHMS = "AES/CBC/PKCS5Padding";

    /**
     * 生成AES密钥
     * 默认长度为128
     * @return 密钥Base64字符串
     */
    public static String generateKey() {
        String key = StringUtils.EMPTY;
        try {
            key = generateAesKey(128);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return key;
    }

    /**
     * 生成AES密钥
     * @param length 自定义长度
     * @return 密钥Base64字符串
     */
    public static String generateKey(Integer length) {
        String key = StringUtils.EMPTY;
        try {
            key = generateAesKey(length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return key;
    }

    /**
     * 解密
     * @param encryptStr 密文
     * @param key 密钥
     * @return 解密得到的原文
     */
    public static String decrypt(String encryptStr, String key) {
        String result = StringUtils.EMPTY;
        try {
            byte[] keyBytes = Base64.decodeBase64(key);
            byte[] dataBytes = Hex.decodeHex(encryptStr.toCharArray());
            byte[] resultBytes = AES_CBC_Decrypt(dataBytes, keyBytes, keyBytes);
            result = new String(resultBytes);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 加密
     * @param content 待加密原文
     * @param key 密钥
     * @return 加密得到的密文
     */
    public static String encrypt(String content, String key) {
        String enc = StringUtils.EMPTY;
        try {
            byte[] keyBytes = Base64.decodeBase64(key);
            byte[] dataBytes = StrUtil.bytes(content, StandardCharsets.UTF_8);
            byte[] resultBytes = AES_CBC_Encrypt(dataBytes, keyBytes, keyBytes);
            // enc = new String(resultBytes);
            enc = Hex.encodeHexString(resultBytes);
            return enc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return enc;
    }


    private static byte[] AES_CBC_Decrypt(byte[] data, byte[] key, byte[] iv) throws Exception {
        Cipher cipher = getCipher(Cipher.DECRYPT_MODE, key, iv);
        return cipher.doFinal(data);
    }

    private static byte[] AES_CBC_Encrypt(byte[] data, byte[] key, byte[] iv) throws Exception {
        Cipher cipher = getCipher(Cipher.ENCRYPT_MODE, key, iv);
        return cipher.doFinal(data);
    }


    private static Cipher getCipher(int mode, byte[] key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHMS);
        //因为AES的加密块大小是128bit(16byte), 所以key是128、192、256bit无关
        //System.out.println("cipher.getBlockSize()： " + cipher.getBlockSize());
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, KEY_ALGORITHM);
        cipher.init(mode, secretKeySpec, new IvParameterSpec(iv));

        return cipher;
    }

    private static String generateAesKey(Integer length) throws Exception {
        //实例化
        KeyGenerator kgen = null;
        kgen = KeyGenerator.getInstance("AES");
        //设置密钥长度
        kgen.init(length);
        //生成密钥
        SecretKey skey = kgen.generateKey();
        //密钥的二进制编码
        byte[] keyBytes = skey.getEncoded();
        //返回Base64编码后的密钥字符串
        return Base64.encodeBase64String(keyBytes);
    }

}
