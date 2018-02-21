package com.mccabe.inst;

import com.mccabe.Mccabe;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import static com.mccabe.Mccabe.PCF.*;
import static com.mccabe.Mccabe.McCABE_Properties.*;

public class PathVecChanger extends Mccabe {

    public static void main(String[] args) throws Exception {
        checkAndSetProperties(args);
        PathVecChanger changer = new PathVecChanger();
        changer.start();
    }

    public void start() {
            try {
            String pathVecFile = PCF.getAbsolutePathForPathVec();
            logger.debug("[File Path] : " + pathVecFile);
            logger.debug("[Will Change OutPath] : " + traceFileOutPath.getString());
            readFile(pathVecFile, traceFileOutPath.getString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readFile(String pathVecFilePath, String outPath) throws Exception {
        if (outPath == null || outPath.length() <= 0)
            return;
        File file = new File(pathVecFilePath);
        if (!file.exists()) {
            logger.debug("file not found. try to find PathVec..");
            String path = COMDIR.getFilePath() + fs + "com" + fs + "mccabe";
            File folder = new File(path);
            for (File element : folder.listFiles()) {
                if (element.getName().startsWith("PathVec_") && element.getName().endsWith(".java")) {
                    file = element;
                    logger.debug("Find! PathVec name is [" + file.getName() + "]");
                }
            }
        }
        if (isWindows.getBoolean())
            outPath = outPath.replace("\\", "\\\\");
        String content = IOUtils.toString(new FileInputStream(file), "UTF-8");
        content = content.replace("%n_inst.out",outPath + "%n_inst.out");
        IOUtils.write(content, new FileOutputStream(file), "UTF-8");
    }
}
