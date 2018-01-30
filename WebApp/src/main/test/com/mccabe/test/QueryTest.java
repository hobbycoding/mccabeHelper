package com.mccabe.test;

import com.mccabe.rest.Query;
import org.json.simple.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class QueryTest {
    public static void main(String[] args) throws Exception {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@testmine.iptime.org:8021:orcl","testmine","testmine");
        JSONObject object = new JSONObject();
        object.put("file_package", "kv3.ba0.cp.cpbc.acccdmgt.srgyimmgtcpbi");
        object.put("job_name", "퇴직보험사무처리");
        object.put("from", "2017-12-01");
        object.put("to", "2018-01-30");
        object.put("order", "1");
        Statement statement = connection.createStatement();
        String query1 = Query.getChartView(object);
        String query2 = "SELECT FILE_DATE label, ROUND(AVG(COV_COVERAGE), 2) data FROM REPORT_TABLE WHERE FILE_PACKAGE='kv3.ba0.cp.cpbc.acccdmgt.srgyimmgtcpbi' AND JOB_NAME='퇴직보험사무처리' AND FILE_DATE >= '2017-12-01' AND FILE_DATE <= '2018-01-30' GROUP BY FILE_DATE";
        ResultSet resultSet = statement.executeQuery(query2);
        while (resultSet.next()) {
            System.out.println(resultSet.getString("label") + " : " + resultSet.getString("data"));
        }
        connection.close();
    }
}
