package com.mccabe.inst;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;

import com.mccabe.McCabeConfig;
import com.mccabe.util.FileUtil;
import com.mccabe.util.StringUtil;
import com.mccabe.vo.Job;

public class ListManager extends McCabeConfig {

    private ArrayList<File> fileListWeb;
    private ArrayList<File> fileListEJB;
    private ArrayList<File> fileListJava;

    private String keyOfWeb = "Web" + fs + "JavaSource";
    private String keyOfEJB = "EJB" + fs + "ejbModule";
    private String keyOfJava = "Java" + fs + "JavaSource";

    public ListManager(String sysName) {
/*
 * keyOf???
 * WIMWeb/JavaSource
 * WIMEJB/ejbModule
 * WIMJava/JavaSource
*/
        /*if(sysName.equals("ecv_n")){
			System.out.println("ECV_N keys for mccbe will be set specially.");
			keyOfWeb = "ECVWeb"+fs+"src";
			keyOfEJB = "ECVEJB"+fs+"ejbModule";
			keyOfJava = "ECVJava"+fs+"src";
		}else{
		}*/
        keyOfWeb = sysName + fs + sysName.toUpperCase() + keyOfWeb;
        keyOfEJB = sysName + fs + sysName.toUpperCase() + keyOfEJB;
        keyOfJava = sysName + fs + sysName.toUpperCase() + keyOfJava;//        eap/EAPJava/JavaSource

        fileListWeb = new ArrayList<File>();
        fileListEJB = new ArrayList<File>();
        fileListJava = new ArrayList<File>();
    }

    public void gatheringList(Job job) {

        HashMap<String, ArrayList<File>> compileMap = new HashMap<String, ArrayList<File>>();

        try {
//            compileMap.put(keyOfWeb, FileUtil.getFilesRecursive(new File(job.getRepositoryRoot() + fs + keyOfWeb), "", "", ".java", 0));
//            compileMap.put(keyOfEJB, FileUtil.getFilesRecursive(new File(job.getRepositoryRoot() + fs + keyOfEJB), "", "", ".java", 0));
            compileMap.put(keyOfJava, FileUtil.getFilesRecursive(new File(MCCABE_HOME + fs + "build"), "", "", ".java", 0));
        } catch (Exception e) {
            e.printStackTrace();
        }
        job.setCompileMap(compileMap);
    }

    /**
     * [svn] U  /tmp/EUP2.2/svnfiles/src/ecf/ECFWeb/JavaSource/scourt/ecf/biz/ECF000Biz.java
     * [svn] U  /tmp/EUP2.2/svnfiles/src/ecf/ECFWeb/JavaSource/scourt/ecf/biz/ECF320Biz.java
     *
     * @param job
     * @throws IOException
     */
    public void parseSVNLogFile(Job job) throws IOException {

        HashMap<String, ArrayList<File>> compileMap = new HashMap<String, ArrayList<File>>();

        Reader reader = new FileReader(LATEST_SVN_LOG_FILE);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line = "";

        try {
            while ((line = bufferedReader.readLine()) != null && line != "") {
                if ((line.indexOf(FLAG_ADDED) > -1) || (line.indexOf(FLAG_UPDATE) > -1)) {
                    if (line.indexOf(".java") > -1) {
                        line = line.trim();
                        if (line.indexOf(keyOfWeb) > -1) {
                            line = StringUtil.replace(line, FLAG_ADDED, ""); // line --> /tmp/EUP2.2/svnfiles/src/ecf/ECFWeb/JavaSource/scourt/ecf/biz/ECF320Biz.java
                            line = StringUtil.replace(line, FLAG_UPDATE, "");// line --> /tmp/EUP2.2/svnfiles/src/ecf/ECFWeb/JavaSource/scourt/ecf/biz/ECF320Biz.java
                            fileListWeb.add(new File(line.trim()));
                        } else if (line.indexOf(keyOfEJB) > -1) {
                            line = StringUtil.replace(line, FLAG_ADDED, "");
                            line = StringUtil.replace(line, FLAG_UPDATE, "");
                            fileListEJB.add(new File(line.trim()));
                        } else if (line.indexOf(keyOfJava) > -1) {
                            line = StringUtil.replace(line, FLAG_ADDED, "");
                            line = StringUtil.replace(line, FLAG_UPDATE, "");
                            fileListJava.add(new File(line.trim()));
                        }
                    }
                } else if (line.indexOf(FLAG_DELETE) > -1) {
                    if (line.indexOf(".java") > -1) {
                        line = StringUtil.replace(line, FLAG_DELETE, ""); // line --> /tmp/EUP2.2/svnfiles/src/ecf/ECFWeb/JavaSource/scourt/ecf/biz/ECF320Biz.java
                        line = StringUtil.replace(line, job.getRepositoryRoot(), INSTRUMENTED_SRC_DIR); // line --> "/app/EUP2.2/mccabe/instsrc/ecf/ECFWeb/JavaSource/scourt/ecf/biz/ECF320Biz.java
                        File removeFile = new File(line.trim());
                        if (removeFile.delete()) {
                            System.out.println(removeFile.toString() + "is deleted !!!");
                        } else {
                            System.out.println(removeFile.toString() + "is not deleted !!!");
                        }
                    }
                }
            }
            compileMap.put(keyOfWeb, fileListWeb);
            compileMap.put(keyOfEJB, fileListEJB);
            compileMap.put(keyOfJava, fileListJava);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bufferedReader.close();
            reader.close();
        }
        job.setCompileMap(compileMap);
    }

    /**
     * @return the keyOfWeb
     */
    public String getKeyOfWeb() {
        return keyOfWeb;
    }

    /**
     * @param keyOfWeb the keyOfWeb to set
     */
    public void setKeyOfWeb(String keyOfWeb) {
        this.keyOfWeb = keyOfWeb;
    }

    /**
     * @return the keyOfEJB
     */
    public String getKeyOfEJB() {
        return keyOfEJB;
    }

    /**
     * @param keyOfEJB the keyOfEJB to set
     */
    public void setKeyOfEJB(String keyOfEJB) {
        this.keyOfEJB = keyOfEJB;
    }

    /**
     * @return the keyOfJava
     */
    public String getKeyOfJava() {
        return keyOfJava;
    }

    /**
     * @param keyOfJava the keyOfJava to set
     */
    public void setKeyOfJava(String keyOfJava) {
        this.keyOfJava = keyOfJava;
    }

    public static void main(String[] args) throws Exception {
        String line = "     [svn] U  /tmp/EUP2.2/svnfiles/src/ecm/ECMJava/JavaSource/scourt/ecm/cm/util/SCBoxComparator.java";
        line = StringUtil.replace(line, FLAG_UPDATE, ""); // line --> /tmp/EUP2.2/svnfiles/src/ecf/ECFWeb/JavaSource/scourt/ecf/biz/ECF320Biz.java
        System.out.println("line[" + line + "]");
    }
}
