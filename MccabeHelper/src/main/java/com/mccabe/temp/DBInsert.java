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
import static com.mccabe.util.KyoboUtil.REPORT_TABLE;

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
            log("Insert / Update Done.");
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                preparedStatement.close();
                connection.close();
            } catch (SQLException e) {
                throw e;
            }
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
                        String n = e[0].substring(e[0].indexOf(".") + 1, e[0].length());
                        n = n.replace("%%", ",");
                        log("=line : " + line);
                        log("=Get " + MCCABERoleSet.convert(n));
                        Properties p = methodContent.get(MCCABERoleSet.convert(n));
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
            // get covered txt
            log("[File Get] : " + reportPath + ".txt");
            List<String> list = Files.readAllLines(Paths.get(reportPath + ".txt"));
            int index = 0;
            List<Properties> temp = new ArrayList<>();
            for (; index < list.size(); index++)
                if (list.get(index).startsWith("   A"))
                    break;
            for (; index < list.size(); index++) {
                if (list.get(index).length() == 0) {
                    index++;
                    break;
                }
                if (list.get(index).contains(className)) {
                    String [] raw = list.get(index).split("\\s+");
                    for (String n : raw) {
                        if (n.contains(className)) {
                            String method = n.substring(n.indexOf(".") + 1, n.length());
                            temp.add(methodContent.get(method));
                            methodContent.get(method).setProperty(REPORT_TABLE.START_LINE.name(), raw[raw.length - 2]);
                            methodContent.get(method).setProperty(REPORT_TABLE.NUM_OF_LINE.name(), raw[raw.length - 1]);
                            break;
                        }
                    }
                }
            }

            String code = "";
            for (int i = 0; index < list.size(); index++) {
                String v = list.get(index);
                if (v.length() == 0)
                    continue;
                int start = Integer.parseInt(temp.get(i).getProperty(REPORT_TABLE.START_LINE.name()));
                int end = start + Integer.parseInt(temp.get(i).getProperty(REPORT_TABLE.NUM_OF_LINE.name())) - 1;
                if (v.startsWith(String.valueOf(start))) {
                    while (!list.get(index).startsWith(String.valueOf(end))) {
                        code+=list.get(index++) + "\n";
                    }
                    code+=list.get(index) + "\n";
                    temp.get(i++).setProperty(REPORT_TABLE.CODES.name(), code);
                    code = "";
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
                n+= t + ",";
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
