package com.mccabe.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.mccabe.McCabeConfig;

public class FileUtil extends McCabeConfig {

    public static void main(String[] args) {
        System.out.println("xyz".startsWith(""));
        System.out.println("xyz".indexOf(""));
        System.out.println("xyz".endsWith(""));
    }

    public static ArrayList<File> omitFiles(ArrayList<File> files, String[] omitStrArray) {
        for (int i = 0; i < omitStrArray.length; i++) {
            files = omitFiles(files, omitStrArray[i]);
        }
        return files;
    }

    private static ArrayList<File> omitFiles(ArrayList<File> files, String omitStr) {
        ArrayList<File> newFiles = new ArrayList<File>();
        for (File file : files) {
            if (file.getName().toUpperCase().indexOf(omitStr.toUpperCase()) < 0) {
                newFiles.add(file);
            } else {
                System.out.println(file.getAbsolutePath() + "is omitted.");
            }
        }
        return newFiles;
    }

    public static void deleteDirectory(File file) {
        if (file.exists()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
            file.delete();
        } else {
            return;
        }
    }

    public static void fileOut(String print, String tempFileName, boolean append) {
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(tempFileName, append), true);
            pw.println(print);
            pw.close();
        } catch (Exception e) {
        }
    }


    /**
     * Gathering ONLY files modified within one day(86400 sec)
     * If beforeSecond = 0 , gathering ALL
     *
     * @param path
     * @param startWord
     * @param searchWord
     * @param extension
     * @param beforeSecond
     * @return
     * @throws Exception
     */
    public static ArrayList<File> getFilesRecursive(File path, String startWord, String searchWord, String extension, long beforeSecond) throws Exception {
        long timeTemp;
        ArrayList<File> list = new ArrayList<>();
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

    public static String getRoledFileName(File file, String path) {
        return file.getAbsolutePath().replace(path + fs, "").replace(fs, "_").replace(".java", "");
    }

}
