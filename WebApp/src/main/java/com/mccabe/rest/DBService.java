package com.mccabe.rest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.*;

public class DBService {

    // property : method [getOverView, detailView, getExcel], where [ex)2017-12-11],
    // ex) {"method" : "getOverView", "where" : "2017-12-11"}
    public String doProcess(String msg) {
        JSONArray result = null;
        try {
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(msg);
            switch (jsonObject.get("method").toString()) {
                case "getOverView":
                    result = getDataFromTable(Query.getOverView(jsonObject.get("where").toString()));
                    break;
                case "getCategoryList":
                    result = getDataFromTable(Query.getCategoryList(jsonObject.get("where").toString()));
                    break;
                case "getSubDetailView":
                    result = getDataFromTable(Query.getSubDetailView(jsonObject.get("where").toString(), jsonObject.get("category").toString()));
                    break;
                case "detailView":
                    result = getDataFromTable(Query.getDetailView(jsonObject.get("where")));
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    private JSONArray getDataFromTable(String query) throws Exception {
        Statement statement = getConnection();
        ResultSet resultSet = statement.executeQuery(query);
        JSONArray result = mashalingJSON(resultSet);
        statement.getConnection().close();
        return result;
    }

    private Statement getConnection() throws Exception {
        String ejb = "mccabe/oracle";
        String normal = "java:comp/env/mccabe/oracle";
        final Context initContext = new InitialContext();
        DataSource ds = (DataSource) initContext.lookup(normal);
        if (ds != null) {
            return ds.getConnection().createStatement();
        }
        throw new Exception("can't find mccabe/oracle.");
    }

    private JSONArray mashalingJSON(ResultSet rs) throws SQLException {
        JSONArray json = new JSONArray();
        ResultSetMetaData rsmd = rs.getMetaData();
        while (rs.next()) {
            int numColumns = rsmd.getColumnCount();
            JSONObject obj = new JSONObject();
            for (int i = 1; i <= numColumns; i++) {
                String column_name = rsmd.getColumnName(i);
                obj.put(Query.Category.valueOf(column_name).getDesc(), rs.getObject(column_name));
            }
            json.add(obj);
        }
        return json;
    }
}
