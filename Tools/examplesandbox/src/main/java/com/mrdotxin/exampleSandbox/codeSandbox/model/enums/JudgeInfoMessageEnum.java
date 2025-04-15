
package com.mrdotxin.exampleSandbox.codeSandbox.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;

/**
 * 文件上传业务类型枚举
 *
 * 
 * 
 */
public enum JudgeInfoMessageEnum {

    ACCEPTED("Accepted", "提交通过"),

    WRONG_ANSWER("Wrong Answer", "答案错误"),

    COMPILER_ERROR("Compiler Error", "编译错误"),

    MEMORY_LIMIT_EXCCEED("Memory Limit Excceed", "超出内存限制"),

    TIME_LIMIT_EXCCEED("Time Limit Excceed", "超出时间限制"),

    PRESENTATION_ERROR("Presentation Error", "展示错误"),

    OUTPUT_LIMIT_EXCCEED("Output Limit Excceed", "输出溢出"),

    DANGEROUS_OPERATION("Dangerous Operation", "危险操作"),

    RUNTIME_ERROR("Runtime Error", "运行异常"),

    SYSTEM_ERROR("System Error", "系统错误"),

    WAITING("Waitting", "等待中");

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

