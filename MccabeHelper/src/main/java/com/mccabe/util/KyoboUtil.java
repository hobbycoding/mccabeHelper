package com.mccabe.util;

import com.mccabe.McCabeConfig;
import com.mccabe.temp.DBInsert;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.StringReader;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class KyoboUtil {

    public static final String REPORT_QUERY = "MERGE INTO REPORT_TABLE T1 " +
            "USING (" +
            "  SELECT ? FILE_DATE, ? FILE_NAME, ? FILE_PACKAGE, ? FILE_NAME_KO, ? FUNTION_NAME, ? FUNTION_NAME_KO, " +
            "    ? SERVICE_ID, ? JOB_NAME, ? JOB_CATEGORY, ? SYSTEM_ID, ? MANAGER, ? FILE_TYPE, ? COV_CODE_LINE, ? COV_COVERED_LINE, " +
            "    ? COV_COVERAGE, ? BRANCH_CODE_LINE, ? BRANCH_COVERED_LINE, ? BRANCH_COVERAGE, ? START_LINE, ? NUM_OF_LINE, empty_clob() CODES " +
            "  FROM dual) T2 " +
            "ON (T1.FILE_NAME = T2.FILE_NAME AND T1.FUNTION_NAME = T2.FUNTION_NAME AND T1.FILE_DATE = T2.FILE_DATE ) " +
            "WHEN MATCHED THEN " + "UPDATE SET " +
            "  FUNTION_NAME_KO = T2.FUNTION_NAME_KO," +
            "  SERVICE_ID = T2.SERVICE_ID," +
            "  JOB_NAME = T2.JOB_NAME," +
            "  JOB_CATEGORY = T2.JOB_CATEGORY," +
            "  SYSTEM_ID = T2.SYSTEM_ID," +
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
            "        JOB_NAME, JOB_CATEGORY, SYSTEM_ID, MANAGER, FILE_TYPE, COV_CODE_LINE, COV_COVERED_LINE, COV_COVERAGE, " +
            "        BRANCH_CODE_LINE, BRANCH_COVERED_LINE, BRANCH_COVERAGE, START_LINE, NUM_OF_LINE, CODES)" +
            " VALUES (T2.FILE_DATE, T2.FILE_PACKAGE, T2.FILE_NAME, T2.FILE_NAME_KO, T2.FUNTION_NAME, T2.FUNTION_NAME_KO, T2.SERVICE_ID," +
            " T2.JOB_NAME, T2.JOB_CATEGORY, T2.SYSTEM_ID, T2.MANAGER, T2.FILE_TYPE, T2.COV_CODE_LINE, T2.COV_COVERED_LINE, T2.COV_COVERAGE, " +
            " T2.BRANCH_CODE_LINE, T2.BRANCH_COVERED_LINE, T2.BRANCH_COVERAGE, T2.START_LINE, T2.NUM_OF_LINE, T2.CODES)";
    public static final String SELECT_CHECK_QUERY = "SELECT count(*) cnt FROM REPORT_TABLE WHERE FILE_DATE = '{date}'";
    public static final String UPDATE_REPORT_QUERY = "UPDATE REPORT_TABLE SET FILE_DATE = '{new}' WHERE FILE_DATE = '{old}'";
    public static final String SELECT_PACKAGE_NAME = "SELECT PACKAGE_NAME, PACKAGE_NAME_KO, SYSTEM_ID FROM PACKAGE_NAME";
    public static final String UPDATE_CODES_QUERY = "UPDATE REPORT_TABLE SET CODES = ? WHERE FILE_DATE = ? AND FILE_NAME = ? AND FUNTION_NAME = ?";

    public static synchronized void putInsertQueryInPrepared(DBInsert.SourceFile sourceFile, PreparedStatement preparedStatement) throws Exception {
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
                preparedStatement.setString(9, sourceFile.pakageName_ko); // JOB_CATEGORY
                preparedStatement.setString(10, sourceFile.system_id); // SYSTEM_ID
                preparedStatement.setString(11, ""); // MANAGER
                preparedStatement.setString(12, sourceFile.getClassContent().getProperty(REPORT_TABLE.FILE_TYPE.name(), "NONE")); // FILE_TYPE
                preparedStatement.setInt(13, Integer.parseInt(entry.getValue().getProperty(REPORT_TABLE.COV_CODE_LINE.name(), "0"))); // COV_CODE_LINE
                preparedStatement.setInt(14, Integer.parseInt(entry.getValue().getProperty(REPORT_TABLE.COV_COVERED_LINE.name(), "0"))); // COV_COVERED_LINE
                preparedStatement.setFloat(15, Float.parseFloat(entry.getValue().getProperty(REPORT_TABLE.COV_COVERAGE.name(), "0"))); // COV_COVERAGE
                preparedStatement.setInt(16, Integer.parseInt(entry.getValue().getProperty(REPORT_TABLE.BRANCH_CODE_LINE.name(), "0"))); // BRANCH_CODE_LINE
                preparedStatement.setInt(17, Integer.parseInt(entry.getValue().getProperty(REPORT_TABLE.BRANCH_COVERED_LINE.name(), "0"))); // BRANCH_COVERED_LINE
                preparedStatement.setFloat(18, Float.parseFloat(entry.getValue().getProperty(REPORT_TABLE.BRANCH_COVERAGE.name(), "0"))); // BRANCH_COVERAGE
                preparedStatement.setInt(19, Integer.parseInt(entry.getValue().getProperty(REPORT_TABLE.START_LINE.name(), "0"))); // START_LINE
                preparedStatement.setInt(20, Integer.parseInt(entry.getValue().getProperty(REPORT_TABLE.NUM_OF_LINE.name(), "0"))); // NUM_OF_LINE
                preparedStatement.execute();
                preparedStatement.clearParameters();
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public static void updateCodes(DBInsert.SourceFile sourceFile, PreparedStatement preparedStatement) throws SQLException {
        for (Map.Entry<String, Properties> entry : sourceFile.getMethodContent().entrySet()) {
            String data = entry.getValue().getProperty(REPORT_TABLE.CODES.name(), "");
            preparedStatement.setCharacterStream(1, new StringReader(data)); // CODES
            preparedStatement.setString(2, sourceFile.date);
            preparedStatement.setString(3, sourceFile.className);
            preparedStatement.setString(4, entry.getKey());
            preparedStatement.execute();
            preparedStatement.clearParameters();
        }
    }

    public static Map<String, List<String>> getCategoryNameFromDB(Connection connection) throws SQLException {
        Map<String, List<String>> map = new HashMap<>();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(SELECT_PACKAGE_NAME);
        while (resultSet.next()) {
            List<String> obj = new ArrayList<>();
            obj.add(0, resultSet.getString(PACKAGE_NAME.PACKAGE_NAME_KO.name()));
            obj.add(1, resultSet.getString(3) == null ? "" : resultSet.getString(3));
            map.put(resultSet.getString(PACKAGE_NAME.PACKAGE_NAME.name()), obj);
        }
        statement.close();
        return map;
    }

    public static boolean isHaveYesterdayData(Connection connection) throws SQLException {
        if (todayIs(Calendar.MONDAY))
            return false;
        Statement statement = connection.createStatement();
        try {
            ResultSet resultSet = statement.executeQuery(SELECT_CHECK_QUERY.replace("{date}", getDate(-1)));
            while (resultSet.next()) {
                if (resultSet.getInt("cnt") > 0) {
                    return true;
                }
                return false;
            }
        } finally {
            statement.close();
        }
        return false;
    }

    public static String getDate() {
        return getDate(0);
    }

    public static String getDate(int day) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, day);
        return dateFormat.format(cal.getTime());
    }

    public static boolean todayIs(int day) {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.DAY_OF_WEEK) == day;
    }

    public static void updateYesterdayData(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        try {
            statement.executeQuery(UPDATE_REPORT_QUERY.replace("{old}", getDate(-1)).replace("{new}", getDate()));
        } finally {
            statement.close();
        }
    }

    public enum REPORT_TABLE {
        FILE_NAME, FILE_DATE, FILE_NAME_KO, FUNTION_NAME, FUNTION_NAME_KO, SERVICE_ID, JOB_NAME, JOB_CATEGORY, SYSTEM_ID, MANAGER, FILE_TYPE,
        COV_CODE_LINE, COV_COVERED_LINE, COV_COVERAGE, BRANCH_CODE_LINE, BRANCH_COVERED_LINE, BRANCH_COVERAGE, START_LINE, NUM_OF_LINE, TESTED_LINE, CODES
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
        setCategory(fullPath, tags);
    }

    // this role is made by kyobo.
    //fullPath    ->  2.시스템명세모델::03.프로세스컴포넌트::퇴직보험사무처리::급부::퇴직보험거치Pbi::퇴직보험거치Pbi
    //fullPath    ->  2.시스템명세모델::06.배치::회계분개허브::구ERP분개허브파일송신Job::구ERP분개허브파일송신Auto
    //JOB_NAME    -> 프로세스컴포넌트
    //FILE_TYPE   -> PBC
    public static void setCategory(String raw, Properties tags) {
        String[] splite = raw.split("::");
        String desc = "";
        Category selected = Category.NONE;
        try {
            if (splite.length > 2) {
                if (splite[1].contains("배치")) {
                    String v = splite[2];
                    if (splite[splite.length - 1].contains("Auto"))
                        selected = Category.AUTO;
                    else selected = Category.MANUAL;
                    desc = v;
                } else {
                    String v = splite[1].substring(splite[1].indexOf(".") + 1, splite[1].length());
                    for (Category category : Category.values()) {
                        if (category.isEquals(v))
                            selected = category;
                    }
                    desc = splite[2];
                }
            }
        } catch (Exception ignore) {
            McCabeConfig.log("setCategory error. skip category. ->" + raw);
        }
        tags.setProperty(REPORT_TABLE.FILE_TYPE.name(), selected.name());
        tags.setProperty(REPORT_TABLE.JOB_NAME.name(), desc);
    }

    public static JSONArray getMatchedFiles(JSONArray fileList, JSONArray others, Properties properties) throws ParseException {
        JSONArray nFileList = new JSONArray();
        JSONArray selected = (JSONArray) new JSONParser().parse(properties.getProperty("selected"));
        for (Object o : fileList) {
            for (Object j : selected) {
                if (o.toString().startsWith(j.toString())) {
                    nFileList.add(o);
                } else if (others != null) {
                    others.add(o);
                }
            }
        }
        return nFileList;
    }
}
