package com.nemo.api.service;

/**
 * @Author Nemo
 * @Description 加解密密钥相关服务
 * @Date 2020/11/19 16:15
 */
public interface SecurityService {

    /**
     * 获取公钥
     * @param appId 调用者所在的appId，用于区分不同系统
     * @return 公钥
     */
    String getPublicKey(String appId);

    /**
     * 获取私钥
     * @param appId 调用者所在的appId，用于区分不同系统
     * @return 私钥
     */
    String getPrivateKey(String appId);
}
