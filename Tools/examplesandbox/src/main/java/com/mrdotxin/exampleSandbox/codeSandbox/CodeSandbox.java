package com.mrdotxin.exampleSandbox.codeSandbox;

import com.mrdotxin.exampleSandbox.codeSandbox.model.ExecuteCodeRequest;
import com.mrdotxin.exampleSandbox.codeSandbox.model.ExecuteCodeResponse;


public interface CodeSandbox {

    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);

}