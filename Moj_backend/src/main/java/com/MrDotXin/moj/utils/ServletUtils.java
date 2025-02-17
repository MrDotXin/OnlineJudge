package com.MrDotXin.moj.utils;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.MrDotXin.moj.common.ErrorCode;
import com.MrDotXin.moj.exception.BusinessException;

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
