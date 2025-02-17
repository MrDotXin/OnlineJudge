package com.MrDotXin.moj.model.entity;

import java.io.Serializable;
import java.sql.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@TableName("question")
@Data
public class Question implements Serializable{
    
    @TableId(type = IdType.ASSIGN_ID)
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
    private String tags;
    
    /**
     * 题解 
     */
    private String answer;

    /**
     * 判题用例 
     */
    private String judgeCase;

    /**
     * 判题配置 
     */
    private String judgeConfig;

    /**
     * 提交数
     */
    private Integer submitNum;

    /**
     * 通过数
     */
    private Integer acceptNum;

    /**
     * 点赞数
     */
    private Integer thumbNum;

    /**
     *  收藏数
     */
    private Integer favourNum;

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

    /*
     * 是否删除
     */
    // @TableLogic yml 加过
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}   
