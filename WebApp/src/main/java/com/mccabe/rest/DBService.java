package com.mccabe.rest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.sql.*;

public class DBService {

    // property : method [getOverView, detailView, getExcel], where [ex)2017-12-11],
    // ex) {"method" : "getOverView", "where" : "2017-12-11"}
    public String doProcess(String msg) {
        Object result = null;
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) new JSONParser().parse(msg);
            switch (jsonObject.get("method").toString()) {
                case "getOverView":
                    result = getDataFromTable(Query.getOverView(jsonObject.get("where").toString()));
                    break;
                case "getCategoryList":
                    result = getDataFromTable(Query.getCategoryList(jsonObject.get("where").toString()));
                    break;
                case "getSubDetailView":
                    result = getDataFromTable(Query.getSubDetailView(jsonObject));
                    break;
                case "detailView":
                    result = getDataFromTable(Query.getDetailView(jsonObject.get("where")));
                    break;
                case "getJobList":
                    result = getDataFromTable(Query.getJoblist(jsonObject));
                    break;
                case "getJobListTable":
                    result = getDataFromTable(Query.getJoblistTable(jsonObject));
                    break;
                case "getCodes":
                    result = getCLOBFromTable(Query.getJoblistTable(jsonObject));
                    break;
                case "getChartView":
                    result = getDataFromTable(Query.getChartView(jsonObject), true);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    private synchronized JSONArray getCLOBFromTable(String query) throws Exception {
        JSONArray jsonArray = new JSONArray();
        JSONObject object = new JSONObject();
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        try {
            while (resultSet.next()) {
                StringBuffer strOut = new StringBuffer();
                String aux;
                try {
                    BufferedReader br = new BufferedReader(resultSet.getClob("CODES").
                            getCharacterStream());
                    while ((aux = br.readLine()) != null) {
                        strOut.append(aux);
                        strOut.append(System.getProperty("line.separator"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String clobStr = strOut.toString();
                object.put("CODES", clobStr);
                jsonArray.add(object);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            close(connection, statement, resultSet);
        }
        return jsonArray;
    }

    private Object getDataFromTable(String query) throws Exception {
        return getDataFromTable(query, false);
    }

    private Object getDataFromTable(String query, boolean direct) throws Exception {
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        try {
            Object result;
            if (!direct) {
                result = marshallingJSON(resultSet);
            } else {
                result = createJSONObject(resultSet);
            }
            return result;
        } catch (Exception e) {
            throw e;
        } finally {
            close(connection, statement, resultSet);
        }
    }

    private void close(Connection connection, Statement statement, ResultSet resultSet) throws SQLException {
        try {
            if (statement != null) {
                statement.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            throw e;
        }
    }

    private Connection getConnection() throws Exception {
        String ejb = "mccabe/oracle";
        String normal = "java:comp/env/mccabe/oracle";
        final Context initContext = new InitialContext();
        DataSource ds = (DataSource) initContext.lookup(normal);
        if (ds != null) {
            return ds.getConnection();
        }
        throw new Exception("can't find mccabe/oracle.");
    }

    private Object createJSONObject(ResultSet resultSet) throws SQLException {
        JSONObject result = new JSONObject();
        JSONArray label = new JSONArray();
        JSONArray data = new JSONArray();
        while (resultSet.next()) {
            label.add(resultSet.getString("label"));
            data.add(resultSet.getInt("data"));
        }
        result.put("label", label);
        result.put("data", data);
        return result;
    }

    private JSONArray marshallingJSON(ResultSet rs) throws SQLException {
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
