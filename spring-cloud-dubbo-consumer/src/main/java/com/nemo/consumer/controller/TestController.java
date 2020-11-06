package com.nemo.consumer.controller;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.config.ConfigService;
import com.nemo.api.service.TestService;
import lombok.SneakyThrows;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@RestController
@RequestMapping("/test")
public class TestController {

    @Reference
    private TestService testService;

    @GetMapping("sayHello")
    public String sayHello(String name) {
        return testService.sayHello(name);
    }


    public static final String SERVER_ADDR = "127.0.0.1:8848";
    public static final String TEST_NAMESPACE = "5f86c5a7-3bf1-4108-966f-c25ebebf7803";

    /**
     * 使用NacosFactory 从nacos配置中心获取城市编码列表
     * @return
     */
    @SneakyThrows
    @GetMapping("getCityCodeListByNacosFactory")
    public JSONArray getCityCodeListByNacosFactory() {
        Properties properties = new Properties();
        // nacos服务器地址，127.0.0.1:8848
        properties.put(PropertyKeyConst.SERVER_ADDR, SERVER_ADDR);
        // 配置中心的命名空间id
        properties.put(PropertyKeyConst.NAMESPACE, TEST_NAMESPACE);
        ConfigService configService = NacosFactory.createConfigService(properties);
        // 根据dataId、group定位到具体配置文件，获取其内容. 方法中的三个参数分别是: dataId, group, 超时时间
        String content = configService.getConfig("CityCodeList", "DEFAULT_GROUP", 3000L);
        // 因为我的配置内容是JSON数组字符串，这里将字符串转为JSON数组
        return JSONUtil.parseArray(content);
    }

    /**
     * 使用nacos的OPEN API 读取配置中心的配置
     * @return
     */
    @GetMapping("getCityCodeListByNacosOpenAPI")
    public JSONArray getCityCodeListByNacosOpenAPI() {
        Map<String, Object> paramMap = new HashMap<>(3);
        // 租户信息，对应nacos命名空间id字段
        paramMap.put("tenant", TEST_NAMESPACE);
        // dataId
        paramMap.put(Constants.DATAID, "CityCodeList");
        // group
        paramMap.put(Constants.GROUP, "DEFAULT_GROUP");
        // url: http://127.0.0.1:8848/nacos/v1/cs/configs
        String content = HttpUtil.get(SERVER_ADDR + "/nacos" + Constants.CONFIG_CONTROLLER_PATH, paramMap);
        // 因为我的配置内容是JSON数组字符串，这里将字符串转为JSON数组
        return JSONUtil.parseArray(content);
    }
}
