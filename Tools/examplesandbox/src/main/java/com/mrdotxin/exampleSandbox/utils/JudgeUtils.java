package com.mrdotxin.exampleSandbox.utils;

public class JudgeUtils {
    
    public static boolean isTimeLimitExceeded(Long time, Long limit) {
        return (time / (limit * 1.)) >= 1.1;
    }
}
