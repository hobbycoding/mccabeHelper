package com.mccabe.util;

import com.jcraft.jsch.*;
import com.mccabe.McCabeConfig;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SFTP extends McCabeConfig {
    private static final String HOST = "server.ip";
    private static final String USER = "userid";
    private static final String PASS = "userpw";
    private static final String PORT = "server.port";

    private JSch jsch = new JSch();
    private Session session;
    private Channel channel;
    private ChannelSftp channelSftp;
    private boolean importAll = false;

    public SFTP(Properties ps) {
        super(ps);
    }

    public static void main(String[] args) throws Exception {
        SFTP main = new SFTP(changeProperties(args));
        main.start();
    }

    private void start() {
        try {
            connect();
            createFolder();
            getFileFromDst();
            end();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createFolder() throws IOException {
        OSUtil.executeCommand((OS.equalsIgnoreCase("windows") ? "cmd /c mkdir " : "mkdir -p ") + REPORT_DIR + fs + property.getProperty("programName"));
        OSUtil.executeCommand((OS.equalsIgnoreCase("windows") ? "cmd /c mkdir " : "mkdir -p ") + TRACEFILE_HOME + fs + property.getProperty("programName"));
    }

    private List<String> getSrcList() throws Exception {
        List<String> projectList = new ArrayList<>();
        File folder = new File(PROJECT_DIR);
        if (!folder.isDirectory())
            throw new Exception(PROJECT_DIR + " is not a directory. please check [MCCABE_HOME] property.");
        for (File file : folder.listFiles()) {
            if (file.isDirectory() && isProjectDir(file)) {
                projectList.add(file.getName());
            }
        }
        log("== project List ==");
        log(projectList);
        return projectList;
    }

    private void getFileFromDst() throws SftpException {
        for (Object o : channelSftp.ls(property.getProperty("remote.dir"))) {
            ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) o;
            if (entry.getFilename().endsWith(".out")) {
                String dstFileName = TRACEFILE_HOME + fs + property.getProperty("programName") + fs + entry.getFilename();
                if (property.containsKey("traceout_suffix")) {
                    dstFileName.replace(".out", property.getProperty("traceout_suffix") + ".out");
                }
                log("get [" + entry.getFilename() + "], put in [" + dstFileName + "]");
                channelSftp.get(property.getProperty("remote.dir") + "/" + entry.getFilename(), dstFileName);
            }
        }
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
        session = jsch.getSession(property.getProperty(USER), property.getProperty(HOST), Integer.parseInt(property.getProperty(PORT)));
        session.setConfig("StrictHostKeyChecking", "no");
        session.setPassword(property.getProperty(PASS));
        session.connect();
        channel = session.openChannel("sftp");
        channelSftp = (ChannelSftp) channel;
        channelSftp.connect();
        log("connected from " + property.getProperty(HOST) + " : " + property.getProperty(PORT));
    }

    public void end() {
        channelSftp.disconnect();
        channel.disconnect();
        session.disconnect();
        log("End.");
    }
}
