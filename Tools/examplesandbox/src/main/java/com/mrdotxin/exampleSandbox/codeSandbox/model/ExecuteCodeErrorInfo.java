package com.mrdotxin.exampleSandbox.codeSandbox.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExecuteCodeErrorInfo {

    private String input;

    private String output;

    private JudgeInfo judgeInfo;

    private String errorType;
}
