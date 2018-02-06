package com.mccabe.util;

import com.mccabe.Mccabe;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


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

                logger.debug("copySrcToInst : " + prop.getProperty("instDir") + packageName);
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
            logger.error(this.getClass().getName() + "\n" + e);
        }
        return fileList;
    }
}
