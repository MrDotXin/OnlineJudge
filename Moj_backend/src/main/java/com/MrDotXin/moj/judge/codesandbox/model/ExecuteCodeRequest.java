package com.MrDotXin.moj.judge.codesandbox.model;


import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder    
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCodeRequest {
    
    private String code;
    
    private String language;

    private List<String> inputList;
    
}
