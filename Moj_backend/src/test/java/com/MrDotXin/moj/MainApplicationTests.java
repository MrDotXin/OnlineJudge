package com.MrDotXin.moj;

import com.MrDotXin.moj.config.WxOpenConfig;
import com.MrDotXin.moj.judge.codesandbox.CodeSandbox;
import com.MrDotXin.moj.judge.codesandbox.CodeSandboxFactory;
import com.MrDotXin.moj.judge.codesandbox.model.ExecuteCodeRequest;
import com.MrDotXin.moj.judge.codesandbox.model.ExecuteCodeResponse;
import com.MrDotXin.moj.model.enums.QuestionSubmitLanguageEnum;

import java.util.Arrays;

import javax.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 主类测试
 *
 * 
 * 
 */
@SpringBootTest
class MainApplicationTests {

    @Resource
    private WxOpenConfig wxOpenConfig;

    @Test
    void contextLoads() {
        System.out.println(wxOpenConfig);
    }


    @Test
    void executeCode() {
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance("remote");
        String code = "import java.io.IOException;" + "import java.util.Scanner;" + "public class Main {"
        +"    public static void main(String[] args) {"
        +"        Scanner scanner = new Scanner(System.in);"
        +"        int a = scanner.nextInt();"
        +"        int b = scanner.nextInt();"
        +"        System.out.println((a + b));"
        +"        scanner.close();"
        +"    }"
        +"}"
        +"";

        ExecuteCodeRequest request = new ExecuteCodeRequest();
        request.setCode(code);
        request.setInputList(Arrays.asList("1 2", "3 4"));
        request.setAnswers(Arrays.asList("3", "7"));
        request.setMemoryLimit(1024 * 1024 * 1024l);
        request.setCodeCompilerMemoryLimit(1024 * 1024 * 256l);
        request.setTimeLimit(2000l);
        request.setLanguage(QuestionSubmitLanguageEnum.JAVA18.getValue());

        ExecuteCodeResponse response = null;
        try {
            response = codeSandbox.executeCode(request);
        } catch(Exception e) {
            e.printStackTrace();
        }

        System.out.println(response);
    }

}
