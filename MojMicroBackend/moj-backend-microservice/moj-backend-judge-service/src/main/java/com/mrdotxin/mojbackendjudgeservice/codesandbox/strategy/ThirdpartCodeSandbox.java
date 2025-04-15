package com.mrdotxin.mojbackendjudgeservice.codesandbox.strategy;


import com.mrdotxin.moj.backend.model.codesandbox.ExecuteCodeRequest;
import com.mrdotxin.moj.backend.model.codesandbox.ExecuteCodeResponse;
import com.mrdotxin.mojbackendjudgeservice.codesandbox.CodeSandbox;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 第三方代码沙箱, 用来链接第三方代码沙箱
 */
@Slf4j
public class ThirdpartCodeSandbox implements CodeSandbox {

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        log.info("第三方代码沙箱启动!");
        return null;
    }
    
}