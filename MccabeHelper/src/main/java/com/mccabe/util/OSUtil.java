package com.mccabe.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class OSUtil {

    public static void executeCommand(String command) throws IOException {
        System.out.println(command);
        BufferedReader br;
        String line;
        Process proc = Runtime.getRuntime().exec(command);
        br = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
        br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
        try {
            proc.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        proc.destroy();
    }
}
