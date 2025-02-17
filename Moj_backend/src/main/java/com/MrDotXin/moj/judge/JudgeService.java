package com.MrDotXin.moj.judge;

import com.MrDotXin.moj.model.vo.QuestionSubmitVO;

public interface JudgeService {
    /*  
     * 判题服务
     */
    public QuestionSubmitVO doJudge(long questionSubmitId);
}
