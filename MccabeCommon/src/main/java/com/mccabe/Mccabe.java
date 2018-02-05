package com.mccabe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.util.Map;
import java.util.Properties;

import static com.mccabe.Mccabe.Mccabe_PATH.*;

public class Mccabe {
    private static Logger logger = LoggerFactory.getLogger(Mccabe.class);
    private static Properties properties = new Properties();
    private static boolean isWin = false;
    private static String fs = System.lineSeparator();
    public enum Mccabe_PATH {
        MCCABE_HOME, PROJECT_DIR, REPORT_DIR, TRACEFILE_HOME, MCCABE_BIN, CLI, PROGRAM_NAME, SRC_DIR,
        INSTRUMENTED_SRC_DIR, PROJECT_PROGRAM_DIR;
        private String path;

        public void setPath(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }
    }

    public static void checkAndSetProperties(String[] args) throws Exception {
        if (args.length > 0) {
            properties.load(new FileInputStream(args[0]));
            if (System.getProperty("os.name").toString().toLowerCase().contains("win"))
                isWin = true;
            if (!properties.containsKey(Mccabe_PATH.MCCABE_HOME.name()) &&
                    System.getProperty((Mccabe_PATH.MCCABE_HOME.name())) == null) {
                logger.error("MCCABE_HOME not set.");
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
                    if (!properties.containsKey(TRACEFILE_HOME.name()))
                        properties.setProperty(TRACEFILE_HOME.name(), entry.getValue().toString() + fs + "tracefiles");
                    if (!properties.containsKey(PROJECT_DIR.name()))
                        properties.setProperty(PROJECT_DIR.name(), entry.getValue().toString() + fs + "projects");
                    if (!properties.containsKey(REPORT_DIR.name()))
                        properties.setProperty(REPORT_DIR.name(), entry.getValue().toString() + fs + "report");
                    if (!properties.containsKey(INSTRUMENTED_SRC_DIR))
                        properties.setProperty(INSTRUMENTED_SRC_DIR.name(), entry.getValue().toString() + fs + "instsrc");
                    break;
                case "MCCABE_BIN":
                    properties.setProperty("CLI", entry.getValue().toString() + fs + "cli ");
                    break;
            }
        }
        properties.setProperty(PROJECT_PROGRAM_DIR.name(),
                properties.getProperty(PROJECT_DIR.name() + fs + PROGRAM_NAME.name()));
        for (Mccabe_PATH element : Mccabe_PATH.values()) {
            String name = element.name();
            if (properties.contains(name)) {
                element.setPath(properties.getProperty(name));
                logger.debug(name + " path set: " + element.getPath());
            }
        }
    }
}
