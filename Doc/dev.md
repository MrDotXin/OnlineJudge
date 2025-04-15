# MOJ

[toc]

---

## 评测机

### Java原生实现

使用Runtime静态类配合ProcessBuilder可以实现

#### 内存限制

在程序中, 我们需要通过对内存使用方面进行限制, 防止恶意代码的大量占用服务器资源

##### 使用java编译原生-Xmx指令实现

#### 时间限制

我们通过限制程序的执行时间来防止程序大量长期占用服务器的资源, 同时可以以判断某些用户程序的不当效率.

##### 通过添加守护线程实现

#### 危险操作

我们通过限制用户代码来实现。

##### SecurityManager

利用安全工具, 编写用户类继承SecurityManager, 自定义安全规则

### docker实现

<img src="C:\Users\MrXin\AppData\Roaming\Typora\typora-user-images\image-20250319093741814.png" alt="image-20250319093741814" style="zoom:33%;" />

Docker运行在 linux Kernel 下

CGroups: 实现了资源隔离, 底层是Linux group命令, 能够控制资源的使用

Network网络: 容器的网络隔离

Namespaces: 把进程隔离在不同名称空间里面, 每个容器有自己的命名空间进程互不影响

Storage: 容器内的文件是互相隔离的

#### Docker 实现代码沙箱

Docker 负责运行java程序，同时得到结果

1.  把用户代码保存为文件
2.  编译代码, 得到class文件
3.  把编译好的文件上传到容器环境内
4.  在容器中执行代码，得到输出结果
5.  收集整理结果
6.  文件清理
7.  错误处理



拉取镜像

```java
docker pull openjdk:8-ipline
```



创建容器后, 我们设置好映射目录

```java
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(DOCKER_IMAGE_NAME);
        HostConfig hostConfig = new HostConfig();
        
        hostConfig.withMemory(1000 * 1000 * 1000l);
        hostConfig.withCpuCount(1l);
        hostConfig.setBinds(new Bind(userCodeFile.getParentFile().getAbsolutePath(), new Volume("/app")));
        CreateContainerResponse containerResponse = containerCmd
                                            .withHostConfig(hostConfig)
                                            .withAttachStderr(true)
                                            .withAttachStdin(true)
                                            .withAttachStdout(true)
                                            .withTty(true)
                                            .exec();
        String id = containerResponse.getId();
```

#### 安全限制

我们需要隔离掉网络环境, 设置最大内存

```java
        CreateContainerResponse containerResponse = containerCmd
                                            .withHostConfig(hostConfig)
                                            .withNetworkDisabled(true)
            				   .withReadonlyRootfs(true)
```

同时结合**Java安全管理器**和其它策略去使用限制用户不能向root根写文件

我们也可以使用linux sccomp 来限制



#### 实现内存限制检测的两种方法

-   [x] 每次判题都重新启动一个沙箱, 通过HostConif严格限制Memory, 结束后删除
    -   [ ] 可以通过池化技术 + 缓存技术来优化s
-   [ ] 初始化一个比较大的沙箱, 在代码运行过程中使用StatsCmd对沙箱执行状况进行检测 

### 模板方法模式优化沙箱

定义判题的几个步骤

1.  创建临时文件, 存储用户代码
2.  编译临时文件, 得到编译结果
    1.  编译失败则报错
3.  运行代码文件 可能会抛出的异常
    1.  运行超时
    2.  危险操作
    3.  内存超限
    4.  答案错误
    5.  运行错误
    6.  格式错误
4.  整理运行结果
5.  删除文件



### 优化判题信息描述

我们的判题信息应该是这样的:

每一道题都有输入、输出、运行状态, 状态信息, 信息类型 	

## 微服务改造

### 使用nacos 2.2.0作为服务发现组件

### 使用 Spring cloud alibaba

我们通过子父项目建立, 将模块分为

gateway: 聚合网关,为前端提供统一接口

