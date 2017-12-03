package com.mccabe.vo;

import java.io.File;
import java.util.Properties;

import org.apache.tools.ant.types.FileSet;

import com.mccabe.McCabeConfig;
import com.mccabe.util.StringUtil;

public class ProjectForJava extends McCabeConfig {
    private String projectName;
    private String projectDir;
    private String instDir;
    private String instFile;
    private String comDir;
    private boolean isClean = true;
    private boolean isInstrument = true;
    private boolean isExport = true;
    private boolean isParse = true;
    private FileSet fileSet;
    private String uniqueTrPath;

    /**
     * @return the isParse
     */
    public boolean isParse() {
        return isParse;
    }

    /**
     * @param isParse the isParse to set
     */
    public void setParse(boolean isParse) {
        this.isParse = isParse;
    }


    /**
     * @param job
     * @param compileKey : ex) eap/EAPJava/JavaSource
     * @param javaFile   : ex) /tmp/EUP2.2/svnfiles/src/eap/EAPEJB/ejbModule/scourt/eap/ejb/CV879s01Cmd.java
     */
    public ProjectForJava(Job job, String compileKey, File javaFile) {
        super(null);
        String packagePath = StringUtil.replace(javaFile.getAbsolutePath(), job.getRepositoryRoot() + fs + compileKey + fs, "");
        if (packagePath.indexOf(fs) > -1) {
            packagePath = packagePath.substring(0, packagePath.lastIndexOf(fs));
        } else {
            packagePath = "";
        }
        String javaFileName = javaFile.getName();
        String javaName = javaFileName.substring(0, javaFileName.lastIndexOf(".java"));
        this.projectName = (compileKey + "_" + packagePath + "_" + javaName).replace(fs, "_");
        this.projectDir = PROJECT_DIR + fs + job.getSysName() + fs + projectName;
        this.comDir = INSTRUMENTED_SRC_DIR + fs + compileKey;
        this.instDir = comDir + fs + packagePath;
        this.instFile = "inst.out";
        this.fileSet = new FileSet();
        fileSet.setFile(javaFile);

    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
//		ProjectForJava pfj = new ProjectForJava(null, "whg/WHGWeb/JavaSource", new File("/app/svnfiles/src/whg/WHGWeb/JavaSource/IXyncFilter.java"));
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
     * @return the projectDir
     */
    public String getProjectDir() {
        return projectDir;
    }

    /**
     * @param projectDir the projectDir to set
     */
    public void setProjectDir(String projectDir) {
        this.projectDir = projectDir;
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
     * @return the instFile
     */
    public String getInstFile() {
        return instFile;
    }

    /**
     * @param instFile the instFile to set
     */
    public void setInstFile(String instFile) {
        this.instFile = instFile;
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
     * @return the isClean
     */
    public boolean isClean() {
        return isClean;
    }

    /**
     * @param isClean the isClean to set
     */
    public void setClean(boolean isClean) {
        this.isClean = isClean;
    }

    /**
     * @return the isInstrument
     */
    public boolean isInstrument() {
        return isInstrument;
    }

    /**
     * @param isInstrument the isInstrument to set
     */
    public void setInstrument(boolean isInstrument) {
        this.isInstrument = isInstrument;
    }

    /**
     * @return the isExport
     */
    public boolean isExport() {
        return isExport;
    }

    /**
     * @param isExport the isExport to set
     */
    public void setExport(boolean isExport) {
        this.isExport = isExport;
    }

    /**
     * @return the fileSet
     */
    public FileSet getFileSet() {
        return fileSet;
    }

    /**
     * @param fileSet the fileSet to set
     */
    public void setFileSet(FileSet fileSet) {
        this.fileSet = fileSet;
    }

    /**
     * @return the uniqueTrPath
     */
    public String getUniqueTrPath() {
        return uniqueTrPath;
    }

    /**
     * @param uniqueTrPath the uniqueTrPath to set
     */
    public void setUniqueTrPath(String uniqueTrPath) {
        this.uniqueTrPath = uniqueTrPath;
    }


}
