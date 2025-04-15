package com.mrdotxin.mojbackendjudgeservice.service.impl;

import cn.hutool.json.JSONUtil;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mrdotxin.moj.backend.common.common.ErrorCode;
import com.mrdotxin.moj.backend.common.exception.BusinessException;
import com.mrdotxin.moj.backend.model.codesandbox.ExecuteCodeRequest;
import com.mrdotxin.moj.backend.model.codesandbox.ExecuteCodeResponse;
import com.mrdotxin.moj.backend.model.dto.question.JudgeCase;
import com.mrdotxin.moj.backend.model.dto.question.JudgeConfig;
import com.mrdotxin.moj.backend.model.dto.questionsubmit.JudgeInfo;
import com.mrdotxin.moj.backend.model.entity.Question;
import com.mrdotxin.moj.backend.model.entity.QuestionSubmit;
import com.mrdotxin.moj.backend.model.enums.JudgeInfoMessageEnum;
import com.mrdotxin.moj.backend.model.vo.QuestionSubmitVO;
import com.mrdotxin.mojbackendjudgeservice.codesandbox.CodeSandbox;
import com.mrdotxin.mojbackendjudgeservice.codesandbox.CodeSandboxFactory;
import com.mrdotxin.mojbackendjudgeservice.service.JudgeService;
import com.mrdotxin.mojbackendserviceclient.service.QuestionFeignClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


@Service
public class JudgeServiceImpl implements JudgeService {

    @Resource
    private QuestionFeignClient questionFeignClient;

    @Value("${codesandbox.type}")
    String type;

    @Override
    public QuestionSubmitVO doJudge(long questionSubmitId) {
        // - 获取题目ID, 获取对应的题目提交信息(代码, 编程语言)
        // - 调用沙箱, 获取执行结果
        // - 根据沙箱的执行结果, 设置提交状态
        QuestionSubmit questionSubmit = questionFeignClient.getQuestionSubmitById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交信息不存在");
        }

        questionSubmit.setStatus(JudgeInfoMessageEnum.RUNNING.getValue());
        updateSubmitInfo(questionSubmit);
        
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        ExecuteCodeRequest request = buildExecuteRequest(questionSubmit);
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(request);
        
        resetJudgeStatus(executeCodeResponse, questionSubmit);
        updateSubmitInfo(questionSubmit);

        return QuestionSubmitVO.objToVo(questionSubmit);
    } 


    private ExecuteCodeRequest buildExecuteRequest(QuestionSubmit questionSubmit) {
        Long questionId = questionSubmit.getQuestionId();

        Question question = questionFeignClient.getQuestionById(questionId);

        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        
        List<String> inputs = new LinkedList<String>();
        List<String> outputs = new LinkedList<String>();
        parseJudgeCase(question.getJudgeCase(), inputs, outputs);
        executeCodeRequest.setAnswers(outputs);
        executeCodeRequest.setInputList(inputs);

        executeCodeRequest.setLanguage(questionSubmit.getLanguage());
        executeCodeRequest.setCode(questionSubmit.getCode());

        JudgeConfig judgeConfig = JSONUtil.toBean(question.getJudgeConfig(), JudgeConfig.class);
        executeCodeRequest.setMemoryLimit(1024 * 1024 * 1024l);
        executeCodeRequest.setTimeLimit(judgeConfig.getTimeLimit());
        executeCodeRequest.setCodeCompilerMemoryLimit(1024 * 1024 * 1024l);

        return executeCodeRequest;
    }

    private void parseJudgeCase(String judgeCases, List<String> inputs, List<String> outputs) {
        List<JudgeCase> data = JSONUtil.toList(judgeCases, JudgeCase.class);
        for (JudgeCase judgeCase : data) {
            inputs.add(judgeCase.getInput());
            outputs.add(judgeCase.getOutput());
        }
    }

    private void resetJudgeStatus(ExecuteCodeResponse executeCodeResponse, QuestionSubmit questionSubmit) {
        JudgeInfo judgeInfo = executeCodeResponse.getJudgeInfo();

        questionSubmit.setStatus(executeCodeResponse.getStatus_type());

        if (judgeInfo == null) {
            judgeInfo = new JudgeInfo();
        }

        judgeInfo.setMessage(StringUtils.join(executeCodeResponse.getOutputList()));
        String judgeInfoString = JSONUtil.toJsonStr(judgeInfo);
        questionSubmit.setJudgeInfo(judgeInfoString);

    }

    private void updateSubmitInfo(QuestionSubmit questionSubmit) {
        questionFeignClient.updateQuestionSubmitById(questionSubmit);
    }

}
