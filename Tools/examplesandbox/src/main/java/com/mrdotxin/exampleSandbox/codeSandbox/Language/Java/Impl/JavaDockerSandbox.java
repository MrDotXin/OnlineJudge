package com.mrdotxin.exampleSandbox.codeSandbox.Language.Java.Impl;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.StatsCmd;
import com.github.dockerjava.api.command.InspectContainerResponse.ContainerState;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.Statistics;
import com.github.dockerjava.api.model.StreamType;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.mrdotxin.exampleSandbox.codeSandbox.Exception.RunningException;
import com.mrdotxin.exampleSandbox.codeSandbox.Language.Java.Java18Sandbox;
import com.mrdotxin.exampleSandbox.codeSandbox.model.ExecuteCodeRequest;
import com.mrdotxin.exampleSandbox.codeSandbox.model.ExecuteCommandInfo;
import com.mrdotxin.exampleSandbox.codeSandbox.model.ExecuteMessage;
import com.mrdotxin.exampleSandbox.utils.DockerAPIUtils;
import com.mrdotxin.exampleSandbox.utils.ExecuteErrorInfoUtils;

import cn.hutool.core.date.StopWatch;

public class JavaDockerSandbox extends Java18Sandbox {
    
    private final String DOCKER_IMAGE_NAME = "openjdk:8-alpine";

    private DockerClient dockerClient = null; 

    private Long defaultCompilerTimeout = 3000l;

    private Long DOCKER_ERROR_SIGKILL = 137l;

    // private Long DOCKER_ERROR_SIGSEGV = 139l;
    /*
     * 编译操作, 也许会抛出编译错误
     */
    @Override
    public ExecuteMessage compilerUserCode(File userCodeFile, Long memoryLimit) throws RunningException {
        dockerClient = getDockerClient();

        String containerId = 
            DockerAPIUtils.CreateConstraintContainer(
                dockerClient, 
                DOCKER_IMAGE_NAME, 
                memoryLimit,
                0l,
                1l, 
                userCodeFile.getParentFile().getAbsolutePath(), 
                "/app"
        );

        DockerAPIUtils.RunContainer(dockerClient, containerId);

        ExecuteCommandInfo commandInfo = DockerAPIUtils.executeCommandTimeout(
            dockerClient, 
            containerId, 
            30000l,
            new String[] {"javac", "-encoding", "utf-8", "./app/Main.java"}
        );

        try {
            if (commandInfo.getCode() != 0) {
                if (commandInfo.getCode() == DOCKER_ERROR_SIGKILL) {
                    if (commandInfo.getTimeout() / defaultCompilerTimeout >= 1.1) {
                        throw new RunningException(ExecuteErrorInfoUtils.asCompilerError(commandInfo.getOutput() + " 编译时间超时"));
                    } else {
                        throw new RunningException(ExecuteErrorInfoUtils.asCompilerError(commandInfo.getOutput() + " 文件大小超限!"));
                    } 
                } else {
                    throw new RunningException(ExecuteErrorInfoUtils.asCompilerError(commandInfo.getOutput() + " 系统异常!"));
                }
            } else if (!commandInfo.getOutput().isEmpty()) {
                throw new RunningException(ExecuteErrorInfoUtils.asCompilerError(commandInfo.getOutput()));
            }
        } catch(RunningException exception) {
            DockerAPIUtils.deleteContainer(dockerClient, containerId);

            throw exception;
        }

        DockerAPIUtils.deleteContainer(dockerClient, containerId);

        return null;
    }
    
    /**
     * 
     * @param executeCodeRequest
     * @return 返回正确信息
     * @throws RunningException 当某一个测试用例出错的时候, 会抛出异常
     */
    
