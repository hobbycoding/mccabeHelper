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

import static com.mccabe.Mccabe.Mccabe_PATH.PROJECT_PROGRAM_DIR;
import static com.mccabe.Mccabe.Mccabe_PATH.SRC_DIR;

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

    private void instrument(Collection<File> fileList) throws IOException {
        if (spliteFileInProject) {
            for(File file : fileList) {
                createPCFFile(file);
            }
        }
    }

    private void createPCFFile(File file) throws IOException {
        Path pcf = Paths.get(FileUtil.getRoleFileName(file, SRC_DIR.getPath()));
        List<String> lines = new ArrayList<>();

        Files.write(pcf, lines, Charset.forName("UTF-8"));
    }

    private Collection<File> getFileList() throws Exception {
        File fileListPath = new File(PROJECT_PROGRAM_DIR.getPath());
        if (!fileListPath.isDirectory())
            throw new Exception(fileListPath.getPath() + " is not a directory.");
        return FileUtils.listFiles(fileListPath, fileType, true);
    }
}
