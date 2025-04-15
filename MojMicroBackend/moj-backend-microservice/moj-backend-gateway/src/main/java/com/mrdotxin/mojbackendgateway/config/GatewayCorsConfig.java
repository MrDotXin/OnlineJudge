package com.mrdotxin.mojbackendgateway.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class GatewayCorsConfig {
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);  // 允许携带cookie
        config.addAllowedOriginPattern("*");  // 允许所有来源（生产环境应替换为具体域名）
        config.addAllowedMethod("*");  // 允许所有HTTP方法
        config.addAllowedHeader("*");  // 允许所有请求头
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);  // 对所有路径生效
        
        return new CorsWebFilter(source);
    }
}