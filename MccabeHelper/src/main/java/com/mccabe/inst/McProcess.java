package com.mccabe.inst;

import com.mccabe.McCabeConfig;
import com.mccabe.temp.PathVecChanger;
import com.mccabe.temp.WLog;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class McProcess extends McCabeConfig {
    public Properties prop = null;

    public McProcess(Properties properties) {
        this.prop = properties;
    }

    public void process() {
        try {
            prop = setConfig();
            Instrument inst = new Instrument(prop, log);
            List<File> fileList = inst.gathering(prop, "");
            //TODO : shit code...but i don't really do anything
            if (SPLIT_FILE) {
                HashSet<String> fileNameList = new HashSet<>();
                for (File file : fileList) {
                    String fileName = file.getAbsolutePath().replace(prop.getProperty("srcDir") + fs, "").replace(fs, "_").replace(".java", "");
                    String instDir = prop.getProperty("projectDir") + fs + fileName;
                    new File(instDir).mkdirs();
                    prop.setProperty("fileName", fileName);
                    prop.setProperty("instDir", instDir);
                    prop.setProperty("COMDIR", instDir + fs + fileName.split("_")[0]);
                    inst.pcfCreate(prop, file);
                    prop.setProperty("fileName", fileName);
                    inst.cliExport(prop);
                    PathVecChanger changer = new PathVecChanger(prop);
                    changer.start();
                    FileUtils.copyDirectory(new File(prop.getProperty("COMDIR")), new File(prop.getProperty("projectDir") + fs + fileName.split("_")[0]));
                    fileNameList.add(fileName);
                }
                makeFileList(fileNameList);
            } else {
                List<File> fileListAll = inst.gatheringAll(prop, "");
                prop.setProperty("fileName", prop.getProperty("projectDir") + prop.getProperty("fs") + prop.getProperty("projectName"));
                inst.copySrcToInst(prop, fileListAll);    // src 에서 java를 제외한 나머지를 inst에 복사 해 둠.
                inst.pcfCreate(prop, fileList);
                inst.cliExport(prop);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (log != null) log.close();
            } catch (Exception e) {
            }
        }

    }

    private void makeFileList(HashSet<String> nameList) throws Exception {
        File fileList = new File(prop.getProperty("projectDir") + fs + "fileList.json");
        JSONArray jsonArray;
        if (fileList.exists()) {
            JSONParser parser = new JSONParser();
            jsonArray = (JSONArray) parser.parse(new FileReader(fileList));
        } else {
            jsonArray = new JSONArray();
        }
        nameList.addAll(jsonArray);
        FileWriter writer = new FileWriter(fileList, false);
        JSONArray result = new JSONArray();
        result.addAll(nameList);
        result.writeJSONString(writer);
        writer.close();
     }

    public Properties setConfig() {
        if (prop.getProperty("mcHome") == null) prop.put("mcHome", MCCABE_HOME);        // 파라메타 받아야 함
        if (prop.getProperty("mcBin") == null) prop.put("mcBin", MCCABE_BIN);        // 파라메타 받아야 함

        if (prop.getProperty("fs") == null) prop.put("fs", fs);                        // 파라메타 받아야 함
        if (prop.getProperty("projectName") == null) prop.put("projectName", PROGRAM_NAME);            // 파라메타 받아야 함
        if (prop.getProperty("scope") == null) prop.put("scope", "all");                    // 파라메타 받아야 함

        if (OS == "windows")
            prop.put("preCmd", "cmd /c ");
        else prop.put("preCmd", "");

        if (prop.getProperty("cliExport") == null)
            prop.put("cliExport", prop.getProperty("preCmd") + prop.getProperty("mcBin") + prop.getProperty("fs") + "cli export -pcf ");

        if (prop.getProperty("javaVersion") == null) prop.put("javaVersion", "1.6");    // 프로젝트 아래에 실제 소스가 있는 경로
        if (prop.getProperty("srcAddPath") == null) prop.put("srcAddPath", "src");    // 프로젝트 아래에 실제 소스가 있는 경로

        prop.put("projectsDir", prop.getProperty("mcHome") + prop.getProperty("fs") + "projects");
        prop.put("projectDir", prop.getProperty("projectsDir") + prop.getProperty("fs") + prop.getProperty("projectName"));
        prop.put("instDir", prop.getProperty("projectDir"));
        prop.put("srcDir", MCCABE_HOME + fs + "build");

        if (prop.getProperty("startFileName") == null) prop.put("startFileName", "");
        if (prop.getProperty("extendFileNames") == null) prop.put("extendFileNames", ".java|.jsp");
        if (prop.getProperty("exceptionFileNames") == null) prop.put("exceptionFileNames", ".java|.svn-base|jsp");


        if ("".equals(prop.getProperty("projectName")) || "".equals(prop.getProperty("scope"))) {
            System.out.println("project or scope 값이 없습니다.");
            System.exit(0);
        }
        return prop;
    }

    public static void main(String args[]) throws Exception {
        Properties properties = changeProperties(args);
        McProcess ps = new McProcess(properties);
        ps.process();
    }

}