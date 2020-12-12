package com.nemo.consumer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @Author Nemo
 * @Description
 * @Date 2020/12/12 12:59
 */
@Configuration
public class Swagger3Config {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(this.apiInfo() )
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.nemo.consumer.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("SpringBoot-Security-Example Web文档")
                .description("SpringBoot整合接口数据加解密Web Api文档")
                .contact(new Contact("NemoWang", "https://github.com/nemowang", "nemowang77@163.com"))
                .version("1.0.0")
                .build();
    }
}
