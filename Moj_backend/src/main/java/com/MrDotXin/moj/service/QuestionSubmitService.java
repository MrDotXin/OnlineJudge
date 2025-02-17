package com.MrDotXin.moj.service;

import javax.servlet.http.HttpServletRequest;

import com.MrDotXin.moj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.MrDotXin.moj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.MrDotXin.moj.model.entity.QuestionSubmit;
import com.MrDotXin.moj.model.entity.User;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.MrDotXin.moj.model.vo.QuestionSubmitVO;
public interface QuestionSubmitService extends IService<QuestionSubmit>{
    /**
     * 点赞
     *
     * @param questionId
     * @param loginUser
     * @return
     */
    long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);

        /**
     * 获取查询条件
     *
     * @param questionQueryRequest
     * @return
     */
    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionQueryRequest);

    /**
     * 获取题目封装
     *
     * @param question
     * @param loginUser
     * @return
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser);

    /**
     * 分页获取题目封装
     *
     * @param questionPage
     * @param request
     * @return
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, HttpServletRequest request);
}
