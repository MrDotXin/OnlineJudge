package com.MrDotXin.moj.judge;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.MrDotXin.moj.common.ErrorCode;
import com.MrDotXin.moj.exception.BusinessException;
import com.MrDotXin.moj.judge.codesandbox.CodeSandbox;
import com.MrDotXin.moj.judge.codesandbox.CodeSandboxFactory;
import com.MrDotXin.moj.judge.codesandbox.model.ExecuteCodeRequest;
import com.MrDotXin.moj.judge.codesandbox.model.ExecuteCodeResponse;
import com.MrDotXin.moj.model.dto.question.JudgeCase;
import com.MrDotXin.moj.model.dto.question.JudgeConfig;
import com.MrDotXin.moj.model.dto.questionsubmit.JudgeInfo;
import com.MrDotXin.moj.model.entity.Question;
import com.MrDotXin.moj.model.entity.QuestionSubmit;
import com.MrDotXin.moj.model.enums.JudgeInfoMessageEnum;
import com.MrDotXin.moj.model.vo.QuestionSubmitVO;
import com.MrDotXin.moj.service.QuestionService;
import com.MrDotXin.moj.service.QuestionSubmitService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.hutool.json.JSONUtil;


@Service
public class JudgeServiceImpl implements JudgeService{

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Value("${codesandbox.type}")
    String type;

    @Override
    public QuestionSubmitVO doJudge(long questionSubmitId) {
        // - 获取题目ID, 获取对应的题目提交信息(代码, 编程语言)
        // - 调用沙箱, 获取执行结果
        // - 根据沙箱的执行结果, 设置提交状态
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
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

        Question question = questionService.getById(questionId);

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
    
        String judgeInfoString = JSONUtil.toJsonStr(judgeInfo);
        questionSubmit.setJudgeInfo(judgeInfoString);

    }

    private void updateSubmitInfo(QuestionSubmit questionSubmit) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", questionSubmit.getId());

        questionSubmitService.update(questionSubmit, queryWrapper);
    }

}
