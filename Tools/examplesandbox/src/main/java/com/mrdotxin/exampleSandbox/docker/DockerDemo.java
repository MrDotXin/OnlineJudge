package com.mrdotxin.exampleSandbox.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.mrdotxin.exampleSandbox.codeSandbox.model.ExecuteCodeResponse;

public class DockerDemo {

    public static void main(String[] args) {
        DockerClient dockerClient = initDockerClient();

        String image = "openjdk:8-alpine";
        
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(image);
        CreateContainerResponse containerResponse = containerCmd
                                        .withAttachStderr(true)
                                        .withAttachStdin(true)
                                        .withAttachStdout(true)
                                        .withTty(true)
                                        .exec();
        String containterId = containerResponse.getId();

        dockerClient.startContainerCmd(containterId).exec();

        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        

        System.out.println("下载成功 " + executeCodeResponse);
    }

    private static DockerClient initDockerClient() {
        // 创建默认配置，自动检测环境变量和系统属性
        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();

        // 使用Apache HttpClient5
        ApacheDockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .build(); 

        return DockerClientImpl.getInstance(config, httpClient);
    }
}
