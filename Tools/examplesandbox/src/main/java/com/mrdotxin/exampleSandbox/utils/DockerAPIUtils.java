package com.mrdotxin.exampleSandbox.utils;

import java.util.concurrent.TimeUnit;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse.ContainerState;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Volume;
import com.mrdotxin.exampleSandbox.codeSandbox.model.ExecuteCommandInfo;

import cn.hutool.core.date.StopWatch;

public class DockerAPIUtils {
    
    public static String executeCommand(DockerClient dockerClient, String id, String [] args) {
        ExecCreateCmdResponse response = dockerClient.execCreateCmd(id)
                                    .withCmd(args)
                                    .withAttachStderr(true)
                                    .withAttachStdin(true)
                                    .withAttachStdout(true)
                                    .withTty(true)
                                    .exec();
        String excId = response.getId();

        final String [] output = {""};
        try {   
            dockerClient.execStartCmd(excId).exec(new ResultCallback.Adapter<Frame>() {
                @Override
                public void onNext(Frame frame) {
                    output[0] = new String(frame.getPayload());
                }
            }).awaitCompletion();


        } catch (Exception e) {
            e.printStackTrace();
        }

        return output[0];
    } 

    public static ExecuteCommandInfo executeCommandTimeout(DockerClient dockerClient, String id, Long timeout, String [] args) {
        ExecuteCommandInfo executeCommandInfo = new ExecuteCommandInfo();
    
        ExecCreateCmdResponse response = dockerClient.execCreateCmd(id)
                                    .withCmd(args)
                                    .withAttachStderr(true)
                                    .withAttachStdin(true)
                                    .withAttachStdout(true)
                                    .withTty(true)
                                    .exec();
        String excId = response.getId();
        StopWatch stopWatch = new StopWatch();
        final StringBuilder [] output = { new StringBuilder() };
        try {   
            stopWatch.start();
            dockerClient.execStartCmd(excId).exec(new ResultCallback.Adapter<Frame>() {
                @Override
                public void onNext(Frame frame) {
                    output[0].append(new String(frame.getPayload()));
                    output[0].append("\n");
                }
            }).awaitCompletion(timeout, TimeUnit.MILLISECONDS);
            stopWatch.stop();
            Long time = stopWatch.getLastTaskTimeMillis();
            Long exitCode = DockerAPIUtils.getLastError(dockerClient, id);

            executeCommandInfo.setTimeout(time);
            executeCommandInfo.setOutput(output[0].toString());
            executeCommandInfo.setCode(exitCode);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return executeCommandInfo;
    } 


    /***
     * 
     * @return 返回跑用户代码的容器
     */
    public static String CreateConstraintContainer(
            DockerClient dockerClient, 
            String image_name,
            Long memoryLimits,
            Long swapMemoryLimits,
            Long cpuCount,
            String bindFolder,
            String volume
        ) {
        
        CreateContainerCmd cmd = dockerClient.createContainerCmd(image_name);

        HostConfig hostConfig = new HostConfig();

        hostConfig.withMemory(memoryLimits);
        hostConfig.withMemorySwap(swapMemoryLimits);
        hostConfig.withCpuCount(cpuCount);
        hostConfig.withReadonlyRootfs(true);
        hostConfig.setBinds(new Bind(bindFolder, new Volume(volume)));

        CreateContainerResponse response = cmd
            .withHostConfig(hostConfig)
            .withNetworkDisabled(true)
            .withAttachStderr(true)
            .withAttachStdin(true)
            .withAttachStdout(true)
            .withTty(true)
            .exec();
        
        return response.getId();
    }

    public static void RunContainer(DockerClient dockerClient, String id) {
        dockerClient.startContainerCmd(id).exec();
    }

    public static void deleteContainer(DockerClient dockerClient, String id) {
        dockerClient.removeContainerCmd(id).withForce(true).exec();
    }

    public static Long getLastError(DockerClient dockerClient, String id) {
        InspectContainerResponse response = dockerClient.inspectContainerCmd(id).exec();

        return response.getState().getExitCodeLong();
    }

    public static ContainerState getContainerStatus(DockerClient dockerClient, String id) {
        InspectContainerResponse response = dockerClient.inspectContainerCmd(id).exec();
        return response.getState();
    }

}
