package com.nemo.consumer.domain.vo.test;

import com.nemo.consumer.domain.vo.BaseReqVO;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author Nemo
 * @Description
 * @Date 2020/11/21 11:47
 */
@Data
public class HelloReqVO extends BaseReqVO implements Serializable {
    private static final long serialVersionUID = -2000487917118410916L;

    private String name;
}
