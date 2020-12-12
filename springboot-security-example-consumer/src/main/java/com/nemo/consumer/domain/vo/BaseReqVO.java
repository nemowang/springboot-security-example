package com.nemo.consumer.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author Nemo
 * @Description
 * @Date 2020/11/21 11:48
 */
@Data
public class BaseReqVO implements Serializable {
    private static final long serialVersionUID = 5908021765738630047L;

    @ApiModelProperty(value = "AES密钥")
    private String domainKey;
}
