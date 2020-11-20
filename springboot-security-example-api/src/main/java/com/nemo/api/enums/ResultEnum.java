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

    REQUEST_ERROR("RE0001", "不支持GET请求"),

    ;

    private String code;
    private String msg;
}
