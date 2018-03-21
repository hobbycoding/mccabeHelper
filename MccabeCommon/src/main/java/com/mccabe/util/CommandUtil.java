package com.mccabe.util;

import com.mccabe.Mccabe;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.mccabe.Mccabe.McCABE_PATH.CLI;


public class CommandUtil extends Mccabe {
    public static void runCommand(String pcfPath, String workingDir) throws Exception {
        Runtime rt = Runtime.getRuntime();
        try {
            pcfPath = CLI.getPath() +  pcfPath + " MC_WRITE_LOG=1";
            Process child = rt.exec(pcfPath, null, new File(workingDir));
            child.waitFor();
        } catch (IOException e1) {
            throw new Exception("Error running CLI: " + e1.getMessage());
        } catch (InterruptedException e2) {
            throw new Exception("Error running CLI: " + e2.getMessage());
        }
    }

    public static void runNormalCommand(String command) throws Exception {
        logger.debug("[Command] [" + command + "]");
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
