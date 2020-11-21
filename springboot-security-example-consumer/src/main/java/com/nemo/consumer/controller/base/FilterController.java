package com.nemo.consumer.controller.base;

import com.nemo.api.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author Nemo
 * @Description 处理filter转发过来的异常信息
 * @Date 2020/11/21 11:42
 */
@Slf4j
@RestController
@RequestMapping("/filter")
public class FilterController {

    @RequestMapping("/filterException")
    public void filterException(HttpServletRequest request) {
        String errorMessage = request.getAttribute("errorMessage").toString();
        log.info("FilterController filterException errorMessage={}", errorMessage);
        throw new BusinessException(errorMessage);
    }
}
