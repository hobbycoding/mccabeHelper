package com.mccabe.inst;

import com.mccabe.McCabeConfig;
import com.mccabe.temp.PathVecChanger;
import com.mccabe.util.FileUtil;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class McProcess extends McCabeConfig {

    public McProcess(Properties properties) {
        super(properties);
    }

    public void process() {
        JSONArray fileListJson = new JSONArray();;
        List<File> fileLists;
        try {
            property = setConfig();
            Instrument inst = new Instrument(property, log);
            fileLists = inst.gathering(property, "");
            File jsonFileList = new File(property.getProperty("projectDir") + fs + FILE_LIST_JSON);
            if (jsonFileList.exists())
                fileListJson = (JSONArray) new JSONParser().parse(new FileReader(jsonFileList));
            //TODO : shit code...but i don't really do anything
            if (SPLIT_FILE) {
                for (File file : fileLists) {
                    String srcDir = property.containsKey("tempDir") ? property.getProperty("tempDir") : property.getProperty("srcDir");
                    String fileName = FileUtil.getRoleFileName(file, srcDir);
                    String instDir = property.getProperty("projectDir") + fs + fileName;
                    new File(instDir).mkdirs();
                    property.setProperty("fileName", fileName);
                    property.setProperty("instDir", instDir);
                    property.setProperty("COMDIR", instDir + fs + fileName.split("_")[0]);
                    inst.pcfCreate(file);
                    property.setProperty("fileName", fileName);
                    inst.cliExport(property);
                    PathVecChanger changer = new PathVecChanger(property);
                    changer.start();
                    FileUtils.copyDirectory(new File(property.getProperty("COMDIR")), new File(property.getProperty("projectDir") + fs + fileName.split("_")[0]));
                    if (!fileListJson.contains(file.getAbsolutePath()))
                        fileListJson.add(file.getAbsolutePath());
                }
            } else {
                List<File> fileListAll = inst.gatheringAll(property, "");
                property.setProperty("fileName", property.getProperty("projectDir") + property.getProperty("fs") + property.getProperty("projectName"));
                inst.copySrcToInst(property, fileListAll);    // src 에서 java를 제외한 나머지를 inst에 복사 해 둠.
                inst.pcfCreate(property, fileLists);
                inst.cliExport(property);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log(e.getMessage());
        } finally {
            try {
                log("[INFO] start make fileList. : " + fileListJson.toJSONString());
                FileUtils.writeStringToFile(new File(property.getProperty("projectDir") + fs + FILE_LIST_JSON),
                        fileListJson.toJSONString(), StandardCharsets.UTF_8, false);
            } catch (Exception e) {
                log("[ERROR] can't make fileList.");
            }
            try {
                if (log != null) log.close();
            } catch (Exception ignore) {
            }
        }
    }

    public Properties setConfig() {
        if (property.getProperty("mcHome") == null) property.put("mcHome", MCCABE_HOME);        // 파라메타 받아야 함
        if (property.getProperty("mcBin") == null) property.put("mcBin", MCCABE_BIN);        // 파라메타 받아야 함

        if (property.getProperty("fs") == null) property.put("fs", fs);                        // 파라메타 받아야 함
        if (property.getProperty("projectName") == null) property.put("projectName", PROGRAM_NAME);            // 파라메타 받아야 함
        if (property.getProperty("scope") == null) property.put("scope", "all");                    // 파라메타 받아야 함

        if (OS == "windows")
            property.put("preCmd", "cmd /c ");
        else property.put("preCmd", "");

        if (property.getProperty("cliExport") == null)
            property.put("cliExport", property.getProperty("preCmd") + property.getProperty("mcBin") + property.getProperty("fs") + "cli export -pcf ");

        if (property.getProperty("javaVersion") == null) property.put("javaVersion", "1.6");    // 프로젝트 아래에 실제 소스가 있는 경로
        if (property.getProperty("srcAddPath") == null) property.put("srcAddPath", "src");    // 프로젝트 아래에 실제 소스가 있는 경로

        property.put("projectsDir", property.getProperty("mcHome") + property.getProperty("fs") + "projects");
        property.put("projectDir", property.getProperty("projectsDir") + property.getProperty("fs") + property.getProperty("projectName"));
        property.put("instDir", property.getProperty("projectDir"));
        if (property.getProperty("srcDir") == null) property.put("srcDir", MCCABE_HOME + fs + "build");

        if (property.getProperty("startFileName") == null) property.put("startFileName", "");
        if (property.getProperty("extendFileNames") == null) property.put("extendFileNames", ".java|.jsp");
        if (property.getProperty("exceptionFileNames") == null) property.put("exceptionFileNames", ".java|.svn-base|jsp");


        if ("".equals(property.getProperty("projectName")) || "".equals(property.getProperty("scope"))) {
            System.out.println("project or scope 값이 없습니다.");
            System.exit(0);
        }
        return property;
    }

    public static void main(String[] args) throws Exception {
        Properties properties = changeProperties(args);
        McProcess ps = new McProcess(properties);
        ps.process();
    }
}