package com.MrDotXin.moj.judge;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.MrDotXin.moj.common.ErrorCode;
import com.MrDotXin.moj.exception.BusinessException;
import com.MrDotXin.moj.model.entity.QuestionSubmit;
import com.MrDotXin.moj.model.vo.QuestionSubmitVO;
import com.MrDotXin.moj.service.QuestionService;
import com.MrDotXin.moj.service.QuestionSubmitService;


@Service
public class JudgeServiceImpl implements JudgeService{

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Override
    public QuestionSubmitVO doJudge(long questionSubmitId) {
        // - 获取题目ID, 获取对应的题目提交信息(代码, 编程语言)
        // - 调用沙箱, 获取执行结果
        // - 根据沙箱的执行结果, 设置提交状态
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交信息不存在");
        }

        return null;
    } 
}
