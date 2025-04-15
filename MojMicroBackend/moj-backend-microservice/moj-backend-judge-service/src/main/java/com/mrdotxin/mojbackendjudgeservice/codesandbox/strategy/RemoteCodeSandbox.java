package com.mrdotxin.mojbackendjudgeservice.codesandbox.strategy;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.mrdotxin.moj.backend.common.common.ErrorCode;
import com.mrdotxin.moj.backend.common.exception.BusinessException;
import com.mrdotxin.moj.backend.model.codesandbox.ExecuteCodeRequest;
import com.mrdotxin.moj.backend.model.codesandbox.ExecuteCodeResponse;
import com.mrdotxin.mojbackendjudgeservice.codesandbox.CodeSandbox;
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