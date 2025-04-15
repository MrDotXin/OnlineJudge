package com.mrdotxin.mojbackendjudgeservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class Swagger2Config {

    @Bean
    public Docket defaultApi2() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("Moj接口文档")
                        .description("moj-backend")
                        .version("β1.0")
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.mrdotxin.mojbackendjudgeservice.controller"))
                .paths(PathSelectors.any())
                .build()
                // 关键：将 Long/long 类型映射为 String
                .directModelSubstitute(Long.class, String.class)
                .directModelSubstitute(long.class, String.class);
    }
}
