package com.nemo.provider.service;

import com.nemo.api.service.SecurityService;
import com.nemo.provider.ProviderApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author Nemo
 * @Description SecurityServiceImpl单元测试类
 * @Date 2020/11/19 17:19
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProviderApplication.class)
public class SecurityServiceImplTest {

    @Autowired
    private SecurityService securityService;

    @Test
    public void getPublicKey() {
        String publicKey = securityService.getPublicKey("200001");
        log.info("securityService getPublicKey publicKey={}", publicKey);
    }
}
