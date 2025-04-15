package com.mrdotxin.mojbackenduserservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.mrdotxin.mojbackenduserservice.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.mrdotxin")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.mrdotxin.mojbackendserviceclient.service"})
public class MojBackendUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MojBackendUserServiceApplication.class, args);
    }

}

