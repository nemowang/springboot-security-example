package com.nemo.consumer.controller.api;

import com.nemo.consumer.domain.vo.test.HelloReqVO;
import com.nemo.consumer.domain.vo.ResultVO;
import com.nemo.consumer.domain.vo.test.HelloRespVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Nemo
 * @Description
 * @Date 2020/11/21 11:45
 */
@Slf4j
@RestController
@RequestMapping("/security/test")
public class SecurityTestController {

    @PostMapping("hello")
    public ResultVO<HelloRespVO> hello(@RequestBody HelloReqVO reqVO) {
        log.info("SecurityTestController hello reqVO={}", reqVO);
        String content = "hello, " + reqVO.getName();
        HelloRespVO respVO = new HelloRespVO();
        respVO.setContent(content);
        respVO.setDomainKey(reqVO.getDomainKey());
        return ResultVO.success(respVO);
    }
}
