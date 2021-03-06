package com.mccabe.temp;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.mccabe.McCabeConfig;
import com.mccabe.util.FileUtil;
import com.mccabe.util.KyoboUtil;
import com.mccabe.util.MCCABERoleSet;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.server.ExportException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.mccabe.util.KyoboUtil.*;

public class DBInsert extends McCabeConfig {
    private static final Charset[] SUPPORT_CHAR = {StandardCharsets.UTF_8, Charset.forName("EUC-KR")};
    private static Map<String, List<String>> packageNames;
    private JSONArray others;

    public DBInsert(Properties properties) {
        super(properties);
    }

    public static void main(String[] args) throws Exception {
        DBInsert dbInsert = new DBInsert(changeProperties(args));
        dbInsert.start();
    }

    public void start() {
        try (Connection connection = createConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(REPORT_QUERY);
             PreparedStatement preparedStatement2 = connection.prepareStatement(UPDATE_CODES_QUERY)) {

            setPackageNames(connection);
            List<File> fileList = getFileList();
            List<SourceFile> sourceFiles = new ArrayList<>();
            if (property.containsKey("weeklySave") &&
                    Boolean.parseBoolean(property.getProperty("weeklySave"))) {
                updateParsedSourceIfHaveSavedData(connection);
            }
            for (File file : fileList) {
                SourceFile sourceFile = new SourceFile(file);
                sourceFile.parse();
                if (!sourceFile.isSkip()) {
                    log("[Start Insert / Update]");
                    KyoboUtil.putInsertQueryInPrepared(sourceFile, preparedStatement);
                    sourceFiles.add(sourceFile);
                }
            }
            connection.setAutoCommit(false);
            for (SourceFile sourceFile : sourceFiles) {
                if (!sourceFile.isSkip()) {
                    KyoboUtil.updateCodes(sourceFile, preparedStatement2);
                }
            }
            executeQuery(preparedStatement2);
            connection.commit();
            log("[Insert / Update] Done.");
        } catch (Exception e) {
            log(e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateParsedSourceIfHaveSavedData(Connection connection) throws SQLException {
        if (KyoboUtil.isHaveYesterdayData(connection)) {
            log("[Today is " + KyoboUtil.getDate() + ". update " + KyoboUtil.getDate(-1) + " columns.");
            KyoboUtil.updateYesterdayData(connection);
        }
    }

    private void executeQuery(PreparedStatement preparedStatement) throws SQLException {
        try {
            preparedStatement.executeBatch();
        } catch (Exception e) {
            throw e;
        }
    }

    private Connection createConnection() throws Exception {
        Class.forName(property.getProperty("JDBC_Driver"));
        return DriverManager.getConnection(property.getProperty("db_url"),
                property.getProperty("db_id"), property.getProperty("db_pass"));

    }

    private void setPackageNames(Connection connection) throws SQLException {
        packageNames = KyoboUtil.getCategoryNameFromDB(connection);
    }

    private List<File> getFileList() throws Exception {
        List<File> result = new ArrayList<>();
        JSONArray fileList = new JSONArray();
        for (File file : FileUtil.getFilesRecursive(new File(property.getProperty("srcDir")), "", "", ".java", 0)) {
            fileList.add(FileUtil.getRoleFileName(file, property.getProperty("srcDir")));
        }
        if (property.containsKey("selected") && property.getProperty("selected").length() > 1) {
            log("Selected package Found. " + property.getProperty("selected"));
            others = new JSONArray();
            fileList = getMatchedFiles(fileList, others, property);
        }
        for (Object object : fileList) {
            String absolutePath = FileUtil.getBackRoleFileName(property.getProperty("srcDir"), object.toString());
            result.add(new File(absolutePath));
            log("Add " + absolutePath);
        }
        return result;
    }

    public static class SourceFile {
        private final String[] REPORT_KIND = {"branch", "codecov"};
        private final Map<String, Properties> methodContent = new HashMap<>();
        private final Properties classContent = new Properties();
        private final File file;
        private boolean skip = false;
        public String className;
        public String packageName;
        public String pakageName_ko = "UNKNOWN";
        public String system_id = "UNKNOWN";
        public String date;

        public SourceFile(File file) {
            this.file = file;
        }

        public void parse() {
            try {
                CompilationUnit cu = JavaParser.parse(file);
                packageName = cu.getPackage() != null ? cu.getPackage().getPackageName() : "";
                new MethodVisitor().visit(cu, this);
                if (!file.getName().equals(className.concat(".java")))
                    log("file : [" + file.getName() + "] and class [" + className + "] are not equal. use class.");
                String reportPath = REPORT_DIR + fs + property.getProperty("programName") + fs +
                        property.getProperty("programName") + "_" + FileUtil.getRoleFileName(file, property.getProperty("srcDir"));
                parseReportCSVFile(reportPath);
                parsePackageName();
                if (property.containsKey("doNotParseCoveredLineTextFile") && Boolean.parseBoolean(property.getProperty("doNotParseCoveredLineTextFile")))
                    return;
                parseCoverdLineTextFile(reportPath);
            } catch (Exception e) {
                log("[Error] " + e.getMessage() + "\n skip file. [" + file.getName() + "]");
                e.printStackTrace();
                skip = true;
            }
            log("Parse Done");
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public Map<String, Properties> getMethodContent() {
            return methodContent;
        }

        public Properties getClassContent() {
            if (!classContent.containsKey(TAG.logicalName.name()))
                classContent.setProperty(TAG.logicalName.name(), "");
            return classContent;
        }

        public boolean isSkip() {
            return skip;
        }

        protected String parseReportCSVFile(String reportPath) throws IOException {
            for (String kind : REPORT_KIND) {
                log("[Parse File] : " + reportPath + "_" + kind + ".csv");
                List<String> list = Files.readAllLines(Paths.get(reportPath + "_" + kind + ".csv"));
                setDate(list.get(0));
                for (String line : list) {
                    if (StringUtils.countMatches(line, ",") == 3 && line.contains(".") && !line.startsWith("Total") && !line.startsWith("Average")) {
                        if (line.startsWith("\"")) {
                            String sub = line.substring(line.indexOf("\""), line.lastIndexOf("\""));
                            line = line.replace(sub, sub.replace(",", "%%"));
                        }
                        String[] e = line.split(",");
                        String n = e[0].substring(e[0].indexOf(".") + 1, e[0].length()).replace("%%", ",");
                        String methodName = MCCABERoleSet.convert(n);
                        Properties p = methodContent.get(methodName);
                        String k = kind;
                        if (k.equals("codecov")) {
                            k = "COV";
                        } else k = "BRANCH";
                        if (p == null) {
                            log("[WARN] " + methodName + " not found. [" + line + "].");
                        } else {
                            p.setProperty(k + "_CODE_LINE", e[1]);
                            p.setProperty(k + "_COVERED_LINE", e[2]);
                            p.setProperty(k + "_COVERAGE", e[3]);
                        }
                    }
                }
            }
            return reportPath;
        }

        private void parseCoverdLineTextFile(String reportPath) throws IOException {
            log("[Parse File] : " + reportPath + ".txt");
            List<String> list = null;
            boolean exception = false;
            try {
                list = Files.readAllLines(Paths.get(reportPath + ".txt"), StandardCharsets.UTF_8);
            } catch (MalformedInputException e) {
                log("MalformedInputException. try encoding EUC-KR");
                exception = true;
            }
            if (exception) {
                try {
                    list = Files.readAllLines(Paths.get(reportPath + ".txt"), Charset.forName("EUC-KR"));
                } catch (MalformedInputException ex) {
                    log("MalformedInputException. parsing Faild.");
                    throw ex;
                }
            }
            if  (list == null || list.size() < 7) {
                throw new ExportException("Text file does not exist.");
            }
            List<Properties> methodProperties = new ArrayList<>();
            List<String>  rawMethodString = new ArrayList<>();

            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).startsWith("------")) {
                    while (list.get(i++).length() != 0) {
                        rawMethodString.add(list.get(i));
                    }
                }
            }
            int letter = 0;
            for (int i = 0; i < rawMethodString.size(); i++) {
                String module = "";
                if (rawMethodString.get(i).startsWith(MCCABERoleSet.getModuleLetter(letter))) {
                    module+= rawMethodString.get(i) + "\n";
                    letter++;
                }
                while (i + 1 < rawMethodString.size() && !rawMethodString.get(i + 1).startsWith(MCCABERoleSet.getModuleLetter(letter)))
                    module+=rawMethodString.get(++i).trim();
                String methodName = "", start = "", num = "";
                for (String in : module.split("\n")) {
                    String[] raw = in.split("\\s+");
                    if (raw.length > 2) {
                        methodName = raw[2];
                        start = raw[raw.length - 2];
                        num = raw[raw.length - 1];
                    } else methodName = methodName.trim().concat(in.trim());
                }
                String RoleName = MCCABERoleSet.convert(methodName.substring(methodName.indexOf(".") + 1, methodName.length()));
                methodProperties.add(methodContent.get(RoleName));
                methodContent.get(RoleName).setProperty(REPORT_TABLE.START_LINE.name(), start);
                methodContent.get(RoleName).setProperty(REPORT_TABLE.NUM_OF_LINE.name(), num);
            }
            log("method parsing Done.");
            int index = rawMethodString.size();
            while (list.get(index++).length() != 0) {
            }
            for (Properties properties : methodProperties) {
                String code = "";
                while (list.get(index).length() != 0) {
                    code += list.get(index++) + "\n";
                }
                properties.setProperty(REPORT_TABLE.CODES.name(), code);
                index++;
            }
            log("code-part parsing Done.");
        }

        private void parsePackageName() throws SQLException {
            String[] split = packageName.split("\\.");
            String word = "KV3_MDL_";
            for (int index = 1; index < split.length; index++) {
                word += split[index].toUpperCase();
                if (packageNames.containsKey(word)) {
                    pakageName_ko = packageNames.get(word).get(0);
                    system_id = packageNames.get(word).get(1);
                }
                word +="_";
            }
        }

        private void setDate(String date) {
            String[] s = date.split("/");
            this.date = "20" + s[2] + "-" + s[0] + "-" + s[1];
        }
    }

    private static class MethodVisitor extends VoidVisitorAdapter {
        private static final String REGEX = "(@\\p{Alnum}+)(.*)";

        @Override
        public void visit(ClassOrInterfaceDeclaration n, Object arg) {
            String content = n.getJavaDoc() != null ? n.getJavaDoc().getContent() : null;
            ((SourceFile) arg).setClassName(n.getName());
            ((SourceFile) arg).classContent.putAll(createContentFromProperties(getTags(content)));
            super.visit(n, arg);
        }

        @Override
        public void visit(ConstructorDeclaration n, Object arg) {
            String content = n.getJavaDoc() != null ? n.getJavaDoc().getContent() : null;
            ((SourceFile) arg).methodContent.put(n.getName() + "(" + getParam(n.getParameters()) + ")", createContentFromProperties(getTags(content)));
            super.visit(n, arg);
        }

        @Override
        public void visit(MethodDeclaration m, Object arg) {
            String content = m.getJavaDoc() != null ? m.getJavaDoc().getContent() : null;
            ((SourceFile) arg).methodContent.put(m.getName() + "(" + getParam(m.getParameters()) + ")", createContentFromProperties(getTags(content)));

        }

        private String getParam(List<Parameter> parameters) {
            String n = "";
            for (Parameter parameter : parameters) {
                String t = MCCABERoleSet.convert(parameter);
                n += t + ",";
            }
            if (n != "")
                n = n.substring(0, n.length() - 1);
            return n;
        }

        private Properties createContentFromProperties(Properties tags) {
            if (tags.containsKey(KyoboUtil.TAG.fullPath.name())) {
                KyoboUtil.createContent(tags);
            }
            return tags;
        }

        private Properties getTags(String raw) {
            Properties properties = new Properties();
            if (raw != null) {
                Pattern pattern = Pattern.compile(REGEX);
                Matcher match = pattern.matcher(raw);
                while (match.find()) {
                    properties.setProperty(match.group(1).replace("@", ""), match.group(2).trim());
                }
            }
            return properties;
        }
    }
}
