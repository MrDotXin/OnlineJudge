
package com.mrdotxin.moj.backend.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件上传业务类型枚举
 *
 * 
 * 
 */
public enum JudgeInfoMessageEnum {

    ACCEPTED("提交通过", "Accepted"),

    WRONG_ANSWER("答案错误", "Wrong Answer"),
    
    COMPILER_ERROR("编译错误", "Compiler Error"),
    
    MEMORY_LIMIT_EXCCEED("超出内存限制", "Memory Limit Excceed"),
    
    TIME_LIMIT_EXCCEED("超出时间限制", "Time Limit Excceed"),
    
    PRESENTATION_ERROR("展示错误", "Presentation Error"),
    
    OUTPUT_LIMIT_EXCCEED("输出溢出", "Output Limit Excceed"),
    
    DANGEROUS_OPERATION("危险操作", "Dangerous Operation"),
    
    RUNTIME_ERROR("运行异常", "Runtime Error"),
    
    SYSTEM_ERROR("系统错误", "System Error"),
    
    WAITING("等待中", "Waitting"),
    
    RUNNING("运行中", "Running");

    private final String text;

    private final String value;

    JudgeInfoMessageEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static JudgeInfoMessageEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (JudgeInfoMessageEnum anEnum : JudgeInfoMessageEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}    

