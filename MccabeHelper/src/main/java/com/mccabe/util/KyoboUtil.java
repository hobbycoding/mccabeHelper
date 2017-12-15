package com.mccabe.util;

import com.mccabe.McCabeConfig;
import com.mccabe.temp.DBInsert;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.sql.*;
import java.util.*;

public class KyoboUtil {

    public static final String REPORT_QUERY = "MERGE INTO REPORT_TABLE T1 " +
            "USING (" +
            "  SELECT ? FILE_DATE, ? FILE_NAME, ? FILE_PACKAGE, ? FILE_NAME_KO, ? FUNTION_NAME, ? FUNTION_NAME_KO, " +
            "    ? SERVICE_ID, ? JOB_NAME, ? MANAGER, ? FILE_TYPE, ? COV_CODE_LINE, ? COV_COVERED_LINE, " +
            "    ? COV_COVERAGE, ? BRANCH_CODE_LINE, ? BRANCH_COVERED_LINE, ? BRANCH_COVERAGE, ? START_LINE, ? NUM_OF_LINE, ? CODES " +
            "  FROM dual) T2 " +
            "ON (T1.FILE_NAME = T2.FILE_NAME AND T1.FUNTION_NAME = T2.FUNTION_NAME AND T1.FILE_DATE = T2.FILE_DATE ) " +
            "WHEN MATCHED THEN " + "UPDATE SET " +
            "  MANAGER = T2.MANAGER," +
            "  COV_CODE_LINE = T2.COV_CODE_LINE," +
            "  COV_COVERED_LINE = T2.COV_COVERED_LINE," +
            "  COV_COVERAGE = T2.COV_COVERAGE, " +
            "  BRANCH_CODE_LINE = T2.BRANCH_CODE_LINE," +
            "  BRANCH_COVERED_LINE = T2.BRANCH_COVERED_LINE," +
            "  START_LINE = T2.START_LINE, " +
            "  NUM_OF_LINE = T2.NUM_OF_LINE, " +
            "  CODES = T2.CODES " +
            "WHEN NOT MATCHED THEN " +
            " INSERT (FILE_DATE, FILE_PACKAGE, FILE_NAME, FILE_NAME_KO, FUNTION_NAME, FUNTION_NAME_KO, SERVICE_ID," +
            "        JOB_NAME, MANAGER, FILE_TYPE, COV_CODE_LINE, COV_COVERED_LINE, COV_COVERAGE, " +
            "        BRANCH_CODE_LINE, BRANCH_COVERED_LINE, BRANCH_COVERAGE, START_LINE, NUM_OF_LINE, CODES)" +
            " VALUES (T2.FILE_DATE, T2.FILE_PACKAGE, T2.FILE_NAME, T2.FILE_NAME_KO, T2.FUNTION_NAME, T2.FUNTION_NAME_KO, T2.SERVICE_ID," +
            " T2.JOB_NAME, T2.MANAGER, T2.FILE_TYPE, T2.COV_CODE_LINE, T2.COV_COVERED_LINE, T2.COV_COVERAGE, " +
            " T2.BRANCH_CODE_LINE, T2.BRANCH_COVERED_LINE, T2.BRANCH_COVERAGE, T2.START_LINE, T2.NUM_OF_LINE, T2.CODES)";
    public static final String SELECT_PACKAGE_NAME = "SELECT PACKAGE_NAME, PACKAGE_NAME_KO, SYSTEM_ID FROM PACKAGE_NAME";