    @Override
    public ExecuteMessage runUserFile(File userCodeFile, ExecuteCodeRequest executeCodeRequest) throws RunningException {
        dockerClient = getDockerClient();
        
        String containerId = CreateRunningConstraintContainer(dockerClient, userCodeFile, executeCodeRequest.getMemoryLimit());
        ExecuteMessage executeMessage = new ExecuteMessage();

        Long [] maxMemory = {0l};

        StatsCmd statsCmd = null;
        try {
            boolean [] timeout = {true};

            statsCmd = startResourceMonitoring(dockerClient, containerId, timeout, maxMemory);

            executeMessage = processAllTestCases(
                    dockerClient, containerId, 
                    executeCodeRequest.getInputList(), executeCodeRequest.getAnswers(), 
                    timeout, executeCodeRequest.getTimeLimit()
                );

        } catch(RunningException exception) {
            cleaningResource(dockerClient, statsCmd, containerId);
            
            throw exception;
        }

        cleaningResource(dockerClient, statsCmd, containerId);

        executeMessage.setExecuteMemory(maxMemory[0]);

        return executeMessage;
    }

    
    private DockerClient getDockerClient() {
        if (dockerClient == null) {
            DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
    
            // 使用Apache HttpClient5
            ApacheDockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                    .dockerHost(config.getDockerHost())
                    .sslConfig(config.getSSLConfig())
                    .build(); 
    
            dockerClient = DockerClientImpl.getInstance(config, httpClient);
        }

        return dockerClient;
    }

    private String CreateRunningConstraintContainer(DockerClient dockerClient, File userCodeFile, Long memoryLimit) {

        String id = DockerAPIUtils.CreateConstraintContainer(
            dockerClient, 
            DOCKER_IMAGE_NAME, 
            memoryLimit, 
            0l, 
            1l, 
            userCodeFile.getParentFile().getAbsolutePath(), 
            "/app"
        );

        DockerAPIUtils.RunContainer(dockerClient, id);

        return id;
    }

    private StatsCmd startResourceMonitoring(
        DockerClient dockerClient,
        String containerId,
        boolean [] timeout,
        Long [] maxMemory
    ) {
        ResultCallback<Statistics> resultCallback = new ResultCallback<Statistics>() {
            @Override
            public void onNext(Statistics statistics) {
                maxMemory[0] = Math.max(maxMemory[0], statistics.getMemoryStats().getUsage());
            }   
            @Override public void onComplete() { timeout[0] = false; }
            @Override public void close() throws IOException {}
            @Override public void onError(Throwable throwable) {}
            @Override public void onStart(Closeable closeable) {}
        };

        // 开启实时监测
        StatsCmd statsCmd = dockerClient.statsCmd(containerId);
        statsCmd.exec(resultCallback);

        
        return statsCmd;
    }

    private ExecuteMessage processAllTestCases(
        DockerClient dockerClient,
        String containerId,
        List<String> inuputs, 
        List<String> answers,
        boolean [] timeout,
        Long timeLimits
    ) throws RunningException {
        StopWatch stopWatch = new StopWatch();
        
        Long maxTime = 0l;
        ExecuteMessage executeMessage = new ExecuteMessage();

        Iterator<String> inputIterator = inuputs.iterator();
        Iterator<String> answersIterator = answers.iterator();

        while (inputIterator.hasNext() && answersIterator.hasNext()) {

            String input = inputIterator.next();
            String answer = answersIterator.next();
            timeout[0] = true;
            
            stopWatch.start();
            ExecuteMessage singlExecuteMessage = executingSingleCase(dockerClient, containerId, input, answer,timeLimits, timeout);
            stopWatch.stop();
            
            Long time = stopWatch.getLastTaskTimeMillis();
            maxTime = Math.max(maxTime, time);

            // 这里可以截停出去
            validateError(dockerClient, containerId, singlExecuteMessage, timeout[0], input, time);

            // 采用分层架构可以复杂化判题机制, 比如交互题, 函数, 特殊得分, 特殊精度, 判题程序
            validateAnswer(singlExecuteMessage.getMessage(), answer);
            
        }

        executeMessage.setExecuteTime(maxTime);
        
        return executeMessage;
    }
    
    /***
     * 
     * @param dockerClient
     * @param executeMessage
     * @param timeout
     */
    private void validateError(
        DockerClient dockerClient,
        String containerId,
        ExecuteMessage executeMessage, 
        boolean timeout,
        String input,
        Long time
    ) throws RunningException {
        if (timeout) {
            throw new RunningException(
                ExecuteErrorInfoUtils.asTimeLimitExceeded(
                    executeMessage.getErrorMessage(), 
                    input, 
                    time
                )
            );
        }

        ContainerState state = DockerAPIUtils.getContainerStatus(dockerClient, containerId);
        if (!state.getRunning()) {
            if (state.getExitCodeLong() == DOCKER_ERROR_SIGKILL) {
                throw new RunningException(
                    ExecuteErrorInfoUtils.asMemoryLimitExceeded(
                        executeMessage.getErrorMessage(),
                        input, 
                        time
                    )
                );
            }
        }

        // 运行错误
        if (executeMessage.getExitValue() != 0) {
            throw new RunningException(
                ExecuteErrorInfoUtils.asRuntimeError(
                    executeMessage.getErrorMessage(), 
                    input
                )
            );
        }
    }


