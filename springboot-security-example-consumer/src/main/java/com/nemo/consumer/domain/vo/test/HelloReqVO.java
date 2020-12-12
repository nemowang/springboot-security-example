package com.nemo.consumer.domain.vo.test;

import com.nemo.consumer.domain.vo.BaseReqVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * @Author Nemo
 * @Description
 * @Date 2020/11/21 11:47
 */
@Data
public class HelloReqVO extends BaseReqVO implements Serializable {
    private static final long serialVersionUID = -2000487917118410916L;

    @NotEmpty(message = "姓名不能为空")
    @ApiModelProperty(value = "姓名")
    private String name;
}
