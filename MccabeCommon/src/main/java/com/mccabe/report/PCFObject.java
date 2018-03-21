package com.mccabe.report;

import java.io.File;
import java.util.ArrayList;

public class PCFObject {
    //	private String trId;
    private File pcfFile;
    private String projectName;
    private String instDir;
    private String instOut;
    private String comDir;
    private String srcDir;
    private String srcFile;
    private String language;
    private ArrayList<File> accumulateTraceFiles;

    /**
     * @return the pcfFile
     */
    public File getPcfFile() {
        return pcfFile;
    }
    /**
     * @param pcfFile the pcfFile to set
     */
    public void setPcfFile(File pcfFile) {
        this.pcfFile = pcfFile;
    }
    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }
    /**
     * @return the trId
     */
    public String getTrId() {
        return projectName.substring(0, projectName.indexOf("_"));
    }

    public String getProjectNameMinusTrId(){
        return projectName.substring(projectName.indexOf("_")+1);
    }
    /**
     * @return the projectName
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * @param projectName the projectName to set
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * @return the instDir
     */
    public String getInstDir() {
        return instDir;
    }

    /**
     * @param instDir the instDir to set
     */
    public void setInstDir(String instDir) {
        this.instDir = instDir;
    }

    /**
     * @return the instOut
     */
    public String getInstOut() {
        return instOut;
    }

    /**
     * @param instOut the instOut to set
     */
    public void setInstOut(String instOut) {
        this.instOut = instOut;
    }

    /**
     * @return the comDir
     */
    public String getComDir() {
        return comDir;
    }

    /**
     * @param comDir the comDir to set
     */
    public void setComDir(String comDir) {
        this.comDir = comDir;
    }

    /**
     * @return the srcDir
     */
    public String getSrcDir() {
        return srcDir;
    }

    /**
     * @param srcDir the srcDir to set
     */
    public void setSrcDir(String srcDir) {
        this.srcDir = srcDir;
    }

    /**
     * @return the srcFile
     */
    public String getSrcFile() {
        return srcFile;
    }

    /**
     * @param srcFile the srcFile to set
     */
    public void setSrcFile(String srcFile) {
        this.srcFile = srcFile;
    }

    /**
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language the language to set
     */
    public void setLanguage(String language) {
        this.language = language;
    }


    /**
     * @return the accumulateTraceFiles
     */
    public ArrayList<File> getAccumulateTraceFiles() {
        return accumulateTraceFiles;
    }

    /**
     * @param accumulateTraceFiles the accumulateTraceFiles to set
     */
    public void setAccumulateTraceFiles(ArrayList<File> accumulateTraceFiles) {
        this.accumulateTraceFiles = accumulateTraceFiles;
    }

}
