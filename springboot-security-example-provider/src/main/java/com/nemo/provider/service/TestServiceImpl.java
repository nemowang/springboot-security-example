package com.nemo.provider.service;

import com.nemo.api.service.TestService;
import org.apache.dubbo.config.annotation.Service;

@Service
public class TestServiceImpl implements TestService {

    @Override
    public String sayHello(String name) {
        return "你好，" + name;
    }
}
