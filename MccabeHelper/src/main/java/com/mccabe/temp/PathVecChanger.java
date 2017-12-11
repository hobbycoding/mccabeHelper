package com.mccabe.temp;

import com.mccabe.McCabeConfig;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class PathVecChanger extends McCabeConfig {

    public PathVecChanger(Properties properties) {
        super(properties);
    }

    public static void main(String[] args) throws Exception {
        PathVecChanger changer = new PathVecChanger(changeProperties(args));
        changer.start();
    }

    public void start() {
        try {
            String program = property.getProperty("programName");
            String outPath = property.getProperty("tracefile.outPath").endsWith(fs) ?
                    property.getProperty("tracefile.outPath") : property.getProperty("tracefile.outPath") + fs;
            String pathVecFilePath = property.getProperty("COMDIR") + fs + "com" + fs + "mccabe" + fs + "PathVec_" + program + "_" + property.getProperty("fileName") + ".java";
            log("[File Path] : " + pathVecFilePath);
            log("[Will Change OutPath] : " + outPath);
            readFile(pathVecFilePath, outPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readFile(String pathVecFilePath, String outPath) throws Exception {
        File file = new File(pathVecFilePath);
        if (!file.exists()) {
            log("file not found. try to find PathVec..");
            String path = property.getProperty("COMDIR") + fs + "com" + fs + "mccabe";
            File folder = new File(path);
            for (File element : folder.listFiles()) {
                if (element.getName().startsWith("PathVec_") && element.getName().endsWith(".java")) {
                    file = element;
                    log("Find! PathVec name is [" + file.getName() + "]");
                }
            }
        }
        if (OS.equals("windows"))
            outPath = outPath.replace("\\", "\\\\");
        String content = IOUtils.toString(new FileInputStream(file), "UTF-8");
        content = content.replace("%n_inst.out",outPath + "%n_inst.out");
        IOUtils.write(content, new FileOutputStream(file), "UTF-8");
    }
}
