package com.mrdotxin.moj.backend.model.dto.questionsubmit;

import com.mrdotxin.moj.backend.common.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询请求
 *
 * 
 * 
 */
// 支持哈希方法且考虑父类的哈希
@EqualsAndHashCode(callSuper = true)
@Data           
// PageRquest : 支持分页查询
public class QuestionSubmitQueryRequest extends PageRequest implements Serializable {

    /**
     * 编程语言
     */
    private String language;

    /**
     * 用户代码
     */
    private String code;

    /**
     * 提交状态
     */
    private String status;

    /**
     * 题目id
     */
    private Long questionId;

    /**
     * 用户id
     */
    private Long userId;
    
    private static final long serialVersionUID = 1L;
}