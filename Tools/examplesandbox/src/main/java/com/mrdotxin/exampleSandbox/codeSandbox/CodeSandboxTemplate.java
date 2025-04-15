package com.mrdotxin.exampleSandbox.codeSandbox;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import com.mrdotxin.exampleSandbox.codeSandbox.Exception.RunningException;
import com.mrdotxin.exampleSandbox.codeSandbox.model.ExecuteCodeRequest;
import com.mrdotxin.exampleSandbox.codeSandbox.model.ExecuteCodeResponse;
import com.mrdotxin.exampleSandbox.codeSandbox.model.ExecuteMessage;
import com.mrdotxin.exampleSandbox.codeSandbox.model.JudgeInfo;
import com.mrdotxin.exampleSandbox.codeSandbox.model.enums.JudgeInfoMessageEnum;

import cn.hutool.core.io.FileUtil;

public abstract class CodeSandboxTemplate implements CodeSandbox {


    private final String GLOBAL_TMPER = "tmpcode";

    private final String GLOBAL_JAVA_CLASS_NAME = "Main.java";

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        ExecuteCodeResponse response = new ExecuteCodeResponse();

        File userCodeFile = CreateTempFile(executeCodeRequest.getCode());

        try {
            compilerUserCode(userCodeFile, executeCodeRequest.getCodeCompilerMemoryLimit());
            
            ExecuteMessage runMessage = runUserFile(userCodeFile, executeCodeRequest);

            response.setStatus(0);
            response.setStatus_type(JudgeInfoMessageEnum.ACCEPTED.getText());
            response.setJudgeInfo(new JudgeInfo(
                JudgeInfoMessageEnum.ACCEPTED.getText(),
                runMessage.getExecuteTime(),
                runMessage.getExecuteMemory()
            ));
        } catch(RunningException e) {
            e.UpdateResponseInfo(response);
        }
        
        DeleteTempFile(userCodeFile);
        return response;
    }


    public File CreateTempFile(String userCode) {
    String userDir = System.getProperty("user.dir");
        String codeTestName = userDir + File.separator + GLOBAL_TMPER;
        if (!FileUtil.exist(codeTestName)) {
            FileUtil.mkdir(codeTestName);
        }

        String userCodPath = codeTestName + File.separator + UUID.randomUUID();
        File userCodeFile = FileUtil.writeString(
                userCode, 
                userCodPath + File.separator + GLOBAL_JAVA_CLASS_NAME, 
                StandardCharsets.UTF_8
        );

        return userCodeFile;
    }
    

    public void DeleteTempFile(File userCodeFile) {
        if (userCodeFile.getParentFile() != null) {
            FileUtil.del(userCodeFile.getParentFile());
        }
    }
    
    /***
     * 编译操作, 也许会抛出编译错误
     * @param userCodeFile
     * @param codeCompilerLimit
     * @return 返回执行信息
     * @throws RunningException 包含编译错误, 编译时间过长, 编译过程中的中间代码过于复杂
     */
    public abstract ExecuteMessage compilerUserCode(File userCodeFile, Long codeCompilerLimit) throws RunningException;

    /**
     * 
     * @param executeCodeRequest
     * @return 返回正确信息
     * @throws RunningException 运行错误, 运行超时, 运行超过时间限制, 答案有错误
     */
    public abstract ExecuteMessage runUserFile(File userCodeFile, ExecuteCodeRequest executeCodeRequest) throws RunningException;
} 
