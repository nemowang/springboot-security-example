package com.nemo.consumer;

import com.nemo.common.util.AESUtil;
import lombok.SneakyThrows;
import org.junit.Test;

/**
 * @Author Nemo
 * @Description
 * @Date 2020/12/2 17:17
 */
public class AESUtilTest {

    private static final String aesKey = "AAAAAAAAAA0AAL8AAOgAuA==";

    @Test
    public void decryptTest() {
        String encryptStr = "263346164b80e59948a1ac4a633bca3f6a5e644f8d1d51d587f109162d3a89ad";
        String key = "AAAAAAAAAA0AAL8AAOgAuA==";
        String decrypt = AESUtil.decrypt(encryptStr, key);
        System.out.println(decrypt);
    }

    @Test
    public void encryptTest() {
        String str = "hello from server side";
        String encrypt = AESUtil.encrypt(str, aesKey);
        System.out.println(encrypt);
    }

    @Test
    @SneakyThrows
    public void goThroughTest() {
        /*String key = aesUtil.generateDesKey(128);
        System.out.println("key=" + key);

        String str = "My name is Li Hua.";
        String encrypt = aesUtil.encrypt(str, key);
        System.out.println("encrypt=" + encrypt);

        String decrypt = aesUtil.decrypt(encrypt, key);
        System.out.println("decrypt=" + decrypt);*/

        String key = AESUtil.generateKey();
        System.out.println("key=" + key);

        String str = "My name is Li Hua.";
        String encrypt = AESUtil.encrypt(str, key);
        System.out.println("encrypt=" + encrypt);

        String decrypt = AESUtil.decrypt(encrypt, key);
        System.out.println("decrypt=" + decrypt);
    }
}
