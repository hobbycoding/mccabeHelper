package com.mccabe.temp;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.mccabe.McCabeConfig;
import com.mccabe.util.FileUtil;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DBInsert extends McCabeConfig {


    public DBInsert(Properties properties) {
        super(properties);
    }

    public static void main(String[] args) throws Exception {
        DBInsert dbInsert = new DBInsert(changeProperties(args));
        dbInsert.start();
    }

    public void start() {
        try {
            List<File> fileList = getFileList();
            for (File file : fileList) {
                parseFile(file);

            }
        } catch (Exception e) {
            log(e.getMessage());
            e.printStackTrace();
        }
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
            result.add(new File(PROJECT_DIR + fs + property.getProperty("programName") + object.toString().replace("_", fs) + ".java"));
        }
        return result;
    }

    private void parseFile(File file) throws Exception {
        SourceFile parser = new SourceFile();
    }

    class SourceFile {
        private Map<String, Properties> fileContent = new HashMap<>();
        private File file;
        private String className;
        public SourceFile(File file) {
            this.file = file;
        }

        public void parse() throws IOException, ParseException {
            File file = new File("C:\\Users\\actuator\\Desktop\\RtisDfrmPbc.java");
            CompilationUnit cu = JavaParser.parse(file);
            new MethodVisitor().visit(cu, this);
        }

        public Map<String, Properties> getFileContent() {
            return fileContent;
        }

        public void setClassName(String className) {
            this.className = className;
        }
    }

    private class MethodVisitor extends VoidVisitorAdapter {
        private static final String REGEX = "(@\\p{Alnum}+)(.*)";

        @Override
        public void visit(ClassOrInterfaceDeclaration n, Object arg) {
            ((SourceFile) arg).setClassName(n.getName());
            super.visit(n, arg);
        }

        @Override
        public void visit(MethodDeclaration m, Object arg) {
            ((SourceFile) arg).getFileContent().put(m.getName(), getTags(m.getJavaDoc().getContent()));
        }

        private Properties getTags(String raw) {
            Pattern pattern = Pattern.compile(REGEX);
            Matcher match = pattern.matcher(raw);
            Properties properties = new Properties();
            while (match.find()) {
                properties.setProperty(match.group(1).replace("@", ""), match.group(2).trim());
            }
            return properties;
        }
    }
}