    /***
     * 仅用于跑通流程, 判题没有这么简单
     * @param output
     * @param answer
     * @throws RunningException
     */
    private void validateAnswer(String output, String answer) throws RunningException {
        String[] outputLine = output.trim().split("\n");
        String[] answerLine = answer.trim().split("\n");
    
        int n = outputLine.length;
        if (n != answerLine.length) {
            throw new RunningException(ExecuteErrorInfoUtils.asFormatterError(output, answer));
        }
    
        for (int i = 0; i < n; i++) {
            String[] outputElem = outputLine[i].trim().split("\\s+");
            String[] answerElem = answerLine[i].trim().split("\\s+");
    
            int m = outputElem.length;
            if (m != answerElem.length) {
                throw new RunningException(ExecuteErrorInfoUtils.asFormatterError(output, answer));
            }
    
            for (int j = 0; j < m; j++) {
                if (!outputElem[j].equals(answerElem[j])) {
                    throw new RunningException(ExecuteErrorInfoUtils.asWrongAnswerError(output, answer));
                }
            }
        }
    }

    /***
     * 运行单个案例的函数, 可以抽象出来, 让判题机可以跑交互题
     * @param dockerClient
     * @param containerId
     * @param input
     * @param answer
     * @param timeLimits
     * @return
     * @throws RunningException
     */
    private ExecuteMessage executingSingleCase(
        DockerClient 
        dockerClient, 
        String containerId, 
        String input, 
        String answer,
        Long timeLimits,
        boolean [] timeout
    ) throws RunningException {
        
        String excId = createAndGetExecuteCmdId(dockerClient, containerId, input);
        
        ExecuteMessage executeMessage = new ExecuteMessage();

        StringBuilder errorMessage = new StringBuilder();
        StringBuilder message = new StringBuilder();
        try {
            dockerClient.execStartCmd(excId).exec(new ResultCallback.Adapter<Frame>() {
                @Override
                public void onNext(Frame frame) {
                    if (frame.getStreamType().equals(StreamType.STDERR)) {
                        errorMessage.append(new String(frame.getPayload()));
                        errorMessage.append('\n');
                    } else {
                        message.append(new String(frame.getPayload()));
                        message.append('\n');
                    }
                }

                @Override
                public void onComplete() { timeout[0] = false; }
            }).awaitCompletion(timeLimits, TimeUnit.MILLISECONDS);

            if (errorMessage.length() != 0) {
                executeMessage.setExitValue(1);
                executeMessage.setErrorMessage(errorMessage.toString());
            } else {
                executeMessage.setExitValue(0);
                executeMessage.setMessage(message.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return executeMessage;
    }

    private String createAndGetExecuteCmdId(
        DockerClient dockerClient,
        String containerId,
        String input
    ) {
        ExecCreateCmdResponse response = dockerClient.execCreateCmd(containerId)
                            .withCmd(BuildJavaCodeExecuteCommand(input))
                            .withAttachStderr(true)
                            .withAttachStdin(true)
                            .withAttachStdout(true)
                            .withTty(true)
                            .exec();       
        String excId = response.getId();

        return excId;
    }

    private void cleaningResource(
        DockerClient dockerClient,
        StatsCmd cmd,
        String containerId
    ) {
        if (cmd != null) {
            cmd.close();
        }

        DockerAPIUtils.deleteContainer(dockerClient, containerId);
    }


    private String [] BuildJavaCodeExecuteCommand(String input) {
        // sh -c "echo test_input | java -cp /app Main"
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("echo \" ");
        for (String elem : input.split(" ")) {
            stringBuilder.append(elem);
            stringBuilder.append(" ");
        }

        stringBuilder.append(" \" | java -cp ./app -Dfile.encoding=UTF-8 Main");

        return new String[] { "sh", "-c", stringBuilder.toString() };
    }
    
}
