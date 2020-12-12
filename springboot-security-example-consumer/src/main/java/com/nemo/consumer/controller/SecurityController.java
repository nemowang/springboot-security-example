package com.nemo.consumer.controller;

import com.nemo.api.service.SecurityService;
import com.nemo.consumer.domain.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Nemo
 * @Description
 * @Date 2020/12/12 12:39
 */
@Slf4j
@RestController
@RequestMapping("/securityKeys")
public class SecurityController {

    @DubboReference
    private SecurityService securityService;

    @PostMapping("getPublicKey")
    public ResultVO getPublicKey(@RequestParam String appId) {
        log.info("Enter SecurityController.getPublicKey appId={}", appId);

        try {
            String publicKey = securityService.getPublicKey(appId);
            return ResultVO.success(publicKey);
        } catch (Exception e) {
            log.error("SecurityController.getPublicKey error. appId={}", appId);
            return ResultVO.error(e.getLocalizedMessage());
        }
    }
}
