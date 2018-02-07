package com.mccabe.inst;

import com.mccabe.Mccabe;
import com.mccabe.util.FileUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.mccabe.Mccabe.McCABE_PATH.*;
import static com.mccabe.Mccabe.McCABE_Properties.programName;
import static com.mccabe.Mccabe.McCABE_Properties.fileType;

public class Instrument extends Mccabe {
    public static void main(String[] args) throws Exception {
        checkAndSetProperties(args);
        Instrument instrument = new Instrument();
        instrument.start();
    }

    private void start() {
        try {
            Collection<File> fileList = getFileList();
            instrument(fileList);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void instrument(Collection<File> fileList) throws Exception {
        if (spliteFileInProject) {
            for(File file : fileList) {
                createPCFFile(file);
            }
        }
    }

    private void createPCFFile(File file) throws Exception {
        String roleFileName = FileUtil.getRoleFileName(file, SRC_DIR.getPath());
        Path filePath = Paths.get(PROJECT_PROGRAM_DIR.getPath() + fs + roleFileName + ".pcf");
        List<String> lines = new ArrayList<>();
        PCF.PROGRAM.setValue(programName.getString() + "_" + roleFileName);
        PCF.INSTDIR.setValue(INSTRUMENTED_SRC_DIR.getPath());
        PCF.INSTOUT.setValue(TRACEFILE_HOME.getPath() + fs + programName.getString() + fs + roleFileName + "_inst.out");
        PCF.COMDIR.setValue(PROJECT_PROGRAM_DIR.getPath());
        PCF.DIR.setValue(SRC_DIR.getPath());
        PCF.export(lines, file.getAbsolutePath());
        Files.write(filePath, lines, Charset.forName("UTF-8"));
        logger.debug("create file : " + filePath.toString());
    }

    private Collection<File> getFileList() throws Exception {
        File fileListPath = new File(SRC_DIR.getPath());
        if (!fileListPath.exists())
            throw new Exception("source directory does not exist.");
        return FileUtils.listFiles(fileListPath, fileType.getArray(), true);
    }

    private void runCommand(String cmd, File workingDir) throws Exception {
        Runtime rt = Runtime.getRuntime();
        try {
            cmd = cmd + " MC_WRITE_LOG=1";
            Process child = rt.exec(cmd, null, workingDir);
            child.waitFor();
        } catch (IOException e1) {
            throw new Exception("Error running CLI: " + e1.getMessage());
        } catch (InterruptedException e2) {
            throw new Exception("Error running CLI: " + e2.getMessage());
        }
    }
}
