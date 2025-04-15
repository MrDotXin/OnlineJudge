package com.mrdotxin.exampleSandbox;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.mrdotxin.exampleSandbox.codeSandbox.CodeSandboxTemplate;
import com.mrdotxin.exampleSandbox.codeSandbox.Language.Java.Impl.JavaDockerSandbox;
import com.mrdotxin.exampleSandbox.codeSandbox.model.ExecuteCodeRequest;
import com.mrdotxin.exampleSandbox.codeSandbox.model.ExecuteCodeResponse;

import cn.hutool.core.io.resource.ResourceUtil;

@SpringBootTest
class ExampleSandboxApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void runSandbox() {
        
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setInputList(Arrays.asList("1 2", "1 3"));

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
