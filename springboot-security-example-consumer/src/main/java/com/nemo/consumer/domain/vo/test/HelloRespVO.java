package com.nemo.consumer.domain.vo.test;

import com.nemo.consumer.domain.vo.BaseRespVO;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author Nemo
 * @Description
 * @Date 2020/11/21 11:50
 */
@Data
public class HelloRespVO extends BaseRespVO implements Serializable {
    private static final long serialVersionUID = -5849015592350666109L;

    private String content;
}
