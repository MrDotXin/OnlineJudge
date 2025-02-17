package com.MrDotXin.moj.judge.codesandbox;

import com.MrDotXin.moj.judge.codesandbox.model.ExecuteCodeRequest;
import com.MrDotXin.moj.judge.codesandbox.model.ExecuteCodeResponse;


public interface CodeSandbox {

    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
     
}