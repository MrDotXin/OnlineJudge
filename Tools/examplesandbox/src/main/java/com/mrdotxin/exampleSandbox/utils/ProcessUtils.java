package com.mrdotxin.exampleSandbox.utils;

import java.io.IOException;

import org.springframework.util.StopWatch;

import com.mrdotxin.exampleSandbox.codeSandbox.model.ExecuteMessage;

public class ProcessUtils {

    /*
     * @returns process output
     *   
     */
    public static ExecuteMessage runProcess(Process process) {

        ExecuteMessage executeMessage = new ExecuteMessage();
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            
            executeMessage.setExitValue(process.waitFor());
            
            stopWatch.stop();
            executeMessage.setExecuteTime(stopWatch.getTotalTimeMillis());

            if (executeMessage.getExitValue() == 0) {
                String output = StreamUtils.getOutputFromInputStream(process.getInputStream());
                executeMessage.setMessage(output);
            } else {
                String output = StreamUtils.getOutputFromInputStream(process.getErrorStream());
                executeMessage.setErrorMessage(output);
            }

        } catch(InterruptedException | RuntimeException exception) {
            exception.printStackTrace();
        }

        return executeMessage;
    }


    public static Process buildCommandProcess(String cmdLine) {
        ProcessBuilder processBuilder = new ProcessBuilder(cmdLine.split(" "));
        Process process = null;
        try {
            process = processBuilder.start();
        } catch (IOException error) {
            error.printStackTrace();
        }

        return process;
    }
}
