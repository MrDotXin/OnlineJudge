package com.mrdotxin.mojbackendquestionservice.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mrdotxin.moj.backend.common.common.BaseResponse;
import com.mrdotxin.moj.backend.common.common.ErrorCode;
import com.mrdotxin.moj.backend.common.common.ResultUtils;
import com.mrdotxin.moj.backend.common.exception.BusinessException;
import com.mrdotxin.moj.backend.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.mrdotxin.moj.backend.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.mrdotxin.moj.backend.model.entity.QuestionSubmit;
import com.mrdotxin.moj.backend.model.entity.User;
import com.mrdotxin.moj.backend.model.vo.QuestionSubmitVO;
import com.mrdotxin.mojbackendquestionservice.service.QuestionSubmitService;
import com.mrdotxin.mojbackendserviceclient.service.UserFeignClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 题目提交接口
 *
 * 
 * 
 */
@RestController
@RequestMapping("/question_submit")
public class QuestionSubmitController {

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private UserFeignClient userService;

    /**
     * 提交 / 取消提交题目
     *
     * @param questionSubmitAddRequest 从前端传过来的请求数据
     * @param request
     * @return resultNum 本次提交变化数
     */
    @PostMapping("/")
    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
                                                           HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能提交
        final User loginUser = userService.getLoginUser(request);
        // 提交到数据库中
        long result = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);

        return ResultUtils.success(result);
    }

    /**
     * 分页获取题目提交列表（仅管理员测试用）
     *
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

    @GetMapping("/get/id")
    public BaseResponse<QuestionSubmitVO> getQuestionSubmitById(@RequestParam("id") long id) {
        QuestionSubmit questionSubmit = questionSubmitService.getById(id);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "错误! 提交不存在");
        }

        return ResultUtils.success(QuestionSubmitVO.objToVo(questionSubmit));
    }
}
