package com.mrdotxin.mojbackendjudgeservice;

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
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableScheduling
@EnableKnife4j
@ComponentScan("com.mrdotxin")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.mrdotxin.mojbackendserviceclient.service"})
public class MojBackendJudgeServiceApplication {

    public static void main(String[] args) {
        // 初始化 rabbitmq
        InitRabbitMQ.doInit();

        SpringApplication.run(MojBackendJudgeServiceApplication.class, args);
    }

}
