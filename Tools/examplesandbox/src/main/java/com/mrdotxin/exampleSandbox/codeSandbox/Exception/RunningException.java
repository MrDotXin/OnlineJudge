package com.mrdotxin.exampleSandbox.codeSandbox.Exception;

import java.util.Arrays;

import com.mrdotxin.exampleSandbox.codeSandbox.model.ExecuteCodeErrorInfo;
import com.mrdotxin.exampleSandbox.codeSandbox.model.ExecuteCodeResponse;

public class RunningException extends RuntimeException {

    ExecuteCodeErrorInfo executeMessage;

    public RunningException(ExecuteCodeErrorInfo message) {
        executeMessage = message;
    } 

    public void UpdateResponseInfo(ExecuteCodeResponse executeCodeResponse) {
        executeCodeResponse.setJudgeInfo(executeMessage.getJudgeInfo());
        executeCodeResponse.setStatus_type(executeMessage.getErrorType());
        executeCodeResponse.setOutputList(Arrays.asList(executeMessage.getOutput()));
        executeCodeResponse.setStatus(1);
    }
}
