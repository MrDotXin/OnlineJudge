package com.mrdotxin.exampleSandbox.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.mrdotxin.exampleSandbox.codeSandbox.Language.Java.Java18Sandbox;
import com.mrdotxin.exampleSandbox.codeSandbox.Language.Java.Impl.JavaDockerSandbox;

@Configuration
public class SandboxConfig {

    @Bean
    public Java18Sandbox getJava18Sandbox() {
        return new JavaDockerSandbox();
    }
}
