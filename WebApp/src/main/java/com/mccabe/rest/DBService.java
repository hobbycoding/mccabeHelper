package com.mccabe.rest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DBService {

    public String doProcess(String msg) {
        try {
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(msg);
            JSONArray result = new JSONArray();
            switch (jsonObject.get("").toString()) {
                case "getOverView" :
                    result = getOverView(jsonObject);
                    break;
                case "detailView" :
                    result = getDetailView(jsonObject);
                    break;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONArray getDetailView(JSONObject jsonObject) {
        return null;
    }

    private JSONArray getOverView(JSONObject jsonObject) {
        return null;
    }
}
