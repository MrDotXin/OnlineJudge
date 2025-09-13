package com.MrDotXin.moj.controller;

import com.MrDotXin.moj.common.BaseResponse;
import com.MrDotXin.moj.common.ErrorCode;
import com.MrDotXin.moj.common.ResultUtils;
import com.MrDotXin.moj.exception.BusinessException;
import com.MrDotXin.moj.judge.JudgeService;
import com.MrDotXin.moj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.MrDotXin.moj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.MrDotXin.moj.model.entity.QuestionSubmit;
import com.MrDotXin.moj.model.entity.User;
import com.MrDotXin.moj.model.vo.QuestionSubmitVO;
import com.MrDotXin.moj.service.QuestionSubmitService;
import com.MrDotXin.moj.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 题目提交接口
 *
 * 
 * 
 */
@RestController
@RequestMapping("/question/question_submit")
public class QuestionSubmitController {

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private UserService userService;

    @Resource
    private JudgeService judgeService;
    /**
     * 提交 / 取消提交题目
     *
     * @param questionSubmitAddRequest 从前端传过来的请求数据
     * @param request
     * @return resultNum 本次提交变化数
     */
    @PostMapping("/")
    public BaseResponse<QuestionSubmitVO> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
            HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能提交
        final User loginUser = userService.getLoginUser(request);
        // 提交到数据库中
        long result = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);

        QuestionSubmitVO vo = judgeService.doJudge(result);

        return ResultUtils.success(vo);
    }

    /**
     * 分页获取题目提交列表（仅管理员测试用）
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,
            HttpServletRequest request) {
        long current = questionSubmitQueryRequest.getCurrent();
        long size = questionSubmitQueryRequest.getPageSize();
        // 得到从数据库中的原始信息
        Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, size),
                questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));

        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage, request));
    }

}
