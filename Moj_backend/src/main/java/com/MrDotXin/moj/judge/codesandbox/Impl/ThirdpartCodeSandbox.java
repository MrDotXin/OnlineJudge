package com.MrDotXin.moj.judge.codesandbox.Impl;

import com.MrDotXin.moj.judge.codesandbox.CodeSandbox;
import com.MrDotXin.moj.judge.codesandbox.model.ExecuteCodeRequest;
import com.MrDotXin.moj.judge.codesandbox.model.ExecuteCodeResponse;

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