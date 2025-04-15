package com.mrdotxin.moj.backend.model.codesandbox;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder    
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCodeRequest {
    
    private String code;
    
    private String language;

    private List<String> inputList;

    private List<String> answers; 

    private Long memoryLimit;

    private Long timeLimit;

    private Long codeCompilerMemoryLimit;
}
