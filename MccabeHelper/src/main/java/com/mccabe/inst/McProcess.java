package com.mccabe.inst;

import com.mccabe.McCabeConfig;
import com.mccabe.temp.WLog;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class McProcess extends McCabeConfig {
    public Properties prop = null;

    public McProcess(Properties properties) {
        this.prop = properties;
    }

    public void process() {
        WLog log = new WLog(new File(MCCABE_HOME + fs + "mclog.log"));
        try {
            prop = setConfig();
            Instrument inst = new Instrument(prop, log);
            List<File> fileListAll = inst.gatheringAll(prop, "");
            inst.copySrcToInst(prop, fileListAll);    // src 에서 java를 제외한 나머지를 inst에 복사 해 둠.
            List<File> fileList = inst.gathering(prop, "");
            inst.pcfCreate(prop, fileList);
            inst.cliExport(prop);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (log != null) log.close();
            } catch (Exception e) {
            }
        }

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

    public void rmMkDir(Properties prop) {
        try {
            // 기존 copy Dir 삭제
            // 잘못하여 형상관리꺼 삭제 하지 않기 위해서.
//			if(prop.getProperty("srcDir").toLowerCase().indexOf("mccabe") > -1){
//				try{ Runtime.getRuntime().exec(prop.getProperty("rmDir") + prop.getProperty("srcDir"	) ); }catch(Exception e){System.out.println(e);}
//				try{ Runtime.getRuntime().exec(prop.getProperty("rmDir") + prop.getProperty("instDir"	) ); }catch(Exception e){System.out.println(e);}
//				try{ Runtime.getRuntime().exec(prop.getProperty("mkDir") + prop.getProperty("srcDir"	) ); }catch(Exception e){System.out.println(e);}
//				try{ Runtime.getRuntime().exec(prop.getProperty("mkDir") + prop.getProperty("instDir"	) ); }catch(Exception e){System.out.println(e);}
            deleteFolder(new File(prop.getProperty("projectDir")));
            new File(prop.getProperty("instDir")).mkdirs();

//			}
        } catch (Exception e) {
            System.out.println(this.getClass().getName() + "\n" + e);
        }
    }

    public void deleteFolder(File targetFolder) {
        File[] childFile = targetFolder.listFiles();
        int size = childFile.length;
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                if (childFile[i].isFile()) {
                    childFile[i].delete();
                    System.out.println(childFile[i] + " 삭제");
                } else {
                    deleteFolder(childFile[i]);
                }
            }
        }
        targetFolder.delete();
        System.out.println("deleteFolder " + targetFolder + " 삭제");
    }


    public static void main(String args[]) throws Exception {
        Properties properties = changeProperties(args);
        McProcess ps = new McProcess(properties);
        ps.process();
    }

}