package com.nemo.provider.config;

import cn.hutool.core.io.resource.ClassPathResource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Slf4j
@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redisson() {
        Config config = null;
        try {
            ClassPathResource classPathResource = new ClassPathResource("redissonConfig.yaml");
            config = Config.fromYAML(classPathResource.getFile());
        } catch (IOException e) {
            log.error("RedissonConfig redisson error.", e);
            return null;
        }
        RedissonClient redissonClient = Redisson.create(config);
        log.info("RedissonConfig redisson 创建RedissonClient成功");
        return redissonClient;
    }
}
