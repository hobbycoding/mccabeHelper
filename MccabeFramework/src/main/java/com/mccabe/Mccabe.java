package com.mccabe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Properties;

public class Mccabe {
    private static Properties properties = new Properties();
    private static boolean isWin = false;
    private static String fs = System.lineSeparator();
    public enum Mccabe_PATH {
        MCCABE_HOME, PROJECT_DIR, REPORT_DIR, TRACEFILE_HOME, MCCABE_BIN, CLI, PROGRAM_NAME, SRC_DIR
    }

    public static void setProperties(String[] args) throws Exception {
        if (args.length > 0) {
            properties.load(new FileInputStream(args[0]));
            if (System.getProperty("os.name").toString().toLowerCase().contains("win"))
                isWin = true;
            if (!properties.containsKey(Mccabe_PATH.MCCABE_HOME.name()) &&
                    System.getProperty((Mccabe_PATH.MCCABE_HOME.name())) == null) {
                throw new Exception("MCCABE_HOME not set.");
            }
            initPathProperties();
        }
    }

    public static String getPath(Mccabe_PATH path) throws Exception {
        if (properties.containsKey(path.name())) {
            return properties.getProperty(path.name());
        }
        throw new Exception("not found " + path.name());
    }

    private static void initPathProperties() throws Exception {
        Properties clone = (Properties) properties.clone();
        for (Map.Entry<Object, Object> entry : clone.entrySet()) {
            switch (entry.getKey().toString()) {
                case "FS":
                    fs = entry.getValue().toString();
                    break;
                case "WORK_HOME":
                    if (!properties.containsKey("TRACEFILE_HOME"))
                        properties.setProperty("TRACEFILE_HOME", entry.getValue().toString() + fs + "tracefiles");
                    if (!properties.containsKey("PROJECT_DIR"))
                        properties.setProperty("PROJECT_DIR", entry.getValue().toString() + fs + "projects");
                    if (!properties.containsKey("REPORT_DIR"))
                        properties.setProperty("REPORT_DIR", entry.getValue().toString() + fs + "report");
                    if (!properties.containsKey("INSTRUMENTED_SRC_DIR"))
                        properties.setProperty("INSTRUMENTED_SRC_DIR", entry.getValue().toString() + fs + "instsrc");
                    break;
                case "MCCABE_BIN":
                    properties.setProperty("CLI", entry.getValue().toString() + fs + "cli ");
                    break;
            }
        }
    }
}
