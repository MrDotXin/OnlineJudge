package com.MrDotXin.moj.model.dto.question;

import com.MrDotXin.moj.common.PageRequest;

import java.io.Serializable;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询请求
 *
 * 
 * 
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;


    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表 
     */
    private List<String> tags;
    
    /**
     * 题解 
     */
    private String answer;

    /**
     * 创建用户ID
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}