package com.mccabe;


import com.mccabe.temp.WLog;

import java.io.File;
import java.io.FileInputStream;
import java.util.Calendar;
import java.util.Map;
import java.util.Properties;

public class McCabeConfig {
    public static String OS = "unix";
    public static String fs = File.separator;
    public static int MCCABE_ON = 1;
    public static int MCCABE_OFF = 0;

    public static String MCCABE_HOME = "/usr/mccabe";
    public static String PROJECT_DIR = MCCABE_HOME + fs +"projects";
    public static String REPORT_DIR = MCCABE_HOME + fs + "report";

    public static String REMOTE_TRACEFILES = MCCABE_HOME + fs + "remote_tracefiles";
    public static String TRACEFILE_HOME = MCCABE_HOME + fs + "tracefiles";
    public static String LATEST_SVN_LOG_FILE = MCCABE_HOME + fs + "jenkins.log";

    public static String MCCABE_BIN = MCCABE_HOME + fs + "bin";
    public static String PCF_TEMPLATE = MCCABE_BIN + fs + "pcfTemplate.pcf";
    public static String PCF_TEMPLATE16 = MCCABE_BIN + fs + "pcfTemplate.pcf";
    public static String PCF_TEMPLATE14 = MCCABE_BIN + fs + "pcfTemplate.pcf";
    public static String CLI = MCCABE_BIN + fs + "cli ";

    public static String SRC_DIR = "src";
    public static String INSTRUMENTED_SRC_DIR = MCCABE_HOME + fs + "instsrc";

    public static String REPORT_TEMPLATE_NAME = "";
    public static String PROGRAM_NAME = "";

    public static String FLAG_ADDED = "A         ";//9 spaces
    public static String FLAG_UPDATE = "U         ";
    public static String FLAG_DELETE = "D         ";

    public static String FLAG_PUBLISH_ON = "ON";
    public static String FLAG_PUBLISH_OFF = "OFF";
    public static boolean REMOVE_REPORT_DIR = false;
    public static boolean SPLIT_FILE = true;

    public static String HUDSON_JOB_DIR = "/scourt/application/eup/ci/jenkins/jobs";
    public static String JENKINS_JOB_DIR = "/scourt/application/eup/ci/jenkins/jobs";
    public static String HUDSON_WEB_ROOT = "/ciserv/tomcat6/webapps/ROOT";
    public static WLog log;
    //fuck code..but There is no time...
    // TODO : remove static values. switch properties.
    public static Properties changeProperties(String[] args) throws Exception {
        Properties properties;
        if (args.length > 0) {
            properties = new Properties();
            properties.load(new FileInputStream(args[0]));
            if (System.getProperty("os.name").toString().toLowerCase().contains("win"))
                OS = "windows";
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                switch (entry.getKey().toString()) {
                    case "OS":
                        OS = entry.getValue().toString();
                        break;
                    case "FS":
                        fs = entry.getValue().toString();
                        break;
                    case "WORK_HOME":
                        MCCABE_HOME = entry.getValue().toString();
                        TRACEFILE_HOME = MCCABE_HOME + fs + "tracefiles";
                        LATEST_SVN_LOG_FILE = MCCABE_HOME + fs + "jenkins.log";
                        INSTRUMENTED_SRC_DIR = MCCABE_HOME + fs + "instsrc";
                        PROJECT_DIR = MCCABE_HOME + fs + "projects";
                        REPORT_DIR = MCCABE_HOME + fs + "report";
                        PCF_TEMPLATE = MCCABE_HOME + fs + "pcfTemplate.pcf";
                        log = new WLog(new File(MCCABE_HOME + fs + "mclog.log"));
                        break;
                    case "MCCABE_BIN":
                        MCCABE_BIN = entry.getValue().toString();
                        PCF_TEMPLATE14 = MCCABE_BIN + fs + "template14.pcf";
                        PCF_TEMPLATE16 = MCCABE_BIN + fs + "template16.pcf";
                        CLI = MCCABE_BIN + fs + "cli ";
                        break;
                    case "REMOTE_TRACEFILES":
                        REMOTE_TRACEFILES = entry.getValue().toString();
                        break;
                    case "HUDSON_JOB_DIR":
                        HUDSON_JOB_DIR = entry.getValue().toString();
                        break;
                    case "JENKINS_JOB_DIR":
                        JENKINS_JOB_DIR = entry.getValue().toString();
                        break;
                    case "HUDSON_WEB_ROOT":
                        HUDSON_WEB_ROOT = entry.getValue().toString();
                        break;
                    case "REPORT_TEMPLATE_NAME":
                        REPORT_TEMPLATE_NAME = entry.getValue().toString();
                        if (!REPORT_TEMPLATE_NAME.startsWith("-report")) {
                            REPORT_TEMPLATE_NAME = "-report " + REPORT_TEMPLATE_NAME;
                        } else {
                            REPORT_TEMPLATE_NAME = "";
                        }
                        break;
                    case "SRC_DIR":
                        SRC_DIR = entry.getValue().toString();
                        break;
                    case "REMOVE_REPORT_DIR":
                        REMOVE_REPORT_DIR = Boolean.parseBoolean(entry.getValue().toString());
                        break;
                    case "PCF_TEMPLATE" :
                        PCF_TEMPLATE = entry.getValue().toString();
                        break;
                    case "programName" :
                        PROGRAM_NAME = entry.getValue().toString();
                        break;
                    case "SPLIT_FILE" :
                        SPLIT_FILE = Boolean.parseBoolean(entry.getValue().toString());
                        break;
                }
            }
            return properties;
        }
        return System.getProperties();
    }

    public static void log(Object msg) {
        String v = "[" + Calendar.getInstance().getTime().toString() + "] " + msg;
        System.out.println(v);
        log.write(v);
    }
}
