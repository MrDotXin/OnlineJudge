package com.mrdotxin.exampleSandbox.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mrdotxin.exampleSandbox.codeSandbox.CodeSandbox;
import com.mrdotxin.exampleSandbox.codeSandbox.CodeSandboxFactory;
import com.mrdotxin.exampleSandbox.codeSandbox.model.ExecuteCodeRequest;
import com.mrdotxin.exampleSandbox.codeSandbox.model.ExecuteCodeResponse;
import com.mrdotxin.exampleSandbox.codeSandbox.model.enums.JudgeInfoMessageEnum;

@RestController
@RequestMapping("/api/sandbox")
public class SandboxController {
    private final String AUTH_REQUEST_HEADER = "auth_request_header";
    private final String AUTH_REQUEST_SECRET = "MrDor_Xin";

    @Autowired
    CodeSandboxFactory codeSandboxFactory;

    @PostMapping("/doJudge")
    public ExecuteCodeResponse JudgeCode(@RequestBody ExecuteCodeRequest executeCodeRequest, HttpServletRequest request) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();

        String authHeader = request.getHeader(AUTH_REQUEST_HEADER);
        if (!authHeader.equals(AUTH_REQUEST_SECRET)) {
            executeCodeResponse.setStatus(1);
            executeCodeResponse.setStatus_type(JudgeInfoMessageEnum.SYSTEM_ERROR.getText());
            executeCodeResponse.setMessage("非法访问!");
        
        } else {
            
            String language = executeCodeRequest.getLanguage();

            CodeSandbox codeSandbox = codeSandboxFactory.sandboxInstance(language);


            if (codeSandbox == null) {
                executeCodeResponse.setStatus(1);
                executeCodeResponse.setStatus_type(JudgeInfoMessageEnum.SYSTEM_ERROR.getText());
                executeCodeResponse.setMessage("当前语言不支持");
            } else {

                try {
                    executeCodeResponse = codeSandbox.executeCode(executeCodeRequest); 
                } catch(Exception e) {
                    e.printStackTrace();

                    executeCodeResponse.setStatus(1);
                    executeCodeResponse.setStatus_type(JudgeInfoMessageEnum.SYSTEM_ERROR.getText());
                    executeCodeResponse.setMessage("未知错误");
                }
            }
        }
        System.out.println(executeCodeResponse);
        return executeCodeResponse;
    }
}
