package com.nemo.provider.util;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * rsa 加解密相关
 */
@Slf4j
public class RSAEncrypt {
    private static final String CHARSET = "UTF-8";
    //密钥算法
    private static final String ALGORITHM_RSA = "RSA";
    // 加密的最大秘文
    private static final int MAX_ENCRYPE_BLOACK = 117;
    // 解密的最大秘文
    private static final int MAX_DECRYPE_BLOACK = 128;
    /**
     * 签名算法
     */
    private static final String ALGORITHM_RSA_SIGN = "SHA1WithRSA";

    /**
     * 用于封装随机产生的公钥与私钥
     */
    private static Map<Integer, String> keyMap = new HashMap<Integer, String>();

    /**
     * 随机生成密钥对
     *
     * @throws NoSuchAlgorithmException
     */
    public static Map<Integer, String> genKeyPair(int size) throws NoSuchAlgorithmException {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        // 初始化密钥对生成器，密钥大小为96-1024位
        keyPairGen.initialize(size, new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();   // 得到私钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  // 得到公钥
        String publicKeyString = new String(Base64.getEncoder().encode(publicKey.getEncoded()));
        // 得到私钥字符串
        privateKey.getFormat();
//        String privateKeyString = Base64Utils.encodeToString(privateKey.getEncoded());
        String privateKeyString = null;
        try {
            privateKeyString = new String(Base64.getEncoder().encode((privateKey.getEncoded())), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 将公钥和私钥保存到Map
        keyMap.put(0, publicKeyString);  //0表示公钥
        keyMap.put(1, privateKeyString);  //1表示私钥

        return keyMap;
    }

    /**
     * RSA公钥加密
     *
     * @param data      加密字符串
     * @param publicKey 钥
     * @return 铭文
     * @throws Exception 解密过程中的异常信息
     */
    public static String encrypt(String data, String publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM_RSA);
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
        int inputLength = data.getBytes().length;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] temp;
        int i = 0;
        while (inputLength - offSet > 0) {
            if (inputLength - offSet > MAX_ENCRYPE_BLOACK) {
                temp = cipher.doFinal(data.getBytes(), offSet, MAX_ENCRYPE_BLOACK);
            } else {
                temp = cipher.doFinal(data.getBytes(), offSet, inputLength - offSet);
            }
            outputStream.write(temp);
            i++;
            offSet = i * MAX_ENCRYPE_BLOACK;
        }
        byte[] encrypteData = outputStream.toByteArray();
        outputStream.close();
        return Base64.getEncoder().encodeToString(encrypteData);
    }

    /**
     * RSA私钥解密
     *
     * @param str        加密字符串
     * @param privateKey 私钥
     * @return 铭文
     * @throws Exception 解密过程中的异常信息
     */
    public static String decrypt(String str, String privateKey) throws Exception {
        log.info("解密数据了");
        //64位解码加密后的字符串
        String s = str.replaceAll(" ", "").replaceAll("\n", "")
                .replaceAll("\r", "");
        byte[] inputByte = Base64.getDecoder().decode(s.getBytes("GBK"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int inputLen = inputByte.length;
        int offSet = 0;
        byte[] cache;
        int i = 0;
        //base64编码的私钥
        RSAPrivateKey priKey = getPrivateKey(privateKey);
        //RSA解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPE_BLOACK) {
                cache = cipher.doFinal(inputByte, offSet, MAX_DECRYPE_BLOACK);
            } else {
                cache = cipher.doFinal(inputByte, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPE_BLOACK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        String outStr = new String(decryptedData);
        return outStr;
    }

    /**
     * RSA算法使用私钥对数据生成数字签名
     *
     * @param data 待签名的明文字符串
     * @param key  RSA私钥字符串
     * @return RSA私钥签名后的经过Base64编码的字符串
     */
    public static String sign(String data, String key) {
        try {
            //通过PKCS#8编码的Key指令获得私钥对象
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(key));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
            Signature signature = Signature.getInstance(ALGORITHM_RSA_SIGN);
            signature.initSign(privateKey);
            signature.update(data.getBytes(CHARSET));
            return Base64.getEncoder().encodeToString(signature.sign());
        } catch (Exception e) {
            throw new RuntimeException("签名字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * RSA算法使用公钥校验数字签名
     *
     * @param data 参与签名的明文字符串
     * @param key  RSA公钥字符串
     * @param sign RSA签名得到的经过Base64编码的字符串
     * @return true--验签通过,false--验签未通过
     */
    public static boolean doCheck(String data, String key, String sign) {
        try {
            //通过X509编码的Key指令获得公钥对象
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(key));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
            PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
            Signature signature = Signature.getInstance(ALGORITHM_RSA_SIGN);
            signature.initVerify(publicKey);
            signature.update(data.getBytes(CHARSET));
            return signature.verify(Base64.getDecoder().decode(sign));
        } catch (Exception e) {
            throw new RuntimeException("验签字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * @param privateKey 私钥串
     * @return
     */
    private static RSAPrivateKey getPrivateKey(String privateKey) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(privateKey);
        return (RSAPrivateKey) KeyFactory.getInstance(ALGORITHM_RSA).generatePrivate(new PKCS8EncodedKeySpec(decoded));
    }

    /**
     * @param publicKey 获取公钥串
     * @return
     */
    private static RSAPublicKey getPublicKey(String publicKey) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
        byte[] decdeKey = Base64.getDecoder().decode(publicKey.getBytes());
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decdeKey);
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }


    public static void main(String args[]) {
        try {
//            String encrypted =
//                    RSAEncrypt.encrypt("123456", "MIGdMA0GCSqGSIb3DQEBAQUAA4GLADCBhwKBgQDJ5QyA5mzIamuHQiudgfD2qpI3JgcVaCnEyFU2ml97FpAudV83tO2oIfCzbbN9TkZ1hkr06hMxfx6gt4Z3Kb/r1FngNVqlVkxqMzqB49MuE6QQ+LOdmjmSmNCwLxg9l/DRALnWd6pSGgTmuEYptITGbXWehWE6V1dIn/bzzwoqQwIBAw==");
//            System.out.println("encrypted: " + encrypted);
//            encrypted = "Bc4fJppZL8hXdQlAYG01kj8r33B+VRksMn8AG0MdFyWePtrAP85LO+OIBxjjp2MtseQotwZx6SY6cs8sb3qbaxHvI9kpaJCzCyUaGhlNbMFiGITJ/10xtJT8TcJyqvYsiaJj7hGd4L4JsV+dDS0IUSIwRC6dqFx8grxsVhBhyNtO4SZRdpLkW5HyIOB9uNiSr+RNRpai2RWWWyscnaNIMtx1V90PvJ4KqPMRUTVbauueYBekkmDbmk6OQxA1sEUzhWO4a07BKyxABz2eJ60J9T8wIv+gaMK8QwFD6mrUo9maml3BNFl5T7ml5QAHX82GPbm3ggmxF/es3mZFxbyfkxmxy4MmoCZobSifk698o19GihdSQ0EzYpAoTY113CT3zHyPFtEFnVcGazFprUXu2lekiRFwfBn7HvF2rN9GVWbEch1aPdNE2HCVw38wqejdNZ5vE9wur4arjqjIz6iWrsuKDjBh/Y8/C7Snrk1EsiI1BFd434GyDnsz0QsnvQVm";
//            String decrypted =
//                    RSAEncrypt.decrypt(encrypted, "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAMnlDIDmbMhqa4dCK52B8PaqkjcmBxVoKcTIVTaaX3sWkC51Xze07agh8LNts31ORnWGSvTqEzF/HqC3hncpv+vUWeA1WqVWTGozOoHj0y4TpBD4s52aOZKY0LAvGD2X8NEAudZ3qlIaBOa4Rim0hMZtdZ6FYTpXV0if9vPPCipDAgEDAoGAIaYswCZndrxnQTWx75WoKRxts9ur2OaxoMwOM8RlPy5tXROP3p4nnAWoHeed6je2aOu3KNGt3ZUvxXPrvob1Uazxkh/UmecY2S64ZNm6hf/WgTvNq4wRdlFLk9TX8pHHNuEvZtCSDu1avU2Rpsird3FF5g6LiCGYCq2uCyDFnfsCQQDkEwLOMhPU5MoWZ/YCcFIvt9I3huYtCtLMpHKx7HeY3GytdpoFrSaPEmxvj9YF2aMsvoo0qAgXszDNf0sYP81xAkEA4p1wqCz2FtKJBIAuxwO35OU3WlqyJMX55GbGgBwWlGkbDCbTjpDR+tIKd0xq+qZWmROv+XQBdhPXwGNl8iio8wJBAJgMrIl2t+NDMWRFTqxK4XUlNs+vRB4HNzMYTHadpRCS8x5PEVkeGbS28vUKjq6RF3MpsXhwBWUiIIj/h2V/3ksCQQCXE6BwHflkjFtYVXSErSVDQ3o8PHbDLqaYRIRVaA8Nm2ddbze0YIv8jAb6MvH8buRmDR/7oqukDTqAQkP2xcX3AkA+Plw6FKiwGqPBOkF25OYZyWxGTSnb3m0Zdb2MSBVUXbpB7ysao5CKX0Jdtpvn+gAOU+SBjOMAII0q8lj3q11N");
//            System.out.println("decrypted: " + decrypted);
            genKeyPair(1024);
//            Map<Integer, String> keyMap = genKeyPair(1024);
//            String encrypted =
//                    RSAEncrypt.encrypt("123456", keyMap.get(0));
//
//            System.out.println("encrypted: " + encrypted);
////            encrypted = "Bc4fJppZL8hXdQlAYG01kj8r33B+VRksMn8AG0MdFyWePtrAP85LO+OIBxjjp2MtseQotwZx6SY6cs8sb3qbaxHvI9kpaJCzCyUaGhlNbMFiGITJ/10xtJT8TcJyqvYsiaJj7hGd4L4JsV+dDS0IUSIwRC6dqFx8grxsVhBhyNtO4SZRdpLkW5HyIOB9uNiSr+RNRpai2RWWWyscnaNIMtx1V90PvJ4KqPMRUTVbauueYBekkmDbmk6OQxA1sEUzhWO4a07BKyxABz2eJ60J9T8wIv+gaMK8QwFD6mrUo9maml3BNFl5T7ml5QAHX82GPbm3ggmxF/es3mZFxbyfkxmxy4MmoCZobSifk698o19GihdSQ0EzYpAoTY113CT3zHyPFtEFnVcGazFprUXu2lekiRFwfBn7HvF2rN9GVWbEch1aPdNE2HCVw38wqejdNZ5vE9wur4arjqjIz6iWrsuKDjBh/Y8/C7Snrk1EsiI1BFd434GyDnsz0QsnvQVm";
//            String decrypted =
//                    RSAEncrypt.decrypt(encrypted, keyMap.get(1));
//            System.out.println("decrypted: " + decrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
