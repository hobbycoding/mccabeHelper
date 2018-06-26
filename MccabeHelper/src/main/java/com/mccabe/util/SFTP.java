package com.mccabe.util;

import com.jcraft.jsch.*;
import com.mccabe.McCabeConfig;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
            getFileFromDst(getSrcList());
            end();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createFolder() throws IOException {
        OSUtil.executeCommand((OS.equalsIgnoreCase("windows") ? "cmd /c mkdir " : "mkdir -p ") + REPORT_DIR + fs + property.getProperty("programName"));
        OSUtil.executeCommand((OS.equalsIgnoreCase("windows") ? "cmd /c mkdir " : "mkdir -p ") + TRACEFILE_HOME + fs + property.getProperty("programName"));
    }

    private Map<String, File> getSrcList() throws Exception {
        HashMap<String, File> outFileList = new HashMap<>();
        String srcListPath = TRACEFILE_HOME + fs + property.getProperty("programName");
        File outFileFolder = new File(srcListPath);
        if (!outFileFolder.isDirectory())
            throw new Exception(srcListPath + " is not a directory. please check [TRACEFILE_HOME] property.");
        for (File file : outFileFolder.listFiles()) {
            if (!file.isDirectory()) {
                outFileList.put(file.getName(), file);
            }
        }
        log("== project List ==");
        log(outFileList);
        return outFileList;
    }

    private void getFileFromDst(Map<String, File> srcList) throws SftpException {
        for (Object o : channelSftp.ls(property.getProperty("remote.dir"))) {
            ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) o;
            if (entry.getFilename().endsWith(".out")) {
                String dstPath = TRACEFILE_HOME + fs + property.getProperty("programName") + fs;
                String dstFileName = entry.getFilename();
                if (property.containsKey("traceout_suffix")) {
                    dstFileName = dstFileName.replace(".out", property.getProperty("traceout_suffix") + ".out");
                }
                if (srcList.containsKey(dstFileName)) {
                     if (srcList.get(dstFileName).length() < entry.getAttrs().getSize()) {
                         log("src size : " + srcList.get(dstFileName).length() + "|| remote size : " + entry.getAttrs().getSize());
                         log("get [" + entry.getFilename() + "], put in [" + dstPath + dstFileName + "]");
                         channelSftp.get(property.getProperty("remote.dir") + "/" + entry.getFilename(), dstPath + dstFileName);
                     }
                } else {
                    log("get [" + entry.getFilename() + "], put in [" + dstPath + dstFileName + "]");
                    channelSftp.get(property.getProperty("remote.dir") + "/" + entry.getFilename(), dstPath + dstFileName);
                }
            }
        }
    }

    private boolean isTraceDirIn(File file) {
        if (file.listFiles((dir, name) -> {
            if (name.contains("out"))
                return true;
            return false;
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
