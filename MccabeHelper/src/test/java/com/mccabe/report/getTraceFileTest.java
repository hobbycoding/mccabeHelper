package com.mccabe.report;

import com.mccabe.util.FileUtil;
import com.mccabe.vo.PCF;

import java.io.File;
import java.util.ArrayList;

import static com.mccabe.util.FileUtil.getFilesRecursive;

public class getTraceFileTest {
    public static void main(String[] args) throws Exception {
//        pcf.setAccumulateTraceFiles(FileUtil.getFilesRecursive(new File(TRACEFILE_HOME + fs + property.getProperty("programName")), "", pcf.getProjectName(), ".out", 0));
        ArrayList<File> files = getFilesRecursive(new File("D:\\testspace\\mccabe\\test\\tracefiles\\test"), "", "test", ".out", 0);
        for (File file : files)
            System.out.println(file.getAbsoluteFile());
    }
}
