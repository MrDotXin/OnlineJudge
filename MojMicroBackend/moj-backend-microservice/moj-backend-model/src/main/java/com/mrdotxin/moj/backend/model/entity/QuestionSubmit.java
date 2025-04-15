package com.mrdotxin.moj.backend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@TableName("question_submit")
@Data
public class QuestionSubmit {
    
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)

    private Long id;

    /**
     * 编程语言
     */
    private String language;

    /**
     * 用户代码
     */
    private String code;

    /**
     * 判题状态
     */
    private String status;

    /**
     * 题目id
     */
    private Long questionId;

    /**
     * 判题信息
     */
    private String judgeInfo;

    /**
     * 创建用户Id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
