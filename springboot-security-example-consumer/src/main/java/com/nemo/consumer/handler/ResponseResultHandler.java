package com.nemo.consumer.handler;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.nemo.common.util.AESUtil;
import com.nemo.consumer.domain.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @Author Nemo
 * @Description 处理返回数据
 * @Date 2020/11/21 11:53
 */
@Slf4j
@ControllerAdvice(basePackages = "com.nemo.consumer.controller.api")
public class ResponseResultHandler implements ResponseBodyAdvice {

    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        log.info("请求返回数据类型class={}", body.getClass().getName());
        ResultVO resultVO = null;
        try {
            if (body instanceof ResultVO) {
                long startTime = System.currentTimeMillis();
                resultVO = (ResultVO) body;
                log.info("请求返回数据:{}", JSONUtil.toJsonStr(resultVO));
                // 加密
                if (null == resultVO.getData()) {
                    return body;
                }
                String domainKey = JSONUtil.parseObj(resultVO.getData()).getStr("domainKey");
                log.info("domainKey:{}", domainKey);
                Object data = JSONUtil.parseObj(resultVO.getData());
                JSONObject dataObj = JSONUtil.parseObj(data);
                dataObj.remove("domainKey");

                // TODO AES加密
                String encData = AESUtil.encrypt(JSONUtil.toJsonStr(dataObj), domainKey);
                log.info("encData:{}", encData);
                resultVO.setData(encData);
                long endTime = System.currentTimeMillis();
                log.info("加密消耗时间:{}", endTime - startTime);
                return resultVO;
            }
        } catch (Exception e) {
            log.error("ResponseResultHandler.beforeBodyWrite error.", e);
        }
        return body;
    }

    /**
     * 数据脱敏
     *
     * @param cardno
     * @return
     */
    private static String dealCardNo(String cardno) {
        return cardno.replaceAll("(?<=\\w{4})\\w(?=\\w{4})", "*");
    }

    /**
     * 数据脱敏
     *
     * @param certid
     * @return
     */
    private static String dealCertId(String certid) {
        return certid.replaceAll("(?<=\\w{3})\\w(?=\\w{4})", "*");
    }

    /**
     * 数据脱敏
     *
     * @param telno
     * @return
     */
    private static String dealTelNo(String telno) {
        return telno.replaceAll("(?<=\\w{3})\\w(?=\\w{4})", "*");
    }

}
