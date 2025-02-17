package com.MrDotXin.moj.judge.codesandbox;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import com.MrDotXin.moj.judge.codesandbox.model.ExecuteCodeRequest;
import com.MrDotXin.moj.judge.codesandbox.model.ExecuteCodeResponse;

@SpringBootTest
public class CodeSandboxTest {

    @Value("${codesandbox.type:example}")
    private String type;

    @Test
    void executeCodeByValue() {
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        List<String> inputList = Arrays.asList("1 2", "3 4");
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
        .code("")
            .language("")
            .inputList(inputList)
            .build();
        
            ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
            Assertions.assertNotNull(executeCodeResponse);
    }   
    @Test
    void executeCodeByProxy() {
        CodeSandbox codeSandbox = new CodeSandboxProxy(CodeSandboxFactory.newInstance(type));
        List<String> inputList = Arrays.asList("1 2", "3 4");
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
        .code("")
            .language("")
            .inputList(inputList)
            .build();
        
            ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
            Assertions.assertNotNull(executeCodeResponse);
    }   
    
    @Test
    void executeCode() {
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance("example");
        List<String> inputList = Arrays.asList("1 2", "3 4");
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
        .code("")
            .language("")
            .inputList(inputList)
            .build();
        
            ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
            // Assertions.assertNotNull(executeCodeResponse);
    }   


    
}

