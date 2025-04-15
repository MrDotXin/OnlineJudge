package com.mrdotxin.exampleSandbox.codeSandbox;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.util.StopWatch;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.command.StatsCmd;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Statistics;
import com.github.dockerjava.api.model.StreamType;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.mrdotxin.exampleSandbox.codeSandbox.model.ExecuteCodeRequest;
import com.mrdotxin.exampleSandbox.codeSandbox.model.ExecuteCodeResponse;
import com.mrdotxin.exampleSandbox.codeSandbox.model.ExecuteMessage;
import com.mrdotxin.exampleSandbox.codeSandbox.model.JudgeInfo;
import com.mrdotxin.exampleSandbox.utils.DockerAPIUtils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;

public class DockerCodeSandboxImpl implements CodeSandbox {

    private final String GLOBAL_TMPER = "tmpcode";

    private final String GLOBAL_JAVA_CLASS_NAME = "Main.java";

    private final long TIME_OUT = 1000l; 

    private final String DOCKER_IMAGE_NAME = "openjdk:8-alpine";

    public static void main(String argc[]) {
        
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setInputList(Arrays.asList("1 2", "1 3"));

        String code = ResourceUtil.readStr("testCode\\TimeError\\Main.java", StandardCharsets.UTF_8);
        System.out.println(code);

        executeCodeRequest.setCode(code);
        executeCodeRequest.setLanguage("java");

        CodeSandbox codeSandbox = new DockerCodeSandboxImpl();
    
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);

        System.out.println(executeCodeResponse);
    }
    
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inpuList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        
        File userCodeFile = createResultCodeTmpFile(code);
        
        // 创建容器
        DockerClient dockerClient = initDockerClient();
        
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(DOCKER_IMAGE_NAME);
        HostConfig hostConfig = new HostConfig();
        
        hostConfig.withMemory(1000 * 1000 * 1000l);
        hostConfig.withMemorySwap(0l);
        hostConfig.withCpuCount(1l);
        hostConfig.withReadonlyRootfs(true);
        hostConfig.setBinds(new Bind(userCodeFile.getParentFile().getAbsolutePath(), new Volume("/app")));
        CreateContainerResponse containerResponse = containerCmd
                                            .withHostConfig(hostConfig)
                                            .withNetworkDisabled(true)
                                            .withAttachStderr(true)
                                            .withAttachStdin(true)
                                            .withAttachStdout(true)
                                            .withTty(true)
                                            .exec();
        String id = containerResponse.getId();
        System.out.println(id);

        dockerClient.startContainerCmd(id).exec();

        String output = DockerAPIUtils.executeCommand(dockerClient, id, new String[] {"javac", "-encoding", "utf-8", "./app/Main.java"});
        
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setJudgeInfo(new JudgeInfo());    

        List<ExecuteMessage> executeMessages = new LinkedList<ExecuteMessage>();
        if (!output.isEmpty()) {
            System.out.println("编译错误! " + output);
            executeCodeResponse.setStatus(2);
            executeCodeResponse.getJudgeInfo().setMessage("编译错误! " + output);
        } else {
            StopWatch stopWatch = new StopWatch();

            final Long [] maxMemory = {0l};
            final boolean [] timeout = {true};
            ResultCallback<Statistics> resultCallback = new ResultCallback<Statistics>() {
                @Override
                public void onNext(Statistics statistics) {
                    System.out.println("内存占用 " + statistics.getMemoryStats().getUsage());
                    maxMemory[0] = Math.max(maxMemory[0], statistics.getMemoryStats().getUsage());
                }   
                @Override public void onComplete() { timeout[0] = false; }
                @Override public void close() throws IOException {}
                @Override public void onError(Throwable throwable) {}
                @Override public void onStart(Closeable closeable) {}
            };

            StatsCmd statsCmd = dockerClient.statsCmd(id);
            statsCmd.exec(resultCallback);
            for (String input : inpuList) {
                Long time = 0l;
                maxMemory[0] = 0l;
                timeout[0] = true; 

                ExecCreateCmdResponse response = dockerClient.execCreateCmd(id)
                                                    .withCmd(BuildJavaExecuteCommand(input))
                                                    .withAttachStderr(true)
                                                    .withAttachStdin(true)
                                                    .withAttachStdout(true)
                                                    .withTty(true)
                                                    .exec();
                String excId = response.getId();

                ExecuteMessage executeMessage = new ExecuteMessage();
                try {
                    stopWatch.start();
                    dockerClient.execStartCmd(excId).exec(new ResultCallback.Adapter<Frame>(){
                        @Override
                        public void onNext(Frame frame) {
                            StreamType streamType = frame.getStreamType();
                            if (StreamType.STDERR.equals(streamType)) {
                                executeMessage.setErrorMessage(new String(frame.getPayload()));
                                System.out.println("输出错误结果  " + new String(frame.getPayload()));
                            } else {
                                executeMessage.setMessage(new String(frame.getPayload()));
                                System.out.println("输出正常结果  " + new String(frame.getPayload()));
                            }

                            super.onNext(frame);    
                        }
                    }).awaitCompletion(TIME_OUT, TimeUnit.MILLISECONDS);
                    stopWatch.stop();
                    time = stopWatch.getLastTaskTimeMillis();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                
                executeMessage.setExecuteTime(time);
                executeMessage.setExecuteMemory(maxMemory[0]);
                executeMessages.add(executeMessage);
            }
            statsCmd.close();
        }
        ListContainersCmd listContainersCmd = dockerClient.listContainersCmd();
        List<Container> containers = listContainersCmd.exec();
        for (Container container : containers) {
            System.out.println(container);
            dockerClient.removeContainerCmd(container.getId()).withForce(true).exec();
        }

        if (userCodeFile.getParentFile() != null) {
            boolean delRes = FileUtil.del(userCodeFile.getParentFile());
            System.out.println("删除 " + (delRes ? "成功" : "失败"));
        }
        
        System.out.println(executeMessages);

        return executeCodeResponse;
    }

    private File createResultCodeTmpFile(String userCodeString) {
        String userDir = System.getProperty("user.dir");
        String codeTestName = userDir + File.separator + GLOBAL_TMPER;
        if (!FileUtil.exist(codeTestName)) {
            FileUtil.mkdir(codeTestName);
        }

        String userCodPath = codeTestName + File.separator + UUID.randomUUID();
        File userCodeFile = FileUtil.writeString(
                userCodeString, 
                userCodPath + File.separator + GLOBAL_JAVA_CLASS_NAME, 
                StandardCharsets.UTF_8
        );

        return userCodeFile;
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


    private static String [] BuildJavaExecuteCommand(String input) {
        // sh -c "echo test_input | java -cp /app Main"
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("echo ");
        for (String elem : input.split(" ")) {
            stringBuilder.append(elem);
            stringBuilder.append(" ");
        }

        stringBuilder.append(" | java -cp ./app -Dfile.encoding=UTF-8 Main");

        return new String[] { "sh", "-c", stringBuilder.toString() };
    }
}
