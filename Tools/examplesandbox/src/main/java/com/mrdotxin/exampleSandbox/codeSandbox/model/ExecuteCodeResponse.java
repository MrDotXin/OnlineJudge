package com.mrdotxin.exampleSandbox.codeSandbox.model;


import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCodeResponse {

    /**
     * 
     * 代码的输出
     */
    private List<String> outputList;

    /**
     * 
     * 执行信息
     */
    private String message;

    /**
     * 
     * 执行状态
     * 0 没问题
     * 1 有问题
     */
    private Integer status;
    
    /*
     * 错误类型
     * 
     */
    private String status_type;

    /**
     * 
     * 判题信息
     */
    private JudgeInfo judgeInfo;


    private long time;


    private long memory;

}
