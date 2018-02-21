package com.mccabe;

import org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.mccabe.Mccabe.McCABE_PATH.*;
import static com.mccabe.Mccabe.McCABE_Properties.programName;
import static com.mccabe.Mccabe.McCABE_Properties.isWindows;
import static com.mccabe.Mccabe.McCABE_Properties.splitFileInProject;

public class Mccabe {
    private static final String [] DEFAULT_CW_OPTIONS = {"-PATHVEC", "-CLASS", "-MODSIG", "-HALSTEAD", "-PARAM",
            "-DATA", "-NOCLASSMSGS", "-OVERLOAD", "-MODE"};
    private static final String [] DEFAULT_PCF_OPTIONS = {"METRICS_LEVEL 3", "EXPORTTREE", "SCOPEINST", "NO_COVERAGE_SERVER"};
    private static final String JAVA_VERSION = "JDK 1.6";
    protected static Logger logger = LoggerFactory.getLogger(Mccabe.class);
    protected static Properties properties = new Properties();
    protected static String fs = File.separator;

    public enum McCABE_PATH {
        MCCABE_HOME(false), MCCABE_BIN(false), CLI(false), SRC_DIR((false)), INSTRUMENTED_SRC_DIR(false), PROJECT_PROGRAM_DIR(false),
        PROJECT_DIR(true), REPORT_DIR(true), TRACEFILE_HOME(true);
        private String path;
        private boolean createDir;

        McCABE_PATH(boolean createDir) {
            this.createDir = createDir;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getPath() throws Exception {
            if (path != null)
                return path;
            switch (this) {
                case TRACEFILE_HOME:
                    return MCCABE_HOME.path + fs + "tracefiles";
                case PROJECT_DIR:
                    return MCCABE_HOME.path + fs + "projects";
                case REPORT_DIR:
                    return MCCABE_HOME.path + fs + "report";
                case INSTRUMENTED_SRC_DIR:
                    return MCCABE_HOME.path + fs + "instsrc";
                case PROJECT_PROGRAM_DIR:
                    return PROJECT_DIR.getPath() + fs + programName.getString();
                case CLI:
                    return MCCABE_BIN.path + fs + "cli export -pcf ";
            }
            throw new Exception("not found path.");
        }
    }
    
    public enum McCABE_Properties {
        programName("string", ""), isWindows("boolean", "false"), fileType("array", "[\"java\"]"),
        exceptionFileNames("array", "[]"), splitFileInProject("boolean", "false"), traceFileOutPath("string", "");
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
            JSONArray array = ((JSONArray) new JSONParser().parse(value));
            String [] result = new String[array.size()];
            array.toArray(result);
            return result;
        }
    }

    public enum PCF {
        PROGRAM, INSTDIR, INSTOUT, COMDIR, DIR;
        private static Path filePath;
        private static List<String> options = new ArrayList<>();
        private static List<String> cwOptions = new ArrayList<>();
        private static String jdkVersion;
        private String value;

        public static void setFilePath(Path filePath) throws IOException {
            if (Files.notExists(filePath.getParent())) {
                Files.createDirectories(filePath.getParent());
                logger.debug(filePath.getParent() + " path not exist. create.");
            }
            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
                logger.debug(filePath + " path not exist. create.");
            }
            PCF.filePath = filePath;
        }

        public static Path getFilePath() {
            return filePath;
        }

        public static String getAbsolutePathForPathVec() throws Exception {
            String absolute = COMDIR.value + fs + "com" + fs + "mccabe" + fs + "PathVec_" + programName.getString();
            if (splitFileInProject.getBoolean())
                return absolute + FilenameUtils.removeExtension(getFilePath().getFileName().toString()) + ".java";
            return absolute + ".java";
        }

        public void setValue(String value) {
            this.value = value;
        }

        public static void addFile(List<String> list, Object obj) throws Exception {
            if (obj instanceof Collection) {
                for (File element : (Collection<File>) obj) {
                    String pathWithPackageOnly = element.getAbsolutePath().substring(SRC_DIR.getPath().length() + 1);
                    list.add("cw_Java_inst " + pathWithPackageOnly + getCWOptions() + jdkVersion);
                }
            } else {
                String pathWithPackageOnly = ((File)obj).getAbsolutePath().substring(SRC_DIR.getPath().length() + 1);
                list.add("cw_Java_inst " + pathWithPackageOnly + getCWOptions() + jdkVersion);
            }
        }

        public static void export(List<String> list, Object file) throws Exception {
            for (PCF e : values()) {
                list.add(e.name() + " " + e.value);
            }
            list.addAll(options);
            addFile(list, file);
        }

        private static String getCWOptions() {
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
            checkAndSetProperties();
        }
    }

    public static void checkAndSetProperties(Properties properties) throws Exception {
        Mccabe.properties = properties;
        checkAndSetProperties();
    }

    private static void checkAndSetProperties() throws Exception {
            if (System.getProperty("os.name").toString().toLowerCase().contains("win"))
                isWindows.value = "true";
            if (!properties.containsKey(McCABE_PATH.MCCABE_HOME.name()) &&
                    System.getProperty((McCABE_PATH.MCCABE_HOME.name())) == null) {
                logger.error("MCCABE_HOME not set.");
                throw new Exception("MCCABE_HOME not set.");
            }
            initPathProperties();
    }

    private static void initPathProperties() throws Exception {
        for (McCABE_PATH element : McCABE_PATH.values()) {
            String name = element.name();
            if (properties.containsKey(name)) {
                element.setPath(properties.getProperty(name));
                logger.debug(name + " path set: " + element.getPath());
                if (element.createDir && Files.notExists(Paths.get(element.getPath()))) {
                    Files.createDirectories(Paths.get(element.getPath()));
                    logger.debug(name + " path not exist. create.");
                }
            }
        }
        for (McCABE_Properties element : McCABE_Properties.values()) {
            if (properties.containsKey(element.name())) {
                element.value = properties.getProperty(element.name());
            }
        }
        // make PCF options.
        PCF.cwOptions.addAll(Arrays.asList(DEFAULT_CW_OPTIONS));
        PCF.options.addAll(Arrays.asList(DEFAULT_PCF_OPTIONS));
        PCF.jdkVersion = JAVA_VERSION;
    }
}
