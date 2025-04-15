package com.mrdotxin.mojbackendjudgeservice.service;

import com.mrdotxin.moj.backend.model.vo.QuestionSubmitVO;

public interface JudgeService {
    /*  
     * 判题服务
     */
    public QuestionSubmitVO doJudge(long questionSubmitId);
}
