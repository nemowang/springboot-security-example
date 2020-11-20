package com.nemo.provider.service;

import cn.hutool.json.JSONUtil;
import com.nemo.api.service.SecurityService;
import com.nemo.provider.constant.RedisKeyConstant;
import com.nemo.provider.domain.dto.RsaKeyDTO;
import com.nemo.common.util.RSAUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author Nemo
 * @Description
 * @Date 2020/11/19 16:18
 */
@Slf4j
@DubboService
public class SecurityServiceImpl implements SecurityService {

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 获取公钥
     * @param appId 调用者所在的appId，用于区分不同系统
     * @return 公钥
     */
    @Override
    public String getPublicKey(String appId) {
        String publicKey = StringUtils.EMPTY;
        try {
            // 从redis查找publicKey
            String redisKey = MessageFormat.format(RedisKeyConstant.OPEN_PUBLIC_KEY_REDIS_KEY, appId);
            RBucket<Object> bucket = redissonClient.getBucket(redisKey);
            if (null == bucket.get() || StringUtils.isEmpty(bucket.get().toString())) {
                // 如果redis中不存在，则生成密钥对，并保存到redis中
                Map<Integer, String> map = RSAUtils.genKeyPair(1024);
                RsaKeyDTO rsaKeyDTO = new RsaKeyDTO();
                rsaKeyDTO.setPublicKey(map.get(0));
                rsaKeyDTO.setPrivateKey(map.get(1));
                bucket.set(JSONUtil.toJsonStr(rsaKeyDTO), 180, TimeUnit.DAYS);
            }
            String keyJson = bucket.get().toString();
            publicKey = JSONUtil.toBean(keyJson, RsaKeyDTO.class).getPublicKey();
        } catch (Exception e) {
            log.error("SecurityServiceImpl getPublicKey error, appId={}", appId, e);
        }
        return publicKey;
    }

    /**
     * 获取私钥
     * @param appId 调用者所在的appId，用于区分不同系统
     * @return 私钥
     */
    @Override
    public String getPrivateKey(String appId) {
        String privateKey = StringUtils.EMPTY;
        try {
            // 从redis查找publicKey
            String redisKey = MessageFormat.format(RedisKeyConstant.OPEN_PUBLIC_KEY_REDIS_KEY, appId);
            RBucket<Object> bucket = redissonClient.getBucket(redisKey);
            if (null == bucket.get() || StringUtils.isEmpty(bucket.get().toString())) {
                // 如果redis中不存在，则生成密钥对，并保存到redis中
                Map<Integer, String> map = RSAUtils.genKeyPair(1024);
                RsaKeyDTO rsaKeyDTO = new RsaKeyDTO();
                rsaKeyDTO.setPublicKey(map.get(0));
                rsaKeyDTO.setPrivateKey(map.get(1));
                bucket.set(JSONUtil.toJsonStr(rsaKeyDTO), 180, TimeUnit.DAYS);
            }
            String keyJson = bucket.get().toString();
            privateKey = JSONUtil.toBean(keyJson, RsaKeyDTO.class).getPrivateKey();
        } catch (Exception e) {
            log.error("SecurityServiceImpl getPrivateKey, appId={}", appId, e);
        }
        return privateKey;
    }
}
