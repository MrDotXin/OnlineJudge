package com.mrdotxin.moj.backend.model.dto.question;

import lombok.Data;

/**
 * 
 * 题目配置
 */
@Data
public class JudgeConfig {

    /** 
     * 时间限制ms
     * 
     */ 
    private long timeLimit;         

    /**
     * 内存限制kb
     */
    private long memoryLimit;         
    
    /**
     * 堆栈限制kb
     */
    private long stackLimit;            
}
