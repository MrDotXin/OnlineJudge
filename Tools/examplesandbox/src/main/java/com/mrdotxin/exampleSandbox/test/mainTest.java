package com.mrdotxin.exampleSandbox.test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import com.mrdotxin.exampleSandbox.codeSandbox.CodeSandboxTemplate;
import com.mrdotxin.exampleSandbox.codeSandbox.Language.Java.Impl.JavaDockerSandbox;
import com.mrdotxin.exampleSandbox.codeSandbox.model.ExecuteCodeRequest;
import com.mrdotxin.exampleSandbox.codeSandbox.model.ExecuteCodeResponse;

import cn.hutool.core.io.resource.ResourceUtil;

public class mainTest {
    
    public static void main(String[] args) {
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setInputList(Arrays.asList("1 2", "1 3"));
        executeCodeRequest.setAnswers(Arrays.asList("3", "4"));
        
        String code = ResourceUtil.readStr("testCode\\APlusB\\Main.java", StandardCharsets.UTF_8);
        System.out.println(code);

        executeCodeRequest.setCode(code);
        executeCodeRequest.setLanguage("java");
		executeCodeRequest.setMemoryLimit(1024 * 1024 * 256l);
		executeCodeRequest.setTimeLimit(2000l);
		CodeSandboxTemplate sandbox = new JavaDockerSandbox();

		ExecuteCodeResponse response = sandbox.executeCode(executeCodeRequest);
		
		System.out.println(response);
    }
}
