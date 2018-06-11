package com.mccabe.temp;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.mccabe.McCabeConfig;
import com.mccabe.util.KyoboUtil;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Properties;

import static com.mccabe.util.KyoboUtil.UPDATE_CODE_COVERED;

public class DeletesDataFromFile extends McCabeConfig {

    public DeletesDataFromFile(Properties properties) {
        super(properties);
    }

    public static void main(String[] args) throws Exception {
        DeletesDataFromFile deletesDataFromFile = new DeletesDataFromFile(changeProperties(args));
        deletesDataFromFile.start();
    }

    public void start() {
        try (Connection connection = createConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_CODE_COVERED)) {
            JSONArray jsonArray = (JSONArray) new JSONParser().parse(new FileReader(PROJECT_DIR + fs + PROGRAM_NAME + fs + FILE_LIST_JSON));
            int count = 0;
            for (Object object : jsonArray) {
                File file = new File(object.toString());
                CompilationUnit cu = JavaParser.parse(file);
                String packageName = cu.getPackage() != null ? cu.getPackage().getPackageName() : "";
                String fileName = file.getName().substring(0, file.getName().indexOf("."));
                preparedStatement.setString(1, KyoboUtil.getDate());
                preparedStatement.setString(2,packageName);
                preparedStatement.setString(3, fileName);
                count += preparedStatement.executeUpdate();
            }
            log("[Update] "+ count +" Done.");
            new File(PROJECT_DIR + fs + PROGRAM_NAME + fs + FILE_LIST_JSON).delete();
        } catch (Exception e) {
            log(e.getMessage());
            e.printStackTrace();
        }
    }

    private Connection createConnection() throws Exception {
        Class.forName(property.getProperty("JDBC_Driver"));
        return DriverManager.getConnection(property.getProperty("db_url"),
                property.getProperty("db_id"), property.getProperty("db_pass"));

    }
}
