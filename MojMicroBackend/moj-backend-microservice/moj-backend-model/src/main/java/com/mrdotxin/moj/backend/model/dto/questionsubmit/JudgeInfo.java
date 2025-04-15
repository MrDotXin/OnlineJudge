package com.mrdotxin.moj.backend.model.dto.questionsubmit;

import lombok.Data;

@Data
public class JudgeInfo {
    
    /**
     * 执行信息
     */
    private String message;

    /**
     * 执行时间ms 
     */
    private long time; 

    /**
     * 执行内存kb
     */
    private long memory; 
}
