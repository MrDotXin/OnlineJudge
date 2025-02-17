package com.MrDotXin.moj.judge.codesandbox.model;


import java.util.List;

import com.MrDotXin.moj.model.dto.questionsubmit.JudgeInfo;

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
     */
    private Integer status;
    
    /**
     * 
     * 判题信息
     */
    private JudgeInfo judgeInfo;


    private long time;


    private long memory;
}