user-service: 用户服务

question-service: 题目服务

judge-service: 判题服务

model: 组件, 提供数据类

common: 全局配置、工具、组件、配置, 引入后通过@ComponentScan来扫描此包



我们通过全局引入如下配置, 这样子服务将自动注册与服务发现

```xml
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-alibaba-sentinel-gateway</artifactId>
        </dependency>
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-loadbalancer</artifactId>
            <version>3.1.5</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>2021.0.5</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
```

通过配置yml, 使得子服务主动去寻找注册中心

```yml
  cloud: # nacos 微服务架构注册中心地址
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848

```



#### 聚合网关gateway

网关分

-   全局网关(接入层网关): 作用是负载均衡、请求日志、不和业务逻辑绑定
-   业务网关(微服务网关): 负责将请求转发到不同业务中

网关实现:

-   nginx(全局网关), Kong 网关(API 网关, [Kong](https://github.com/Kong/kong))
-   Spring cloud gateway(取代了Zuul)



我们在网关中配置路由, 以帮助网关可以访问这些配置

```yml
    gateway:
      routes:
        - id: moj-backend-user-service
          uri: lb://moj-backend-user-service
          predicates:
            - Path=/api/user/**
        - id: moj-backend-question-service
          uri: lb://moj-backend-question-service
          predicates:
            - Path=/api/question/**
        - id: moj-backend-judge-service
          uri: lb://moj-backend-judge-service
          predicates:
            - Path=/api/judge/**
```



### 基于knife4j的spring-cloud-gateway的聚合文档生成

我们在需要对外提供服务的子组件中引入

```xml
<dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>knife4j-openapi2-spring-boot-starter</artifactId>
    <version>4.3.0</version>
</dependency>
```

在网关项目中引入

```xml
<dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>knife4j-gateway-spring-boot-starter</artifactId>
    <version>4.3.0</version>
</dependency>
```

同时在网关项目中写下自动发现的配置:

```yml
knife4j:
  gateway:
    enabled: true
    strategy: discover
    discover:
      enabled: true
      version: swagger2
```

>   // todo
>
>   怎么合并这些文档，整合成一个？ 



### 设置gateway网关的跨域请求

```java
package com.mrdotxin.mojbackendgateway.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 全局跨域配置
 *
 * 
 * 
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 覆盖所有请求
        registry.addMapping("/**")
                // 允许发送 Cookie
                .allowCredentials(true)
                // 放行哪些域名（必须用 patterns，否则 * 会和 allowCredentials 冲突）
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("*");
    }
}

```

## 微服务架构下的权限校验

### 内部接口

我们可以使用 spring cloud gateway 的filter.GlobalFilter做到拦截内部接口的非法访问

>   `org.springframework.cloud.gateway.filter.GlobalFilter` 是 **Spring Cloud Gateway** 中的一个核心接口，用于定义全局过滤器（Global Filter），对所有经过网关的请求或响应进行统一处理。它不依赖于特定路由配置，适用于全局范围的逻辑（如鉴权、日志、流量监控等）。

```java
package com.mrdotxin.mojbackendgateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class GlobalRequestFilter implements GlobalFilter {

    public AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest exchangeRequest = exchange.getRequest();
        String path = exchangeRequest.getURI().getPath();

        if (antPathMatcher.match("/**/inner/**", path)) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.FORBIDDEN);
            DataBufferFactory dataBufferFactory = response.bufferFactory();
            DataBuffer wrap = dataBufferFactory.wrap("无权限".getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(wrap));
        }

        return chain.filter(exchange);
    }
}
```

示例:

<img src="C:\Users\MrXin\AppData\Roaming\Typora\typora-user-images\image-20250331162607611.png" alt="image-20250331162607611" style="zoom:25%;" />



## 基于RabbitMQ的判题服务解耦

我们通过引入rabbitmq 来解耦判题服务和题目提交服务

```

```

