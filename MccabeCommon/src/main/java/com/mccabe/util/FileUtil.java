package com.mccabe.util;

import com.mccabe.Mccabe;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static com.mccabe.Mccabe.McCABE_PATH.INSTRUMENTED_SRC_DIR;
import static com.mccabe.Mccabe.McCABE_PATH.SRC_DIR;
import static com.mccabe.Mccabe.McCABE_Properties.exceptionFileNames;


public class FileUtil extends Mccabe {

    public static String getRoleFileName(File file, String path) {
        return file.getAbsolutePath().replace(path + fs, "").replace(fs, "_").replace(".java", "");
    }

    public static String getBackRoleFileName(String filePath, String subPath) {
        String fileName = null;
        for (String e : subPath.split("_")) {
            if (Files.exists(Paths.get(filePath + fs + e))) {
                filePath += (fs + e);
            } else {
                if (fileName == null)
                    fileName = fs + e;
                else fileName += "_" + e;
            }
        }
        return filePath + fileName + ".java";
    }

    public static void write_UTF_8(File file) throws IOException {
        String content = FileUtils.readFileToString(file, "ISO8859_1");
        FileUtils.write(file, content, "UTF-8");
    }

    public List<File> copySrcToInst(Properties prop, List<File> fileList) {
        if (fileList == null) fileList = new ArrayList();
        try {
            FileReader fr;
            FileWriter fw;

            for (File file : fileList) {
                String absoutePath = file.getAbsolutePath();
                String packageName = absoutePath.substring(SRC_DIR.getPath().length());
                if ("".equals(packageName)) continue;
                boolean flag = false;
                for (String fileName : exceptionFileNames.getArray()) {
                    if (packageName.endsWith(fileName)) {
                        flag = true;
                        break;
                    }
                }
                if (flag) continue;

                logger.debug("copySrcToInst : " + INSTRUMENTED_SRC_DIR.getPath() + packageName);
                if (file.isDirectory()) {
                    new File(INSTRUMENTED_SRC_DIR.getPath() + packageName).mkdirs();
                } else {
                    File dest = new File(INSTRUMENTED_SRC_DIR.getPath() + packageName);
                    fr = new FileReader(file);
                    fw = new FileWriter(dest);
                    int c;
                    while ((c = fr.read()) != -1) fw.write((char) c);
                    fr.close();
                    fw.close();
                }
            }
        } catch (Exception e) {
            logger.error(this.getClass().getName() + "\n" + e);
        }
        return fileList;
    }

    public static ArrayList<File> getFilesRecursive(File path, String startWord, String searchWord, String extension, long beforeSecond) throws Exception {
        long timeTemp;
        ArrayList<File> list = new ArrayList<File>();
        if (path.isDirectory()) {
            for (File file : path.listFiles()) {
                if (file.isDirectory()) {
                    list.addAll(getFilesRecursive(file, startWord, searchWord, extension, beforeSecond));
                } else {
                    if (file.getName().toUpperCase().startsWith(startWord.toUpperCase()) && file.getName().indexOf(searchWord) > -1 && file.getName().toUpperCase().endsWith(extension.toUpperCase())) {
                        timeTemp = file.lastModified();
                        timeTemp = new java.util.Date().getTime() - timeTemp;
                        timeTemp /= 1000; // from mili-sec to sec
                        if (beforeSecond == 0) {
                            list.add(file);
                        } else if (beforeSecond != 0 && timeTemp <= beforeSecond) {//only file modified within one day
                            list.add(file);
                        }
                    }
                }
            }
        } else {
            System.out.println(path.getAbsolutePath() + " is not directory!");
        }
        return list;
    }

    public static ArrayList<File> findPCFFilesFromProjectDir(String dirPath) throws Exception {
        File dir = new File(dirPath);
        if (!dir.exists())
            throw new Exception("not found dir. [" + dirPath + "]");
        return findPCFFilesFromProjectDir(dir, StringUtils.countMatches(dir.getPath(), fs) + 3);
    }

    public static ArrayList<File> findPCFFilesFromProjectDir(File dir) throws Exception {
        return findPCFFilesFromProjectDir(dir, StringUtils.countMatches(dir.getPath(), fs) + 3);
    }

    public static ArrayList<File> findPCFFilesFromProjectDir(File dir, int cnt) throws Exception {
        ArrayList<File> list = new ArrayList<File>();
        if (dir.isDirectory()) {
            for (File e : dir.listFiles()) {
                if (e.isDirectory()) {
                    if (StringUtils.countMatches(e.getPath(), fs) > cnt)
                        break;
                    list.addAll(findPCFFilesFromProjectDir(e, cnt));
                } else {
                    if (e.getName().endsWith(".pcf")) {
                        list.add(e);
                    }
                }
            }
        }
        return list;
    }
}
