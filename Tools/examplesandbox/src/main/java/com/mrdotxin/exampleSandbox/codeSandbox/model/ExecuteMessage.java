package com.mrdotxin.exampleSandbox.codeSandbox.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteMessage {
    
    private int exitValue;

    private String message;

    private String errorMessage;

    private Long executeTime;

    private Long executeMemory;
}
