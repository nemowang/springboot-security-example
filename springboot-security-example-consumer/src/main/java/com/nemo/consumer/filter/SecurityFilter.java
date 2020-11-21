package com.nemo.consumer.filter;

import com.nemo.api.service.SecurityService;
import com.nemo.consumer.handler.RequestHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @Author Nemo
 * @Description 用来拦截需要参数解密的接口的过滤器
 * @Date 2020/11/21 11:33
 */
@Slf4j
@RefreshScope
@WebFilter(filterName = "securityFilter", urlPatterns = "/security/*")
public class SecurityFilter implements Filter {

    /** 加密开关 */
    @Value("${security.encryptSwitch}")
    private String encryptSwitch;

    @DubboReference
    private SecurityService securityService;

    @Override
    public void init(FilterConfig config) throws ServletException {
        log.info("SecurityFilter初始化...");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        log.info("SecurityFilter加密开关:{}", encryptSwitch);
        try {
            long startTime = System.currentTimeMillis();
            RequestHandler requestWrapper = new RequestHandler((HttpServletRequest) req, securityService, encryptSwitch);
            long endTime = System.currentTimeMillis();
            log.info("SecurityFilter解密消耗时间:{}", endTime - startTime);
            chain.doFilter(requestWrapper, resp);
        } catch (Exception e) {
            // 处理异常 因为filter中的异常无法抛出，因此将异常信息转发到专门处理filter异常的controller中进行接口返回
            log.info("SecurityFilter doFilter ERROR.", e);
            req.setAttribute("errorMessage", e.getMessage());
            req .getRequestDispatcher("/filter/filterException").forward(req, resp);
        }
    }

    @Override
    public void destroy() {
        log.info("SecurityFilter销毁...");
    }
}
