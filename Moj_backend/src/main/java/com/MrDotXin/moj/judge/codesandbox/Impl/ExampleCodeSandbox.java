package com.MrDotXin.moj.judge.codesandbox.Impl;

import com.MrDotXin.moj.judge.codesandbox.CodeSandbox;
import com.MrDotXin.moj.judge.codesandbox.model.ExecuteCodeRequest;
import com.MrDotXin.moj.judge.codesandbox.model.ExecuteCodeResponse;
import com.MrDotXin.moj.model.dto.questionsubmit.JudgeInfo;
import com.MrDotXin.moj.model.enums.JudgeInfoMessageEnum;
import com.MrDotXin.moj.model.enums.QuestionSubmitStatusEnum;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 实例代码沙箱: 仅测试用
 * 
 */
@Slf4j
public class ExampleCodeSandbox implements CodeSandbox {

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        log.info("实例代码沙箱启动!");
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();

        executeCodeResponse.setOutputList(executeCodeRequest.getInputList());
        executeCodeResponse.setMessage("执行成功");
        executeCodeResponse.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMemory(100L);
        judgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getText());
        judgeInfo.setTime(100L);

        executeCodeResponse.setJudgeInfo(judgeInfo);

        return executeCodeResponse;
    }
    
}