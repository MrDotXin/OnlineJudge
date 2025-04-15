package com.mrdotxin.mojbackendjudgeservice.codesandbox;


import com.mrdotxin.moj.backend.model.codesandbox.ExecuteCodeRequest;
import com.mrdotxin.moj.backend.model.codesandbox.ExecuteCodeResponse;

public interface CodeSandbox {

    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);

}