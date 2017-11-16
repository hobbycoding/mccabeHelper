package com.mccabe.temp;

import com.mccabe.McCabeConfig;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class PathVecChanger extends McCabeConfig {
    private final Properties properties;

    public PathVecChanger(Properties properties) {
        this.properties = properties;
    }

    public static void main(String[] args) throws Exception {
        PathVecChanger changer = new PathVecChanger(changeProperties(args));
        changer.start();
    }

    public void start() {
        try {
            String program = properties.getProperty("programName");
            String outPath = properties.getProperty("tracefile.outPath").endsWith(fs) ?
                    properties.getProperty("tracefile.outPath") : properties.getProperty("tracefile.outPath") + fs;
            String pathVecFilePath = properties.getProperty("COMDIR") + fs + "com" + fs + "mccabe" + fs + "PathVec_" + program + "_" + properties.getProperty("fileName")+ ".java";
            log("[Path] : " + pathVecFilePath);
            log("[OutPath] : " + outPath);
            readFile(pathVecFilePath, outPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readFile(String pathVecFilePath, String outPath) throws Exception {
        File file = new File(pathVecFilePath);
        if (!file.exists()) {
            throw new Exception("file not found. " + pathVecFilePath);
        }
        if (OS.equals("windows"))
            outPath = outPath.replace("\\", "\\\\");
        String content = IOUtils.toString(new FileInputStream(file), "UTF-8");
        content = content.replace("%n_inst.out",outPath + "%n_inst.out");
        IOUtils.write(content, new FileOutputStream(file), "UTF-8");
    }
}
