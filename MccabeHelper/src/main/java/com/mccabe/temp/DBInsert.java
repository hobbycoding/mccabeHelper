package com.mccabe.temp;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.mccabe.McCabeConfig;
import com.mccabe.util.FileUtil;
import com.mccabe.util.KyoboUtil;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.mccabe.util.KyoboUtil.REPORT_QUERY;
import static com.mccabe.util.KyoboUtil.TAG;

public class DBInsert extends McCabeConfig {
    private Connection connection = null;
    private PreparedStatement preparedStatement;

    public DBInsert(Properties properties) {
        super(properties);
    }

    public static void main(String[] args) throws Exception {
        DBInsert dbInsert = new DBInsert(changeProperties(args));
        dbInsert.start();
    }

    public void start() {
        try {
            connectDB();
            List<File> fileList = getFileList();
            for (File file : fileList) {
                SourceFile sourceFile = new SourceFile(file);
                sourceFile.parse();
                KyoboUtil.insertDB(sourceFile, preparedStatement);
            }
            executeQuery();
        } catch (Exception e) {
            log(e.getMessage());
            e.printStackTrace();
        }
    }

    private void executeQuery() throws SQLException {
        try {
            preparedStatement.executeBatch();
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                preparedStatement.close();
                connection.close();
            } catch (SQLException e) {
                throw e;
            }
            log("Insert / Update Done.");
        }
    }

    private void connectDB() throws Exception {
        Class.forName(property.getProperty("JDBC_Driver"));
        connection = DriverManager.getConnection(property.getProperty("db_url"),
                property.getProperty("db_id"), property.getProperty("db_pass"));
        preparedStatement = connection.prepareStatement(REPORT_QUERY);
    }

    private List<File> getFileList() throws Exception {
        List<File> result = new ArrayList<>();
        Path fileList_json = Paths.get(PROJECT_DIR + fs + property.getProperty("programName") + fs + FILE_LIST_JSON);
        JSONArray fileList = null;
        if (Files.exists(fileList_json)) {
            fileList = (JSONArray) new JSONParser().parse(new String(Files.readAllBytes(fileList_json), "UTF-8"));
        } else {
            for (File file : FileUtil.getFilesRecursive(new File(property.getProperty("srcDir")), "", "", ".java", 0)) {
                fileList.add(FileUtil.getRoleFileName(file, property.getProperty("srcDir")));
            }
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
        private Map<String, Properties> methodContent = new HashMap<>();
        private Properties classContent = new Properties();
        private File file;
        public String className;
        public String packageName;
        public String date;

        public SourceFile(File file) {
            this.file = file;
        }

        public void parse() throws Exception {
            CompilationUnit cu = JavaParser.parse(file);
            packageName = cu.getPackage() != null ? cu.getPackage().getPackageName() : "";
            new MethodVisitor().visit(cu, this);
            if (!file.getName().equals(className.concat(".java")))
                log("file : [" + file.getName() + "] and class [" + className + "] are not equal. use class.");
            //report file parse
            String reportPath = REPORT_DIR + fs + property.getProperty("programName") + fs + property.getProperty("programName") + "_" + FileUtil.getRoleFileName(file, property.getProperty("srcDir"));
            for (String kind : REPORT_KIND) {
                log("[File Get] : " + reportPath + "_" + kind + ".csv");
                List<String> list = Files.readAllLines(Paths.get(reportPath + "_" + kind + ".csv"));
                setDate(list.get(0));
                for (String line : list) {
                    if (StringUtils.countMatches(line, ",") >= 3 && line.contains(".") && !line.startsWith("Total") && !line.startsWith("Average")) {
                        if (line.startsWith("\"")) {
                            String sub = line.substring(line.indexOf("\""), line.lastIndexOf("\""));
                            line = line.replace(sub, sub.replace(",", "%%"));
                        }
                        String[] e = line.split(",");
                        String n = e[0].split("\\.")[1];
                        Properties p = methodContent.get(n.substring(0, n.indexOf("(")));
                        String k = kind;
                        if (k.equals("codecov")) {
                            k = "COV";
                        } else k = "BRANCH";
                        p.setProperty(k + "_CODE_LINE", e[1]);
                        p.setProperty(k + "_COVERED_LINE", e[2]);
                        p.setProperty(k + "_COVERAGE", e[3]);
                    }
                }
            }
        }

        private void setDate(String date) {
            String[] s = date.split("/");
            this.date = "20" + s[2] + "-" + s[0] + "-" + s[1];
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
            ((SourceFile) arg).methodContent.put(n.getName(), createContentFromProperties(getTags(content)));
            super.visit(n, arg);
        }

        @Override
        public void visit(MethodDeclaration m, Object arg) {
            String content = m.getJavaDoc() != null ? m.getJavaDoc().getContent() : null;
            ((SourceFile) arg).methodContent.put(m.getName(), createContentFromProperties(getTags(content)));

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
