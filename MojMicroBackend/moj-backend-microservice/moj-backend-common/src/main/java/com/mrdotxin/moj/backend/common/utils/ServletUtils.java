package com.mrdotxin.moj.backend.common.utils;

import com.mrdotxin.moj.backend.common.common.ErrorCode;
import com.mrdotxin.moj.backend.common.exception.BusinessException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class ServletUtils {
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) { 
            new BusinessException(ErrorCode.FORBIDDEN_ERROR, "会话错误, 请重试");
        } else {
            return attributes.getRequest();
        }

        return null;
    }   
}
