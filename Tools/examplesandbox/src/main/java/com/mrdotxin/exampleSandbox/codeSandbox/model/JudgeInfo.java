package com.mrdotxin.exampleSandbox.codeSandbox.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
