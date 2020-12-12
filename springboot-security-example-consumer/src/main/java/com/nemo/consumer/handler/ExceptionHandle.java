package com.nemo.consumer.handler;

import cn.hutool.json.JSONUtil;
import com.nemo.api.enums.ResultEnum;
import com.nemo.api.exception.BusinessException;
import com.nemo.consumer.domain.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Author Nemo
 * @Description 拦截异常，统一处理
 * @Date 2020/11/20 16:29
 */
@Slf4j
@RestControllerAdvice
public class ExceptionHandle {

    /**
     * 处理自定义异常
     * @param e 异常
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public ResultVO handleBusinessException(BusinessException e) {
        log.error("BusinessExcepion ", e);
        return ResultVO.error(e.getMessage());
    }

    /**
     * 处理@Valid校验错误
     * @param e
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultVO handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getBindingResult().getFieldError().getDefaultMessage());
        return ResultVO.error(ResultEnum.FAIL.getCode(), e.getBindingResult().getFieldError().getDefaultMessage());
    }

    /**
     * 处理未知异常
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ResultVO handleException(Exception e) {
        log.error("未知异常{}", e);
        return ResultVO.error("未知异常");
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public void handleIllegalArgumentException(HttpServletResponse response, IllegalArgumentException e) {
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("UTF-8");
        resWrite(response, ResultVO.error(e.getMessage()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public void handleMissingServletRequestParameterException(HttpServletResponse response, MissingServletRequestParameterException e) {
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("UTF-8");
        resWrite(response, ResultVO.error(e.getMessage()));
    }

    private void resWrite(HttpServletResponse response, ResultVO resultVO) {
        try {
            PrintWriter writer = response.getWriter();
            writer.write(JSONUtil.toJsonStr(resultVO));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            log.error("ExceptionHandle resWrite error.", e);
        }
    }
}