    public static void putInsertQueryInPrepared(DBInsert.SourceFile sourceFile, PreparedStatement preparedStatement) throws Exception {
        try {
            for (Map.Entry<String, Properties> entry : sourceFile.getMethodContent().entrySet()) {
                preparedStatement.setString(1, sourceFile.date); // FILE_DATE
                preparedStatement.setString(2, sourceFile.className); // FILE_NAME
                preparedStatement.setString(3, sourceFile.packageName); // FILE_PACKAGE
                preparedStatement.setString(4, sourceFile.getClassContent().getProperty(TAG.logicalName.name(), "")); // FILE_NAME_KO
                preparedStatement.setString(5, entry.getKey()); // FUNTION_NAME
                preparedStatement.setString(6, entry.getValue().getProperty((TAG.logicalName.name()), "")); // FUNTION_NAME_KO
                preparedStatement.setString(7, entry.getValue().getProperty(TAG.serviceID.name(), "")); // SERVICE_ID
                preparedStatement.setString(8, sourceFile.getClassContent().getProperty(REPORT_TABLE.JOB_NAME.name(), "")); // JOB_NAME
                preparedStatement.setString(9, ""); // MANAGER
                preparedStatement.setString(10, sourceFile.getClassContent().getProperty(REPORT_TABLE.FILE_TYPE.name(), "0")); // FILE_TYPE
                preparedStatement.setInt(11, Integer.parseInt(entry.getValue().getProperty(REPORT_TABLE.COV_CODE_LINE.name(), "0"))); // COV_CODE_LINE
                preparedStatement.setInt(12, Integer.parseInt(entry.getValue().getProperty(REPORT_TABLE.COV_COVERED_LINE.name(), "0"))); // COV_COVERED_LINE
                preparedStatement.setInt(13, Integer.parseInt(entry.getValue().getProperty(REPORT_TABLE.COV_COVERAGE.name(), "0"))); // COV_COVERAGE
                preparedStatement.setInt(14, Integer.parseInt(entry.getValue().getProperty(REPORT_TABLE.BRANCH_CODE_LINE.name(), "0"))); // BRANCH_CODE_LINE
                preparedStatement.setInt(15, Integer.parseInt(entry.getValue().getProperty(REPORT_TABLE.BRANCH_COVERED_LINE.name(), "0"))); // BRANCH_COVERED_LINE
                preparedStatement.setInt(16, Integer.parseInt(entry.getValue().getProperty(REPORT_TABLE.BRANCH_COVERAGE.name(), "0"))); // BRANCH_COVERAGE
                preparedStatement.setInt(17, Integer.parseInt(entry.getValue().getProperty(REPORT_TABLE.START_LINE.name(), "0"))); // START_LINE
                preparedStatement.setInt(18, Integer.parseInt(entry.getValue().getProperty(REPORT_TABLE.NUM_OF_LINE.name(), "0"))); // NUM_OF_LINE
                String data = entry.getValue().getProperty(REPORT_TABLE.CODES.name(), "");
                Clob clob = preparedStatement.getConnection().createClob();
                clob.setString(1, data);
                preparedStatement.setClob(19, clob); // CODES
                preparedStatement.addBatch();
                preparedStatement.clearParameters();
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public static Map<String, List<String>> getCategoryNameFromDB(Statement statement) throws SQLException {
        Map<String, List<String>> map = new HashMap<>();
        ResultSet resultSet = statement.executeQuery(SELECT_PACKAGE_NAME);
        while (resultSet.next()) {
            List<String> obj = new ArrayList<>();
            obj.add(0, resultSet.getString(PACKAGE_NAME.PACKAGE_NAME_KO.name()));
            obj.add(1, resultSet.getString(3) == null ? "" : resultSet.getString(3));
            map.put(resultSet.getString(PACKAGE_NAME.PACKAGE_NAME.name()), obj);
        }
        return map;
    }


    public enum REPORT_TABLE {
        FILE_NAME, FILE_DATE, FILE_NAME_KO, FUNTION_NAME, FUNTION_NAME_KO, SERVICE_ID, JOB_NAME, JOB_CATEGORY, MANAGER, FILE_TYPE,
        COV_CODE_LINE, COV_COVERED_LINE, COV_COVERAGE, BRANCH_CODE_LINE, BRANCH_COVERED_LINE, BRANCH_COVERAGE, START_LINE, NUM_OF_LINE, CODES;
    }

    public enum PACKAGE_NAME {
        PACKAGE_NAME, PACKAGE_NAME_KO, SYSYTEM_ID
    }

    public enum TAG {
        logicalName, version, serviceID, modelVersion, modelProject, fullPath
    }

    public enum Category {
        CPBC("공통프로세스컴포넌트"), PBC("프로세스컴포넌트"), AUTO("Auto"), MANUAL("Manual"), NONE("None");
        private final String desc;

        Category(String desc) {
            this.desc = desc;
        }

        public boolean isEquals(String desc) {
            return this.desc.equals(desc) ? true : false;
        }
    }

    public static void createContent(Properties tags) {
        String fullPath = tags.getProperty(TAG.fullPath.name());
        Category category = getCategory(fullPath);
        tags.setProperty(REPORT_TABLE.JOB_NAME.name(), category.desc);
        tags.setProperty(REPORT_TABLE.FILE_TYPE.name(), category.name());
    }

    // this role is made by kyobo.
    //fullPath    ->  2.시스템명세모델::03.프로세스컴포넌트::퇴직보험사무처리::급부::퇴직보험거치Pbi::퇴직보험거치Pbi
    public static Category getCategory(String raw) {
        String[] splite = raw.split("::");
        String v = splite[1].substring(splite[1].indexOf(".") + 1, splite[1].length());
        for (Category category : Category.values()) {
            if (category.isEquals(v))
                return category;
        }
        return Category.NONE;
    }
}
