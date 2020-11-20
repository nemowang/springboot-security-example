package com.nemo.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author Nemo
 * @Description
 * @Date 2020/11/20 16:26
 */
@Getter
@AllArgsConstructor
public enum ResultEnum {
    SUCCESS("000000", "成功"),
    FAIL("999999", "失败"),

    REQUEST_ERROR("RQE0001", "不支持GET请求"),
    PARAM_EMPTY("PME0001", "参数不能为空"),
    APPID_EMPTY("APE0001", "appId不能为空"),
    DECRYPT_ERROR("JME0001", "decrypt失败"),

    ;

    private String code;
    private String msg;
}
