package com.nemo.consumer.handler;

import com.nemo.api.enums.ResultEnum;
import com.nemo.api.exception.BusinessException;
import com.nemo.api.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * @Author Nemo
 * @Description
 * @Date 2020/11/20 9:59
 */
@Slf4j
public class RequestHandler extends HttpServletRequestWrapper {

    /**
     * 存储body数据的容器
     * request中的body只能读取一次，因此定义变量暂存
     */
    private byte[] body;

    /**
     * 处理入参
     * 解密
     * @param request           request
     * @param securityService   非对称加密公私钥管理服务
     * @param encryptSwitch     加解密开关
     */
    public RequestHandler(HttpServletRequest request, SecurityService securityService, String encryptSwitch) {
        super(request);
        String contentType = request.getMethod();
        if ("GET".equals(contentType)) {
            log.info("RequestHandler don't support GET method. RequestHandler不支持get请求");
            throw new BusinessException(ResultEnum.REQUEST_ERROR);
        }

        // 私钥，用来解密
        String privateKey = StringUtils.EMPTY;
        // 密文(RSA加密)
        String encData = StringUtils.EMPTY;

        try {

        } catch (BusinessException e) {
            log.error("RequestHandler.RequestHandler decrypt error. privateKey:{}, encData:{}", privateKey, encData, e);
            // 继续抛出异常，让filter去处理
            throw new BusinessException(e.getMessage());
        } catch (Exception e) {
            log.error("RequestHandler error.", e);
            throw new BusinessException(ResultEnum.FAIL);
        }
    }
}
