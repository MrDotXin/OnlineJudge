package com.mrdotxin.exampleSandbox.codeSandbox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mrdotxin.exampleSandbox.codeSandbox.Language.Java.Java18Sandbox;
import com.mrdotxin.exampleSandbox.codeSandbox.model.enums.QuestionSubmitLanguageEnum;

@Service
public class CodeSandboxFactory {
    
    @Autowired
    private Java18Sandbox java18Sandbox; // Java 1.8 版本

    public CodeSandbox sandboxInstance(String language) {
        
        if (language.equals(QuestionSubmitLanguageEnum.JAVA18.getValue())) {
            return java18Sandbox;
        }

        return null;
    }
}
