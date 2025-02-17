package com.MrDotXin.moj.judge.codesandbox;

import com.MrDotXin.moj.judge.codesandbox.Impl.ExampleCodeSandbox;
import com.MrDotXin.moj.judge.codesandbox.Impl.RemoteCodeSandbox;
import com.MrDotXin.moj.judge.codesandbox.Impl.ThirdpartCodeSandbox;

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
            default :
                return new ExampleCodeSandbox();
       } 
    }
}
