package com.mrdotxin.mojbackendquestionservice;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.mrdotxin.mojbackendquestionservice.mapper")
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableScheduling
@ComponentScan("com.mrdotxin")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.mrdotxin.mojbackendserviceclient.service"})
public class MojBackendQuestionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MojBackendQuestionServiceApplication.class, args);
    }

}
