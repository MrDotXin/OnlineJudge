package com.mrdotxin.mojbackendjudgeservice.codesandbox;


import com.mrdotxin.mojbackendjudgeservice.codesandbox.strategy.ExampleCodeSandbox;
import com.mrdotxin.mojbackendjudgeservice.codesandbox.strategy.RemoteCodeSandbox;
import com.mrdotxin.mojbackendjudgeservice.codesandbox.strategy.ThirdpartCodeSandbox;

/**
 * 简单工厂
 * 
 */
public class CodeSandboxFactory {
    
    public static CodeSandbox newInstance(String type) {
        switch (type) {
            case "example":
                return new ExampleCodeSandbox();
            case "remote" :
                return new RemoteCodeSandbox();
            case "thirdpart":
                return new ThirdpartCodeSandbox();
        }


        return new ExampleCodeSandbox();
    }
}
