package com.mrdotxin.exampleSandbox.codeSandbox.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExecuteCommandInfo {
    private Long timeout;

    private Long code;

    private String output;
}
