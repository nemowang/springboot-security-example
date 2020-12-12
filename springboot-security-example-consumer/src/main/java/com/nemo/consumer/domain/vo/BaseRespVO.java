package com.nemo.consumer.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author Nemo
 * @Description
 * @Date 2020/11/21 11:49
 */
@Data
public class BaseRespVO implements Serializable {
    private static final long serialVersionUID = -6057897847528509223L;

    @ApiModelProperty(value = "AES密钥")
    private String domainKey;
}
