package com.nemo.api.exception;

import com.nemo.api.enums.ResultEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @Author Nemo
 * @Description 自定义异常处理
 * @Date 2020/11/20 16:21
 */
@Getter
@NoArgsConstructor
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 58016848053430415L;
    private String code;
    private String msg;

    public BusinessException(String message) {
        super(message);
        this.msg = message;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(String code, String messaege) {
        super(String.format("[%s]:%s", code, messaege));
        this.code = code;
        this.msg = messaege;
    }

    public BusinessException(ResultEnum resultEnum) {
        super(String.format("[%s]:%s", resultEnum.getCode(), resultEnum.getMsg()));
        this.code = resultEnum.getCode();
        this.msg = resultEnum.getMsg();
    }

    public String getErrCode() {
        return this.getCode();
    }

    public String getMsg() {
        return this.getMsg();
    }
}
