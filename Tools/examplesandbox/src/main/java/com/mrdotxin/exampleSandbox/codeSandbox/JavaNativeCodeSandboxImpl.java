package com.mrdotxin.exampleSandbox.codeSandbox;

import com.mrdotxin.exampleSandbox.codeSandbox.model.ExecuteCodeResponse;
import com.mrdotxin.exampleSandbox.codeSandbox.model.ExecuteMessage;
import com.mrdotxin.exampleSandbox.codeSandbox.model.JudgeInfo;
import com.mrdotxin.exampleSandbox.utils.ProcessUtils;
import com.mrdotxin.exampleSandbox.utils.StreamUtils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.mrdotxin.exampleSandbox.codeSandbox.model.ExecuteCodeRequest;

public class JavaNativeCodeSandboxImpl implements CodeSandbox {

    private final String GLOBAL_TMPER = "tmpcode";

    private final String GLOBAL_JAVA_CLASS_NAME = "Main.java";

    private final long TIME_OUT = 5000l; 

    public static void main(String argc[]) {
        
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setInputList(Arrays.asList("1 2", "1 3"));

        String code = ResourceUtil.readStr("testCode\\MemoryError\\Main.java", StandardCharsets.UTF_8);
        System.out.println(code);

        executeCodeRequest.setCode(code);
        executeCodeRequest.setLanguage("java");

        CodeSandbox codeSandbox = new JavaNativeCodeSandboxImpl();
    
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);

        System.out.println(executeCodeResponse);
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {

        List<String> inpuList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();

        File userCodeFile = createResultCodeTmpFile(code);

        String compilerCode = String.format("javac -encoding utf-8 %s", userCodeFile.getAbsolutePath());

        Process compilerProcess = ProcessUtils.buildCommandProcess(compilerCode);
        ExecuteMessage executeMessage = ProcessUtils.runProcess(compilerProcess);

        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setJudgeInfo(new JudgeInfo());

        if (executeMessage.getExitValue() != 0) {
            System.out.println(executeMessage.getErrorMessage());
            executeCodeResponse.setMessage("编译错误");
            executeCodeResponse.setStatus(1);
        } else {
            List<String> outputLists = new LinkedList<String>();
            executeCodeResponse.setMessage("运行成功");
            Long executeTimes = 0l;
            executeCodeResponse.setStatus(1);
            for (String inputArgs : inpuList) {
                String runCmd = 
                    String.format(
                        "java -Xmx512m -Dfile.encoding=UTF-8 -cp %s Main", 
                        userCodeFile.getParentFile().getAbsolutePath()  
                    );  

                Process runProcess = ProcessUtils.buildCommandProcess(runCmd);                
                
                StreamUtils.writeString(runProcess.getOutputStream(), inputArgs + '\n');
                
                Thread thread = new Thread(() -> {
                    try {
                        Thread.sleep(TIME_OUT);
                        if (runProcess.isAlive()) {
                            executeCodeResponse.setMessage("运行超时!");
                            executeCodeResponse.setStatus(4);
                            runProcess.destroy();
                        }
                    } catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                });

                thread.start();

                ExecuteMessage runOutput = ProcessUtils.runProcess(runProcess);

                if (executeCodeResponse.getStatus() == 4) {
                    break;
                }

                executeTimes = Math.max(
                    executeTimes, 
                    runOutput.getExecuteTime()
                );


                if (runOutput.getExitValue() != 0) {
                    executeCodeResponse.setMessage("运行异常 " + runOutput.getExitValue());
                    executeCodeResponse.setOutputList(Arrays.asList(runOutput.getErrorMessage()));
                    executeCodeResponse.setStatus(3);
                    break;
                } else {
                    outputLists.add(runOutput.getMessage());
                }

                runProcess.destroy();
                thread.interrupt();
            }

            if (userCodeFile.getParentFile() != null) {
                boolean delRes = FileUtil.del(userCodeFile.getParentFile());
                System.out.println("删除 " + (delRes ? "成功" : "失败"));
            }

            executeCodeResponse.setOutputList(outputLists);
            executeCodeResponse.getJudgeInfo().setTime(executeTimes);
        }
        

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
}
