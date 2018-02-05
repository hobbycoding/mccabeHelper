package com.mccabe.inst;

import com.mccabe.Mccabe;

import java.io.File;
import java.util.List;

import static com.mccabe.Mccabe.Mccabe_PATH.PROJECT_PROGRAM_DIR;

public class Instrument extends Mccabe {
    public static void main(String[] args) throws Exception {
        checkAndSetProperties(args);
        Instrument instrument = new Instrument();
        instrument.start();
    }

    private void start() {
        List<File> fileList = getFileList();
    }

    private List<File> getFileList() {
        String fileListPath = PROJECT_PROGRAM_DIR.getPath();
//        FileUtils.listFiles(fileListPath,)
        return null;
    }
}
