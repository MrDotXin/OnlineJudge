package com.mrdotxin.exampleSandbox.utils;

import com.mrdotxin.exampleSandbox.codeSandbox.model.ExecuteCodeErrorInfo;
import com.mrdotxin.exampleSandbox.codeSandbox.model.JudgeInfo;
import com.mrdotxin.exampleSandbox.codeSandbox.model.enums.JudgeInfoMessageEnum;

public class ExecuteErrorInfoUtils {
    
    public static ExecuteCodeErrorInfo asCompilerError(String output) {
        ExecuteCodeErrorInfo errorInfo = new ExecuteCodeErrorInfo();

        errorInfo.setOutput(output);
        errorInfo.setErrorType(JudgeInfoMessageEnum.COMPILER_ERROR.getText());
        
        return errorInfo;
    }

    public static ExecuteCodeErrorInfo asTimeLimitExceeded(String output, String input, Long executeTime) {
        ExecuteCodeErrorInfo errorInfo = new ExecuteCodeErrorInfo();
        errorInfo.setErrorType(JudgeInfoMessageEnum.TIME_LIMIT_EXCCEED.getText());
        errorInfo.setInput(input);
        errorInfo.setOutput(output);
        errorInfo.setJudgeInfo(new JudgeInfo());
        errorInfo.getJudgeInfo().setTime(executeTime);
        return errorInfo;
    }

    public static ExecuteCodeErrorInfo asMemoryLimitExceeded(String output, String input, Long executeTime) {
        ExecuteCodeErrorInfo errorInfo = new ExecuteCodeErrorInfo();
        errorInfo.setErrorType(JudgeInfoMessageEnum.MEMORY_LIMIT_EXCCEED.getText());
        errorInfo.setInput(input);
        errorInfo.setOutput(output);
        return errorInfo;
    }

    public static ExecuteCodeErrorInfo asRuntimeError(String output, String input) {
        ExecuteCodeErrorInfo errorInfo = new ExecuteCodeErrorInfo();
        errorInfo.setErrorType(JudgeInfoMessageEnum.RUNTIME_ERROR.getText());
        errorInfo.setInput(input);
        errorInfo.setOutput(output);
        return errorInfo;
    }

    public static ExecuteCodeErrorInfo asFormatterError(String output, String input) {
        ExecuteCodeErrorInfo errorInfo = new ExecuteCodeErrorInfo();
        errorInfo.setErrorType(JudgeInfoMessageEnum.PRESENTATION_ERROR.getText());
        errorInfo.setInput(input);
        errorInfo.setOutput(output);
        return errorInfo;    
    }

    public static ExecuteCodeErrorInfo asWrongAnswerError(String output, String input) {
        ExecuteCodeErrorInfo errorInfo = new ExecuteCodeErrorInfo();
        errorInfo.setErrorType(JudgeInfoMessageEnum.WRONG_ANSWER.getText());
        errorInfo.setInput(input);
        errorInfo.setOutput(output);
        return errorInfo;    
    }
}
