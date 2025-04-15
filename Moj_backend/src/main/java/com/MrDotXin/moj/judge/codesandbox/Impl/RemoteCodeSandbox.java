package com.MrDotXin.moj.judge.codesandbox.Impl;

import com.MrDotXin.moj.common.ErrorCode;
import com.MrDotXin.moj.exception.BusinessException;
import com.MrDotXin.moj.judge.codesandbox.CodeSandbox;
import com.MrDotXin.moj.judge.codesandbox.model.ExecuteCodeRequest;
import com.MrDotXin.moj.judge.codesandbox.model.ExecuteCodeResponse;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 代码沙箱
 */
@Slf4j
public class RemoteCodeSandbox implements CodeSandbox {
    private final String AUTH_REQUEST_HEADER = "auth_request_header";
    private final String AUTH_REQUEST_SECRET = "MrDor_Xin";

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        String Url = "http://localhost:8106/api/sandbox/doJudge";
        String json = JSONUtil.toJsonStr(executeCodeRequest);

        String response = HttpUtil.createPost(Url)
                        .header(AUTH_REQUEST_HEADER, AUTH_REQUEST_SECRET)
                        .body(json)
                        .execute()
                        .body();
        
        if (StringUtils.isBlank(response)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Execute error");
        }

        return JSONUtil.toBean(response, ExecuteCodeResponse.class);
    }
    
}