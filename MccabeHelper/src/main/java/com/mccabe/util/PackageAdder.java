package com.mccabe.util;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.mccabe.McCabeConfig;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Properties;

public class PackageAdder extends McCabeConfig {
    private HashMap<String, String> packageNames = new HashMap<>();

    public PackageAdder(Properties ps) {
        super(ps);
    }

    public static void main(String[] args) throws Exception {
        PackageAdder main = new PackageAdder(changeProperties(args));
        main.start();
    }

    private void start() {
        try {
            makePackageList();
            getReportFileAndChange();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void makePackageList() throws Exception {
        File packageDir = new File(property.getProperty("srcDir"));
        if (!packageDir.isDirectory()) {
            throw new Exception(property.getProperty("srcDir") + " is not a directory. please set dir.");
        }
        searchFile(packageDir);
        log("find packages " + packageNames);
    }

    private void searchFile(File parent) throws IOException, ParseException {
        for (File children : parent.listFiles()) {
            if (children.isDirectory()) {
                searchFile(children);
            } else if (FilenameUtils.getExtension(children.getName()).equals("java")) {
                CompilationUnit cu = JavaParser.parse(children);
                String names = cu.getPackage() != null ? cu.getPackage().getPackageName() : "";
                packageNames.put(FilenameUtils.removeExtension(children.getName()), names);
            }
        }
    }

    private void getReportFileAndChange() throws Exception {
        String programName = property.getProperty("programName");
        String report_branch = REPORT_DIR + fs + programName + fs +  programName + "_branch.csv";
        String report_codecov = REPORT_DIR + fs + programName + fs + programName + "_codecov.csv";
        changeReportValue(report_branch);
        changeReportValue(report_codecov);
        if (property.containsKey("subjobs") && property.getProperty("subjobs").length() > 2) {
            String raw = property.getProperty("subjobs").substring(property.getProperty("subjobs").indexOf("[") + 1, property.getProperty("subjobs").lastIndexOf("]"));
            log("[subJob property " + raw + "]");
            for (String subjob : raw.split(",")) {
                subjob = subjob.trim();
                log("[SubJob Found. " + subjob + "]");
                changeReportValue(REPORT_DIR + fs + programName + fs + programName + "_" + subjob.replace(fs, "_") + "_branch.csv");
                changeReportValue(REPORT_DIR + fs + programName + fs + programName + "_" + subjob.replace(fs, "_") + "_codecov.csv");
            }
        }
    }

    private void changeReportValue(String paht) throws Exception {
        log("[" + paht + "] Read.");
        File report = new File(paht);
        if (!report.exists())
            throw new Exception("[" + paht + "] Read fail.");

        String content = "";
        BufferedReader reader = null;
        FileWriter writer = null;
        try {
            reader = new BufferedReader(new FileReader(report));
            String line = reader.readLine();
            while (line != null) {
                if ( StringUtils.countMatches(line, ",") >= 3 && line.contains(".")) {
                    String className = getClassName(line);
                    if (packageNames.containsKey(className) && packageNames.get(className).length() > 0) {
                        line = line.replaceFirst(className, packageNames.get(className) + "." + className);
                    }
                }
                content = content + line + System.lineSeparator();
                line = reader.readLine();
            }
            writer = new FileWriter(report);
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null)
                    reader.close();
                if (writer != null)
                    writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getClassName(String line) {
        String normal = line.substring(0, line.indexOf("."));
        String change = normal.replaceAll("\"","");
        return change;
    }
}
