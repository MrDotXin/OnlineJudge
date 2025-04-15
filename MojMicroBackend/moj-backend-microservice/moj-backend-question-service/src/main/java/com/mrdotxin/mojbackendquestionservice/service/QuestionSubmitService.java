package com.mrdotxin.mojbackendquestionservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mrdotxin.moj.backend.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.mrdotxin.moj.backend.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.mrdotxin.moj.backend.model.entity.QuestionSubmit;
import com.mrdotxin.moj.backend.model.entity.User;
import com.mrdotxin.moj.backend.model.vo.QuestionSubmitVO;

import javax.servlet.http.HttpServletRequest;
public interface QuestionSubmitService extends IService<QuestionSubmit>{
    /**
     * 点赞
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
     * @param loginUser
     * @return
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser);

    /**
     * 分页获取题目封装
     *
     * @param request
     * @return
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, HttpServletRequest request);
}
