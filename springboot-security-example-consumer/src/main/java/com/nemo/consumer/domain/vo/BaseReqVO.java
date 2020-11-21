package com.nemo.consumer.domain.vo;

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

    private String domainKey;
}
