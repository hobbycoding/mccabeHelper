package com.mccabe.inst;

import com.mccabe.McCabeConfig;
import com.mccabe.temp.WLog;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.Character.UnicodeBlock;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class Instrument extends McCabeConfig {

    List fileList = null;
    List fileListAll = null;
    WLog log = null;
    String[] extendFileNames = null;
    String[] exceptionFileNames = null;

    public Instrument(Properties prop, WLog log) {
        super(null);
        extendFileNames = prop.getProperty("extendFileNames").split("[|]");
        exceptionFileNames = prop.getProperty("exceptionFileNames").split("[|]");
        this.log = log;
    }

    public List<File> gatheringAll(Properties prop, String pwd) {
        if (fileListAll == null) fileListAll = new ArrayList();
        try {
            String filePath = prop.getProperty("srcDir") + prop.getProperty("fs") + pwd;
            try {
                File fileDir = new File(filePath);
                if (fileDir.isDirectory()) {
                    fileListAll.add(fileDir);
                    for (File file : fileDir.listFiles()) {
                        gatheringAll(prop, pwd + prop.getProperty("fs") + file.getName());
                    }
                } else if (fileDir.isFile()) {
                    log.write("gatheringAll : " + pwd + prop.getProperty("fs") + fileDir.getName() + "  ");
                    fileListAll.add(fileDir);
                }
            } catch (Exception e) {
                log.write(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileListAll;
    }

    public List<File> gathering(Properties prop, String pwd) {
        if (fileList == null) fileList = new ArrayList();
        try {
            String filePath =  prop.containsKey("tempDir") ? prop.getProperty("tempDir") : prop.getProperty("srcDir") + prop.getProperty("fs") + pwd;
            try {
                File fileDir = new File(filePath);
                if (fileDir.isDirectory()) {
                    for (File file : fileDir.listFiles()) {
                        gathering(prop, pwd + prop.getProperty("fs") + file.getName());
                    }
                } else if (fileDir.isFile()) {
                    if (fileDir.getName().startsWith(prop.getProperty("startFileName"))) {
                        if (fileDir.getName().indexOf(" ") > -1) {

                        } else {
                            if (containsHangul(fileDir.getName())) {

                            } else {
                                for (String fileName : extendFileNames) {
                                    if (fileDir.getName().endsWith(fileName)) {
                                        log.write("gathering : " + pwd + prop.getProperty("fs") + fileDir.getName() + "  ");
                                        fileList.add(fileDir);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.write(e);
            }
        } catch (Exception e) {
            log.write(this.getClass().getName() + "\n" + e);
        }
        return fileList;
    }

    public List<File> copySrcToInst(Properties prop, List<File> fileList) {
        if (fileList == null) fileList = new ArrayList();
        try {
            FileReader fr;
            FileWriter fw;

            for (File file : fileList) {
                String absoutePath = file.getAbsolutePath();
                String packageName = absoutePath.substring(prop.getProperty("srcDir").length());
                if ("".equals(packageName)) continue;
                boolean flag = false;
                for (String fileName : exceptionFileNames) {
                    if (packageName.endsWith(fileName)) {
                        flag = true;
                        break;    // 예외 파일은 복사하지 않음. 예. .svn 파일
                    }
                }
                if (flag) continue;

                log.write("copySrcToInst : " + prop.getProperty("instDir") + packageName);
                if (file.isDirectory()) {
                    new File(prop.getProperty("instDir") + packageName).mkdirs();
                } else {
                    File dest = new File(prop.getProperty("instDir") + packageName);
                    fr = new FileReader(file);
                    fw = new FileWriter(dest);
                    int c;
                    while ((c = fr.read()) != -1) fw.write((char) c);
                    fr.close();
                    fw.close();
                }
            }
        } catch (Exception e) {
            log.write(this.getClass().getName() + "\n" + e);
        }
        return fileList;
    }

    public void pcfCreate(Properties prop, File file) throws Exception {
        String pcfFile = prop.getProperty("fileName") + ".pcf";
        FileWriter fw = null;
        String filePath = prop.getProperty("instDir") + fs + pcfFile;
        try {
            fw = new FileWriter(filePath, false);
            fw.append("PROGRAM " + prop.getProperty("projectName") + "_" + prop.getProperty("fileName"));
            fw.append(System.getProperty("line.separator") + "INSTDIR " + prop.getProperty("instDir"));
            fw.append(System.getProperty("line.separator") + "INSTOUT " + prop.getProperty("instDir") + prop.getProperty("fs") + "inst.out");
            fw.append(System.getProperty("line.separator") + "COMDIR " + prop.getProperty("COMDIR"));
            fw.append(System.getProperty("line.separator") + "METRICS_LEVEL 3");
            fw.append(System.getProperty("line.separator") + "EXPORTTREE");
            fw.append(System.getProperty("line.separator") + "SCOPEINST");
            fw.append(System.getProperty("line.separator") + "DIR " + prop.getProperty("srcDir"));
            fw.append(System.getProperty("line.separator") + "NO_COVERAGE_SERVER");

            String dir = prop.getProperty("srcDir");
            int dirLeng = dir.length();
            String cw_Java_inst_option = " -PATHVEC -CLASS -MODSIG -HALSTEAD -PARAM -DATA -NOCLASSMSGS -OVERLOAD -MODE JDK" + prop.getProperty("javaVersion");
            String absoutePath = file.getAbsolutePath();
            String packageFilePath = absoutePath.substring(dirLeng + 1);
            fw.append(System.getProperty("line.separator") + "cw_Java_inst " + packageFilePath + cw_Java_inst_option);
        } catch (Exception e) {
            log.write(this.getClass().getName() + "\n" + e);
        } finally {
            try {
                if (fw != null) fw.close();
            } catch (Exception e) {
            }
        }
    }

    public void pcfCreate(Properties prop, List<File> fileList) {
        String pcfFile = prop.getProperty("projectDir") + prop.getProperty("fs") + prop.getProperty("projectName") + ".pcf";
        FileWriter fw = null;
        try {
            fw = new FileWriter(pcfFile, false);
            fw.append("PROGRAM " + prop.getProperty("projectName"));
            fw.append(System.getProperty("line.separator") + "INSTDIR " + prop.getProperty("instDir"));
            fw.append(System.getProperty("line.separator") + "INSTOUT " + prop.getProperty("instDir") + prop.getProperty("fs") + "inst.out");
            fw.append(System.getProperty("line.separator") + "COMDIR " + prop.getProperty("instDir"));
            fw.append(System.getProperty("line.separator") + "METRICS_LEVEL 3");
            fw.append(System.getProperty("line.separator") + "EXPORTTREE");
            fw.append(System.getProperty("line.separator") + "SCOPEINST");
            fw.append(System.getProperty("line.separator") + "DIR " + prop.getProperty("srcDir"));
            fw.append(System.getProperty("line.separator") + "NO_COVERAGE_SERVER");

            String dir = prop.getProperty("srcDir");
            int dirLeng = dir.length();
            String cw_Java_inst_option = " -PATHVEC -CLASS -MODSIG -HALSTEAD -PARAM -DATA -NOCLASSMSGS -OVERLOAD -MODE JDK" + prop.getProperty("javaVersion");
            for (File file : fileList) {
                String absoutePath = file.getAbsolutePath();
                String packageFilePath = absoutePath.substring(dirLeng + 1);
                fw.append(System.getProperty("line.separator") + "cw_Java_inst " + packageFilePath + cw_Java_inst_option);
            }

        } catch (Exception e) {
            log.write(this.getClass().getName() + "\n" + e);
        } finally {
            try {
                if (fw != null) fw.close();
            } catch (Exception e) {
            }
        }
    }

    public void cliExport(Properties prop) {
        try {
            String pcfFile = prop.getProperty("instDir") + fs + prop.getProperty("fileName") + ".pcf";
            log.write(prop.getProperty("cliExport") + pcfFile);
            Process p = Runtime.getRuntime().exec(prop.getProperty("cliExport") + pcfFile);
            p.waitFor();
        } catch (Exception e) {
            log.write(this.getClass().getName() + "\n" + e);
        }
    }

    public boolean containsHangul(String str) {
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            UnicodeBlock unicodeBlock = UnicodeBlock.of(ch);
            if (UnicodeBlock.HANGUL_SYLLABLES.equals(unicodeBlock) || UnicodeBlock.HANGUL_COMPATIBILITY_JAMO.equals(unicodeBlock) || UnicodeBlock.HANGUL_JAMO.equals(unicodeBlock)) {
                return true;
            }
        }
        return false;
    }
}
