package com.mccabe.util;

import com.mccabe.McCabeConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class OSUtil {

    public static void executeCommand(String command) throws IOException {
        McCabeConfig.log("[ExecuteCommand] " + command);
        BufferedReader br;
        String line;
        Process proc = Runtime.getRuntime().exec(command);
        br = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
        while ((line = br.readLine()) != null) {
            McCabeConfig.log("[Error] " + line);
        }
        br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        while ((line = br.readLine()) != null) {
            McCabeConfig.log("[Error] " + line);
        }
        try {
            proc.waitFor();
        } catch (InterruptedException e) {
            McCabeConfig.log("[Error] " + e.getMessage());
            e.printStackTrace();
        }
        proc.destroy();
    }
}
