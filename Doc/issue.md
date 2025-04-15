# Issue

[toc]

## 关于Long类型转JS Number类型出现精度丢失问题

我们强制在序列化时转换Long 到 string 类型 如果配置了knife4j, 可以在Docket配置下加上

```java
@Configuration
@EnableSwagger2
@Profile({"dev", "test"})
public class Knife4jConfig {

    @Bean
    public Docket defaultApi2() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("Moj接口文档")
                        .description("moj-backend")
                        .version("β1.0")
                        .build())
                .select()
                // 指定 Controller 扫描包路径
                .apis(RequestHandlerSelectors.basePackage("com.MrDotXin.moj.controller"))
                .paths(PathSelectors.any())
                .build()

                // JS Number 存在精度丢失问题
                .directModelSubstitute(Long.class, String.class)
                .directModelSubstitute(long.class, String.class);

    }
}
```

然后全局配置Jackson, 使得spring boot 在序列化数据的时候 自动将Long 序列化为string

```java
@Configuration
public class JacksonConfig {
    
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            builder.serializers(new ToStringSerializer(Long.class));
            builder.serializers(new ToStringSerializer(long.class));
        };
    }
}
```

## 为什么数据库记录的日期全部只能精确到天份?

你看看你model里面的Date类型是不是写错了, 有两种类型

```java
java.sql.Date
```

和

```java
java.util.Date
```

下面这个才是正确的, 上面的就会导致这个问题, 这是因为`java.sql.Date`只有年月日三个字段...



## Spring mvc 与 spring cloud gateway 不兼容

这是因为gateway 和 mvc的不兼容, 你可以使用`<exclusion>`在`<dependencies>`中去掉web

也可以使用spring.main.web-application-type: reactive 来解决

Spring MVC 和 Spring Cloud Gateway 的不兼容性主要源于它们**底层架构**的根本差异，涉及不同的编程模型、请求处理机制和依赖体系。以下是具体原因分析：

### **技术栈的架构差异**

| **维度**     | **Spring MVC**                      | **Spring Cloud Gateway**                       |
| ------------ | ----------------------------------- | ---------------------------------------------- |
| **编程模型** | 基于 **Servlet API**（同步阻塞式）  | 基于 **Reactive Stack**（异步非阻塞，WebFlux） |
| **核心依赖** | `spring-boot-starter-web`           | `spring-boot-starter-webflux`                  |
| **请求处理** | 通过 `DispatcherServlet` 和过滤器链 | 通过 `WebHandler` 和反应式过滤器链             |
| **线程模型** | 每个请求占用一个线程（线程池限制）  | 事件循环（少量线程处理高并发）                 |

**冲突根源**：
Spring MVC 的 `DispatcherServlet` 和 Gateway 的 `WebHandler` 是两套完全独立的处理机制，无法混合使用。

### **2. 关键组件的不兼容**

#### **(1) 配置类不互通**

-   **Spring MVC** 的配置（如 `@EnableWebMvc`、`WebMvcConfigurer`）依赖 Servlet 容器，对 Gateway 无效。
-   **Gateway** 的配置（如 `RouteLocator`、`GlobalFilter`）仅作用于 WebFlux 环境。

#### **(2) 注解和拦截器失效**

-   `@RestController`、`@RequestMapping` 在 Gateway 中无效（除非明确用于 Gateway 的 Controller 端点）。
-   MVC 的拦截器（`HandlerInterceptor`）无法拦截 Gateway 转发的请求。

#### **(3) 依赖冲突**

若同时引入 `spring-boot-starter-web` 和 `spring-boot-starter-webflux`，会导致：

-   应用默认以 **Servlet 模式** 启动（因为 `spring-web` 优先级更高）。
-   Gateway 的 WebFlux 路由功能被破坏。

------

### **3. 典型问题场景**

#### **场景 1：CORS 配置无效**

-   **错误做法**：在 Gateway 项目中添加 `WebMvcConfigurer` 的 CORS 配置。
-   **原因**：Gateway 的请求根本不经过 `DispatcherServlet`，配置被忽略。
-   **解决方案**：必须使用 `CorsWebFilter`（WebFlux 专属）。

#### **场景 2：过滤器不生效**

-   **错误做法**：在 Gateway 中实现 `javax.servlet.Filter`。
-   **原因**：Servlet 过滤器无法介入 WebFlux 的请求处理链。
-   **解决方案**：改用 `GlobalFilter`（Reactive 接口）。

Spring MVC 和 Spring Cloud Gateway 的不兼容性是由 **反应式 vs 阻塞式架构** 的底层差异决定的。要避免问题：

1.  **技术栈纯净**：Gateway 项目仅依赖 WebFlux。
2.  **配置隔离**：MVC 配置需明确限定作用域。
3.  **架构设计**：网关职责单一化，避免混用请求处理模型。

## 网关模块出现Failed to configure a DataSource

这是因为父目录引入了mybatisplus, 它会产生给自动给链接数据库，但网管模块不需要数据库连接

可以在`@EnableAutoConfiguration`(也就是包含它的`@SpringBootApplication`注解)中设置

`exclude= {DataSourceAutoConfiguration.class}`来防止它被扫描到并且加载数据库配置。

## 明明怎么排查都查不出错误, 但就是爆出莫名其妙的，怎么改都没有用的错误

试试`mvn clean` 把构建信息的缓存清除掉

## 开启了Redis 作为分布式session的缓存数据库, 但session key就是不一样

有可能你前端发的cookie就是不一样的, 你需要配置后端

`server.servlet.session.path` , 将它们统一以确保不同请求携带的cookie相同, 这样request就会携带

相同的信息。

第二, 你可能需要显示设置它们的`spring.session.redis.namespace`一致

>    JSESSIONID与SESSIONID
>
>   >   -   **`JSESSIONID`**
>   >       由 Tomcat 生成，由于你配置了 `path=/api`，所以浏览器能正确携带到不同服务。
>   >   -   **`SESSION`**
>   >       由 `spring-session-data-redis` 生成，默认情况下：
>   >       -   每个服务可能使用 **不同的 Session 存储命名空间**（即使 Redis 是同一个）。
>   >       -   如果未显式统一配置 `spring.session.redis.namespace`，会导致 Session 隔离。
>   >
>   >   ------

## swagger2聚合文档下多api文档合并问题

怎么合并这些文档，整合成一个？



你可以手动写工具，或者就分模块每个服务单独生成一次



## 既然**Servlet API**与**Reactive Stack**不兼容，那么gateway要怎么配置跨域请求 ？

你可以使用CorsWebFilter

```java
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
```

