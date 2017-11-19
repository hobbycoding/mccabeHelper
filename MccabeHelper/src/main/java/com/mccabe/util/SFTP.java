package com.mccabe.util;

import com.jcraft.jsch.*;
import com.mccabe.McCabeConfig;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

public class SFTP extends McCabeConfig {
    private static final String HOST = "server.ip";
    private static final String USER = "userid";
    private static final String PASS = "userpw";
    private static final String PORT = "server.port";

    private Properties ps;
    private JSch jsch = new JSch();
    private Session session;
    private Channel channel;
    private ChannelSftp channelSftp;
    private String jsonFileName;

    public SFTP(Properties ps) {
        this.ps = ps;
    }

    public static void main(String[] args) throws Exception {
        SFTP main = new SFTP(changeProperties(args));
        main.start();
    }

    private void start() {
        try {
            connect();
            createFolder();
            List<String> projectList = getSrcList();
            getFileFromDst(projectList);
            end();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createFolder() throws IOException {
        OSUtil.executeCommand((OS.equalsIgnoreCase("windows") ? "cmd /c mkdir " : "mkdir -p ") + REPORT_DIR + fs + ps.getProperty("programName"));
        OSUtil.executeCommand((OS.equalsIgnoreCase("windows") ? "cmd /c mkdir " : "mkdir -p ") + TRACEFILE_HOME + fs + ps.getProperty("programName"));
    }

    private List<String> getSrcList() throws Exception {
        List<String> projectList = new ArrayList<>();
        String paht = PROJECT_DIR + fs + ps.getProperty("programName");
        jsonFileName = paht + fs + "fileList.json";
        File folder = new File(paht);
        File jsonFile = new File(jsonFileName);
        if (!folder.isDirectory())
            throw new Exception(paht + " is not a directory. please check [MCCABE_HOME] property.");
        if (!jsonFile.exists())
            throw new Exception(paht + " not found.");
        File fileList = new File(jsonFileName);
        projectList.addAll((JSONArray) new JSONParser().parse(new FileReader(fileList)));
        log("== project List ==");
        log(projectList);
        return projectList;
    }

    private void getFileFromDst(List<String> projectList) throws SftpException {
        for (String name : projectList) {
            String file = ps.getProperty("remote.dir") + "/" + name + "_inst.out";
            channelSftp.get(file, TRACEFILE_HOME + fs + name);
            log("get [" + ps.getProperty("remote.dir") + fs + name + "_inst.out], put in [" + TRACEFILE_HOME + fs + name + "]");
        }
        new File(jsonFileName).delete();
    }

    private boolean isProjectDir(File file) {
        if (file.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if (name.contains("pcf"))
                    return true;
                return false;
            }
        }).length >= 1) {
            return true;
        }
        return false;
    }

    private void connect() throws JSchException {
        session = jsch.getSession(ps.getProperty(USER), ps.getProperty(HOST), Integer.parseInt(ps.getProperty(PORT)));
        session.setConfig("StrictHostKeyChecking", "no");
        session.setPassword(ps.getProperty(PASS));
        session.connect();
        channel = session.openChannel("sftp");
        channelSftp = (ChannelSftp) channel;
        channelSftp.connect();
        log("connected from " + ps.getProperty(HOST) + " : " + ps.getProperty(PORT));
    }

    public void end() {
        channelSftp.disconnect();
        channel.disconnect();
        session.disconnect();
        log("End.");
    }
}
