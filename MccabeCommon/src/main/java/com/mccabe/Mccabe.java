package com.mccabe;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.util.*;

import static com.mccabe.Mccabe.McCABE_PATH.*;
import static com.mccabe.Mccabe.McCABE_Properties.ProgramName;
import static com.mccabe.Mccabe.McCABE_Properties.isWindows;

public class Mccabe {
    private static final String [] DEFAULT_CW_OPTIONS = {"-PATHVEC", "-CLASS", "-MODSIG", "-HALSTEAD", "-PARAM",
            "-DATA", "-NOCLASSMSGS", "-OVERLOAD", "-MODE"};
    private static final String [] DEFAULT_PCF_OPTIONS = {"METRICS_LEVEL 3", "EXPORTTREE", "SCOPEINST", "NO_COVERAGE_SERVER"};
    private static final String JAVA_VERSION = "JDK 1.6";
    protected static Logger logger = LoggerFactory.getLogger(Mccabe.class);
    protected static Properties properties = new Properties();
    protected static String fs = System.lineSeparator();
    protected static String[] exceptionFileNames = null;
    protected static boolean spliteFileInProject = false;

    public enum McCABE_PATH {
        MCCABE_HOME, PROJECT_DIR, REPORT_DIR, TRACEFILE_HOME, MCCABE_BIN, CLI, SRC_DIR,
        INSTRUMENTED_SRC_DIR, PROJECT_PROGRAM_DIR;
        private String path;

        public void setPath(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }
    }
    
    public enum McCABE_Properties {
        ProgramName("string", ""), isWindows("boolean", "false"), fileType("array", "[\"*.java\"}"),
        exceptionFileNames("boolean", ""), spliteFileInProject("boolean", "false");
        private String kind;
        private String value;

        McCABE_Properties(String kind, String value) {
            this.kind = kind;
            this.value = value;
        }

        public String getString() throws Exception {
            if (!kind.equals("string"))
                throw new Exception("value is not string. the value is " + kind);
            return value;
        }

        public boolean getBoolean() throws Exception {
            if (!kind.equals("boolean"))
                throw new Exception("value is not boolean. the value is " + kind);
            return Boolean.parseBoolean(value);
        }

        public String[] getArray() throws Exception {
            if (!kind.equals("array"))
                throw new Exception("value is not array. the value is " + kind);
            return (String[]) ((JSONArray) new JSONParser().parse(value)).toArray();
        }
    }

    public enum PCF {
        PROGRAM, INSTDIR, INSTOUT, COMDIR, DIR;
        private static List<String> options = new ArrayList<>();
        private static List<String> cwOptions = new ArrayList<>();
        private static String jdkVersion;
        private String value;

        public void setValue(String value) {
            this.value = value;
        }

        public void export(List<String> list, String filePath) {
            for (PCF e : values()) {
                list.add(e.name() + " " + e.value);
            }
            list.addAll(options);
            list.add("cw_Java_inst " + filePath + getCWOptions() + jdkVersion);
        }

        private String getCWOptions() {
            String ret = " ";
            for (String op : cwOptions) {
                ret+= op + " ";
            }
            return ret;
        }
    }

    public static void checkAndSetProperties(String[] args) throws Exception {
        if (args.length > 0) {
            properties.load(new FileInputStream(args[0]));
            if (System.getProperty("os.name").toString().toLowerCase().contains("win"))
                isWindows.value = "true";
            if (!properties.containsKey(McCABE_PATH.MCCABE_HOME.name()) &&
                    System.getProperty((McCABE_PATH.MCCABE_HOME.name())) == null) {
                logger.error("MCCABE_HOME not set.");
                throw new Exception("MCCABE_HOME not set.");
            }
            initPathProperties();
        }
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
                properties.getProperty(PROJECT_DIR.name() + fs + ProgramName.getString()));
        for (McCABE_PATH element : McCABE_PATH.values()) {
            String name = element.name();
            if (properties.contains(name)) {
                element.setPath(properties.getProperty(name));
                logger.debug(name + " path set: " + element.getPath());
            }
        }
        for (McCABE_Properties element : McCABE_Properties.values()) {
            if (properties.contains(element.name())) {
                element.value = properties.getProperty(element.name());
            }
        }
        // make PCF options.
        PCF.cwOptions.addAll(Arrays.asList(DEFAULT_CW_OPTIONS));
        PCF.options.addAll(Arrays.asList(DEFAULT_PCF_OPTIONS));
        PCF.jdkVersion = JAVA_VERSION;
    }
}
