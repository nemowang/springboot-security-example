package com.nemo.consumer;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.nemo.common.util.AESUtil;
import com.nemo.common.util.RSAUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Nemo
 * @Description API加解密完整调用demo
 * @Date 2020/12/12 13:15
 */
@Slf4j
public class CipherTest {

    private static final String APPID = "100001";
    private static final String AES_KEY = "DHbpdfjEAwc6Nx5og32IrA==";

    /**
     * API加解密完整调用demo
     */
    @Test
    @SneakyThrows
    public void test() {
        // step1. 获取原始报文(未加密报文)
        String jsonStr = this.getJsonStr();

        // step2. 对原始报文进行AES对称加密
        String bizContent = AESUtil.encrypt(jsonStr, AES_KEY);
        // log.info("CipherTest.test bizContent={}", bizContent);

        // step3. 对AES密钥进行非对称加密
        // 根据appId获取公钥publicKey
        Map<String, Object> paramMap = new HashMap<>(1);
        paramMap.put("appId", APPID);
        String post = HttpUtil.post("http://localhost:8082/keys/getPublicKey", paramMap);
        // log.info("CipherTest.test post={}", post);
        String publicKey = JSONUtil.parseObj(post).getStr("data");
        // log.info("CipherTest.test publicKey={}", publicKey);
        String domain = RSAUtils.encrypt(AES_KEY, publicKey);
        // log.info("CipherTest.test domain={}", domain);

        // step3. 构造密文入参
        /** 入参结构：
                *      bizContent: AES加密后的接口入参密文
                *      domain:     RSA加密后的AES密钥
                *      appId:      appId用于区分系统*/
        JSONObject jsonObject = JSONUtil.createObj();
        jsonObject.set("bizContent", bizContent)
                .set("domain", domain)
                .set("appId", APPID);
        log.info("CipherTest.test jsonObject={}", jsonObject.toString());

        // step4. 调用接口
        String response = HttpUtil.post("http://localhost:8082/security/test/hello", jsonObject.toString());
        log.info("CipherTest.test response={}", response);
        String resData = JSONUtil.parseObj(response).getStr("data");

        // step5. 对接口返回结果使用AES解密
        String data = AESUtil.decrypt(resData, AES_KEY);
        log.info("CipherTest.test data={}", data);
    }

    /**
     * 组装原始报文
     * @return 原始报文JSON字符串
     */
    private String getJsonStr() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("name", "Nemo");
        log.info("CipherTest.getJsonStr jsonObject={}", jsonObject.toString());
        return jsonObject.toString();
    }
}
