package com.mrdotxin.mojbackendjudgeservice.codesandbox.strategy;


import com.mrdotxin.moj.backend.model.codesandbox.ExecuteCodeRequest;
import com.mrdotxin.moj.backend.model.codesandbox.ExecuteCodeResponse;
import com.mrdotxin.moj.backend.model.dto.questionsubmit.JudgeInfo;
import com.mrdotxin.moj.backend.model.enums.JudgeInfoMessageEnum;
import com.mrdotxin.moj.backend.model.enums.QuestionSubmitStatusEnum;
import com.mrdotxin.mojbackendjudgeservice.codesandbox.CodeSandbox;
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