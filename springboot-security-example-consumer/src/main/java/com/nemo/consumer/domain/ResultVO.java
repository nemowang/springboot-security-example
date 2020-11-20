package com.nemo.consumer.domain;

import com.nemo.api.enums.ResultEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Nemo
 * @Description
 * @Date 2020/11/20 16:30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultVO<T> implements Serializable {
    private static final long serialVersionUID = -4555397099128728219L;

    /**
     * 状态码
     */
    private String code;

    /**
     * 描述
     */
    private String msg;

    /**
     * 数据
     */
    private T data;

    public static ResultVO success() {
        return new ResultVO(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), null);
    }

    public static ResultVO success(Object data) {
        return new ResultVO(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), data);
    }

    public static ResultVO error(String msg) {
        return new ResultVO(ResultEnum.FAIL.getCode(), msg, null);
    }

    public static ResultVO error(String code, String msg) {
        return new ResultVO(code, msg, null);
    }

    public static ResultVO error(ResultEnum resultEnum) {
        return new ResultVO(resultEnum.getCode(), resultEnum.getMsg(), null);
    }
}
