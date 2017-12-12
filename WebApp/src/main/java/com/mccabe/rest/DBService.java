package com.mccabe.rest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.sql.*;

public class DBService {

    public enum Category {
        SYSTEM_ID("시스템"), PACKAGE_NAME_KO("업무분류"), TOTAL_PROGRAM("전체Program"), PROGRAM_TESTED("테스트된 Program"),
        OVER_COVERAGE("80% 이상 Program"), UNDER_COVERAGE("80% 미만 Program"), TOTAL_FUNC_CNTS("전체 Funtion"),
        FUN_TESTED("테스트된 Function"), COVERAGE("COVERAGE"), UNTESTED("미테스트 Program");
        private final String desc;

        Category(String desc) {
            this.desc = desc;
        }

        public boolean isEquals(String desc) {
            return this.desc.equals(desc) ? true : false;
        }
    }
    // property : method [getOverView, detailView, getExcel], where [ex)2017-12-11],
    // ex) {"method" : "getOverView", "where" : "2017-12-11"}
    public String doProcess(String msg) {
        JSONArray result = null;
        try {
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(msg);
            switch (jsonObject.get("method").toString()) {
                case "getOverView" :
                    result = getOverView(jsonObject);
                    break;
                case "detailView" :
                    result = getDetailView(jsonObject);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    private JSONArray getDetailView(JSONObject jsonObject) throws Exception {
        return null;
    }

    private JSONArray getOverView(JSONObject jsonObject) throws Exception {
        Statement statement = importStatementAfterConnect();
        String query = Query.getOverView(jsonObject.get("where").toString());
        ResultSet resultSet = statement.executeQuery(query);
        return mashalingJSON(resultSet);
    }

    private Statement importStatementAfterConnect() throws Exception {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@testmine.iptime.org:1521:orcl",
                "testmine", "testmine");
        return connection.createStatement();
    }

    private JSONArray mashalingJSON(ResultSet rs) throws SQLException {
        JSONArray json = new JSONArray();
        ResultSetMetaData rsmd = rs.getMetaData();
        while(rs.next()) {
            int numColumns = rsmd.getColumnCount();
            JSONObject obj = new JSONObject();
            for (int i=1; i<=numColumns; i++) {
                String column_name = rsmd.getColumnName(i);
                obj.put(Category.valueOf(column_name).desc, rs.getObject(column_name));
            }
            json.add(obj);
        }
        return json;
    }
}
