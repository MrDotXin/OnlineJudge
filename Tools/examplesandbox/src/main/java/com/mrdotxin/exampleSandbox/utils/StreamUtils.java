package com.mrdotxin.exampleSandbox.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class StreamUtils {
    
    public static String getOutputFromInputStream(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String blockinput;
        StringBuffer stringBuffer = new StringBuffer();

        try {
            while ((blockinput = bufferedReader.readLine()) != null) {
                stringBuffer.append(blockinput);
            }
            bufferedReader.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        
        return stringBuffer.toString();
    }


    public static void writeString(OutputStream outputStream, String str) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            outputStreamWriter.write(str);
            outputStreamWriter.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    } 
}
