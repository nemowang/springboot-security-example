package com.nemo.consumer.handler;

import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.nemo.api.enums.ResultEnum;
import com.nemo.api.exception.BusinessException;
import com.nemo.api.service.SecurityService;
import com.nemo.common.util.RSAUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.Charset;

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
     *
     * 入参结构：
     *      bizContent: AES加密后的接口入参密文
     *      domain:     RSA加密后的AES密钥
     *      appId:      appId用于区分系统
     *
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
            // 将body数据存储起来
            String bodyStr = this.getBodyString(request);
            log.info("RequestHandler 请求开始解析bodyStr:{}", bodyStr);
            // step1. 读取请求数据
            JSONObject jsonObject = JSONUtil.parseObj(bodyStr);

            // 加密开关关闭时，直接返回
            if ("false".equals(encryptSwitch)) {
                body = encData.getBytes(Charset.defaultCharset());
                return;
            }
            if (JSONUtil.isNull(jsonObject.get("bizContent"))) {
                log.info("RequestHandler 参数不能为空");
                throw new BusinessException(ResultEnum.PARAM_EMPTY);
            }

            encData = jsonObject.getStr("bizContent");
            log.info("RequestHandler 加密串encData:{}", encData);

            String domain = jsonObject.getStr("domain");
            log.info("RequestHandler domain:{}", domain);
            if (StringUtils.isEmpty(domain)) {
                log.info("RequestHandler 解密失败，domain不能为空。");
                throw new BusinessException(ResultEnum.DECRYPT_ERROR);
            }

            String appId = jsonObject.getStr("appId");
            log.info("RequestHandler appId:{}", appId);
            if (StringUtils.isEmpty(appId)) {
                log.info("RequestHandler 解密失败，appId不能为空");
                throw new BusinessException(ResultEnum.APPID_EMPTY);
            }

            // step2. 获取RSA私钥，解密AES密钥
            // 获取RSA私钥
            privateKey = securityService.getPrivateKey(appId);
            log.info("RequestHandler 获取私钥privateKey:{}", privateKey);
            // 解密AES密钥
            String decryptDomainKey = RSAUtils.decrypt(domain, privateKey);
            log.info("RequestHandler domainkey:{}, encData:{}", decryptDomainKey, encData);

            // step3. 使用AES密钥解密数据
            // TODO AES使用decryptDomainKey解密encData获取bizContent中的数据
            String decryptUnicode = StringUtils.EMPTY;
            log.info("RequestHandler 解析的得到的数据为:{}", decryptUnicode);
            String decrypt = UnicodeUtil.toString(decryptUnicode);
            log.info("RequestHandler decryptUnicode转String得到的数据为:{}", decrypt);

            // step4. 返回给body
            JSONObject result = new JSONObject();
            if (StringUtils.isNotBlank(decrypt)) {
                result = JSONUtil.parseObj(decrypt);
            }
            // 把domainkey放到参数中，返回数据加密时使用
            result.put("domainKey", decryptDomainKey);
            log.info("RequestHandler resultJson={}", JSONUtil.toJsonStr(result));
            body = JSONUtil.toJsonStr(result).getBytes();
        } catch (BusinessException e) {
            log.error("RequestHandler.RequestHandler decrypt error. privateKey:{}, encData:{}", privateKey, encData, e);
            // 继续抛出异常，让filter去处理
            throw new BusinessException(e.getMessage());
        } catch (Exception e) {
            log.error("RequestHandler error.", e);
            throw new BusinessException(ResultEnum.FAIL);
        }
    }

    /**
     * 获取请求body
     * @param request
     * @return bodyStr
     */
    private String getBodyString(HttpServletRequest request) {
        try {
            return this.inputStream2String(request.getInputStream());
        } catch (IOException e) {
            log.error("RequestHandler.getBodyString error.", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 将inputStream里的数据读取出来并转换成字符串
     * @param inputStream
     * @return String
     */
    private String inputStream2String(InputStream inputStream) {
        String content = null;
        try {
            content = IOUtils.toString(inputStream, "UTF-8");
        } catch (IOException e) {
            log.error("RequestHandler.inputStream2String error.", e);
        }
        return content;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {

        final ByteArrayInputStream inputStream = new ByteArrayInputStream(body);

        return new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return inputStream.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }
        };
    }
}
