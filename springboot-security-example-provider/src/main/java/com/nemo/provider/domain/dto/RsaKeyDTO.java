package com.nemo.provider.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author Nemo
 * @Description rsa密钥对dto
 * @Date 2020/11/19 16:58
 */
@Data
public class RsaKeyDTO implements Serializable {
    private static final long serialVersionUID = 7231468481245164316L;

    private String privateKey;

    private String publicKey;
}
