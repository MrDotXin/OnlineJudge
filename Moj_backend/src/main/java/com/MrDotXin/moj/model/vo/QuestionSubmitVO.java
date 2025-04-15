package com.MrDotXin.moj.model.vo;

import java.io.Serializable;
import java.util.Date;

import org.springframework.beans.BeanUtils;

import com.MrDotXin.moj.model.dto.questionsubmit.JudgeInfo;
import com.MrDotXin.moj.model.entity.QuestionSubmit;

import cn.hutool.json.JSONUtil;
import lombok.Data;

@Data
public class QuestionSubmitVO implements Serializable {
    
    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String language;

    /**
     * 内容
     */
    private String code;

    /**
     * 判题信息
     */
    private JudgeInfo judgeInfo;

    /**
     * 判题状态
     */
    private String status;
    
    /**
     * 创建用户ID
     */
    private Long questionId;

    /**
    * 创建用户ID
    */
    private Long userId;

    /*
     * 创建时间
     */
    private Date createTime;

    /*
     * 更新时间
     */
    private Date updateTime;

    /**
     * 提交者信息
     */
    private UserVO userVO;

    /**
     * 提交的题目信息
     */
    private QuestionVO questionVO;

    /**
     * 包装类转对象
     *
     * @param questionSubmitVO
     * @return
     */
    public static QuestionSubmit voToObj(QuestionSubmitVO questionSubmitVO) {
        if (questionSubmitVO == null) {
            return null;
        }
        QuestionSubmit questionSubmit = new QuestionSubmit();
        BeanUtils.copyProperties(questionSubmitVO, questionSubmit);

        JudgeInfo judgeInfo = questionSubmitVO.getJudgeInfo();
        if (judgeInfo != null) {
            questionSubmit.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        }

        return questionSubmit;
    }

    /**
     * 对象转包装类
     *
     * @param question
     * @return
     */
    public static QuestionSubmitVO objToVo(QuestionSubmit question) {
        if (question == null) {
            return null;
        }
        QuestionSubmitVO questionSubmitVO = new QuestionSubmitVO();       
        BeanUtils.copyProperties(question, questionSubmitVO);
        String judgeInfo = question.getJudgeInfo();
        if (judgeInfo != null) {
            questionSubmitVO.setJudgeInfo(JSONUtil.toBean(judgeInfo, JudgeInfo.class));
        }

        return questionSubmitVO;
    }

    private static final long serialVersionUID = 1L;
}
