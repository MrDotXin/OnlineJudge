package com.mrdotxin.mojbackendmicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class MojBackendMicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MojBackendMicroserviceApplication.class, args);
    }

}
