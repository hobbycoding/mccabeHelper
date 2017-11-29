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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DBInsert extends McCabeConfig {
    private static final String REGEX = "(@\\p{Alnum}+)(.*)";
    private static Properties properties;

    public DBInsert() {

    }

    public static void main(String[] args) throws Exception {
        properties = changeProperties(args);
        File projectFolder = new File(PROJECT_DIR);
        Path fileList_json = Paths.get(projectFolder + fs + properties.getProperty("programName") + fs + "fileList.json");
        JSONArray fileList = null;
        if (Files.exists(fileList_json)) {
            fileList = (JSONArray) new JSONParser().parse(new String(Files.readAllBytes(fileList_json), "UTF-8"));
        } else {
            for (File file : FileUtil.getFilesRecursive(new File(properties.getProperty("srcDir")), "", "", ".java", 0)) {
                fileList.add(FileUtil.getRoledFileName(file, properties.getProperty("srcDir")));
            }
        }
        FileParser parser = new FileParser()
    }

    public void parse() {

    }

    class FileParser {
        private List<MethodDeclaration> contents = new ArrayList<>();

        public FileParser(File file) {

        }

        public void parse() throws IOException, ParseException {
            File file = new File("C:\\Users\\actuator\\Desktop\\RtisDfrmPbc.java");
            CompilationUnit cu = JavaParser.parse(file);
            new MethodVisitor().visit(cu, contents);
            for (MethodDeclaration method : contents) {
                System.out.println(method.getName());
                System.out.println(getTags(method.getJavaDoc().getContent()));
            }
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

    private static class MethodVisitor extends VoidVisitorAdapter {

        @Override
        public void visit(ClassOrInterfaceDeclaration n, Object arg) {
            System.out.println(n.getName());
            super.visit(n, arg);
        }

        @Override
        public void visit(MethodDeclaration m, Object arg) {
            ((ArrayList<MethodDeclaration>)arg).add(m);
        }
    }
}
