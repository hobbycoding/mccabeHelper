package com.mccabe.rest;

import org.json.simple.JSONObject;

public class Query {
    private static final String OVERVIEW = "SELECT SYSTEM_ID, JOB_CATEGORY, COUNT(JOB_CATEGORY) TOTAL_PROGRAM, COUNT(CASE WHEN COVERAGE > 0 THEN 1 END ) TOTAL_TESTED,\n" +
            "                                   COUNT(CASE WHEN COVERAGE >= 80 THEN 1 END) OVER_COVERAGE, COUNT(CASE WHEN COVERAGE < 80 THEN 1 END) UNDER_COVERAGE,\n" +
            "                                   SUM(TOTAL_FUNC_CNT) TOTAL_FUNC_CNT, SUM(TESTED) FUNC_TESTED, SUM(NUM_OF_LINE) TOTAL_LINE, SUM(TESTED_LINE) TOTAL_TESTED_LINE,\n" +
            "                                   (SUM(COVERAGE) / COUNT(FILE_PACKAGE)) COVERAGE, (COUNT(JOB_CATEGORY) - COUNT(CASE WHEN COVERAGE > 0 THEN 1 END )) UNTESTED\n" +
            "FROM (SELECT SYSTEM_ID, JOB_CATEGORY, FILE_PACKAGE, FILE_NAME, COUNT(FILE_NAME) TOTAL_FUNC_CNT, COUNT(FILE_NAME) - COUNT(DECODE(COV_COVERAGE, 0, 1)) TESTED,\n" +
            "        SUM(COV_COVERAGE) / CASE WHEN (COUNT(FILE_NAME) - COUNT(DECODE(COV_COVERAGE, 0, 1))) <= 0 THEN 1 ELSE (COUNT(FILE_NAME) - COUNT(DECODE(COV_COVERAGE, 0, 1))) END COVERAGE,\n" +
            "        SUM(NUM_OF_LINE) NUM_OF_LINE,  SUM(TESTED_LINE) TESTED_LINE\n" +
            "      FROM (SELECT SYSTEM_ID, JOB_CATEGORY, FILE_PACKAGE, FILE_NAME, FUNTION_NAME, COV_COVERAGE, NUM_OF_LINE, (REPORT_TABLE.COV_COVERED_LINE) TESTED_LINE FROM REPORT_TABLE WHERE FILE_DATE = '{date}')\n" +
            "      GROUP BY SYSTEM_ID, JOB_CATEGORY, FILE_PACKAGE, FILE_NAME)\n" +
            "GROUP BY SYSTEM_ID, JOB_CATEGORY";
    private static final String CATEGORYLIST = "SELECT JOB_CATEGORY FROM REPORT_TABLE WHERE FILE_DATE = '{date}' GROUP BY JOB_CATEGORY";
    private static final String JOBLIST = "SELECT JOB_NAME FROM REPORT_TABLE WHERE FILE_DATE = '{date}' GROUP BY JOB_NAME";
    private static final String SUBDETAIL = "SELECT FILE_NAME, FILE_NAME_KO, FUNTION_NAME, FUNTION_NAME_KO, SERVICE_ID, " +
            "JOB_NAME, MANAGER, FILE_TYPE, NUM_OF_LINE, COV_COVERED_LINE, COV_COVERAGE FROM REPORT_TABLE " +
            "WHERE FILE_DATE = '{date}'  AND JOB_CATEGORY = '{category}'";
    private static final String TABLE_1 = "SELECT FILE_PACKAGE, ROUND(AVG(COV_COVERAGE), 2) COV_COVERAGE " +
            "FROM REPORT_TABLE WHERE JOB_NAME='{job_name}' ";
    private static final String TABLE_2 = "SELECT FILE_NAME, FILE_NAME_KO, ROUND(AVG(COV_COVERAGE), 2) COV_COVERAGE " +
            "FROM REPORT_TABLE WHERE JOB_NAME='{job_name}' AND FILE_PACKAGE = '{file_package}' ";
    private static final String TABLE_3 = "SELECT FUNTION_NAME, FUNTION_NAME_KO, COV_COVERAGE FROM REPORT_TABLE " +
            "WHERE JOB_NAME='{job_name}' AND FILE_PACKAGE = '{file_package}' AND FILE_NAME = '{file_name}'";
    private static final String TABLE_4 = "SELECT CODES FROM REPORT_TABLE WHERE JOB_NAME='{job_name}' " +
            "AND FILE_PACKAGE = '{file_package}' AND FILE_NAME = '{file_name}' AND FUNTION_NAME = '{function_name}'";
    private static final String CHART_QUERY = "SELECT FILE_DATE label, ROUND(AVG(COV_COVERAGE), 2) data FROM REPORT_TABLE WHERE " +
            "FILE_PACKAGE='{file_package}' AND JOB_NAME='{job_name}' AND FILE_DATE >= '{from}' AND FILE_DATE <= '{to}'";

    public enum Category {
        SYSTEM_ID("시스템"), JOB_CATEGORY("업무분류"), TOTAL_PROGRAM("전체Program"), TOTAL_TESTED("테스트된 Program"),
        OVER_COVERAGE("80% 이상 Program"), UNDER_COVERAGE("80% 미만 Program"), TOTAL_FUNC_CNT("전체 Funtion"),
        FUNC_TESTED("테스트된 Function"), TOTAL_LINE("총라인수"), TOTAL_TESTED_LINE("테스트라인수"), COVERAGE("COVERAGE"), UNTESTED("미테스트 Program"),
        FILE_NAME("프로그램 영문명"), FILE_NAME_KO("프로그램 한글명"), FUNTION_NAME("Function 영문명"),
        FUNTION_NAME_KO("Function 한글명"), SERVICE_ID("서비스 ID"), JOB_NAME("업무명"), MANAGER("담당자"),
        FILE_TYPE("유형"), NUM_OF_LINE("전체라인수"), COV_COVERED_LINE("Covered 라인수"), COV_COVERAGE("Coverage(%)"), FILE_PACKAGE("패키지명"), CODES("CODES");

        private final String desc;

        Category(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }

        public boolean isEquals(String desc) {
            return this.desc.equals(desc) ? true : false;
        }


    }

    public static String getOverView(String where) {
        return OVERVIEW.replace("{date}", where);
    }

    public static String getSubDetailView(JSONObject jsonObject) {
        String result = SUBDETAIL.replace("{date}", jsonObject.get("where").toString())
                .replace("{category}", jsonObject.get("category").toString());
        result += getAndQuery(jsonObject, "JOB_NAME", "MANAGER", "FILE_TYPE");
        return result;
    }

    public static String getDetailView(Object where) {
        return null;
    }

    public static String getCategoryList(String where) {
        return CATEGORYLIST.replace("{date}", where);
    }

    public static String getJoblist(String where) {
        return JOBLIST.replace("{date}", where);
    }

    public static String getJoblistTable(JSONObject object) {
        String result = "";
        switch (object.get("order").toString()) {
            case "1":
                result = TABLE_1.replace("{job_name}", object.get("job_name").toString());
                if (object.containsKey("from")) {
                    result += " AND FILE_DATE >= '" + object.get("from") + "'"
                            + " AND FILE_DATE <= '" + object.get("where") + "'";
                } else {
                    result += " AND FILE_DATE <= '" + object.get("where") + "'";
                }
                result += " GROUP BY FILE_PACKAGE";
                break;
            case "2":
                result = TABLE_2.replace("{job_name}", object.get("job_name").toString())
                        .replace("{file_package}", object.get("file_package").toString());
                if (object.containsKey("from")) {
                    result += " AND FILE_DATE >= '" + object.get("from") + "'"
                            + " AND FILE_DATE <= '" + object.get("where") + "'";
                } else {
                    result += " AND FILE_DATE <= '" + object.get("where") + "'";
                }
                result += " GROUP BY FILE_NAME, FILE_NAME_KO";
                break;
            case "3":
                result = TABLE_3.replace("{job_name}", object.get("job_name").toString())
                        .replace("{file_package}", object.get("file_package").toString())
                        .replace("{file_name}", object.get("file_name").toString());
                if (object.containsKey("from")) {
                    result += " AND FILE_DATE >= '" + object.get("from") + "'"
                            + " AND FILE_DATE <= '" + object.get("where") + "'";
                } else {
                    result += " AND FILE_DATE <= '" + object.get("where") + "'";
                }
                break;
            case "4":
                result = TABLE_4.replace("{job_name}", object.get("job_name").toString())
                        .replace("{file_package}", object.get("file_package").toString())
                        .replace("{file_name}", object.get("file_name").toString())
                        .replace("{function_name}", object.get("function_name").toString());
                if (object.containsKey("from")) {
                    result += " AND FILE_DATE >= '" + object.get("from") + "'"
                            + " AND FILE_DATE <= '" + object.get("where") + "'";
                } else {
                    result += " AND FILE_DATE <= '" + object.get("where") + "'";
                }
                break;
        }
        return result;
    }

    public static String getChartView(JSONObject object) {
        String result = "";
        switch (object.get("order").toString()) {
            case "1":
                result = CHART_QUERY.replace("{file_package}", object.get("file_package").toString())
                        .replace("{job_name}", object.get("job_name").toString())
                        .replace("{from}", object.get("from").toString())
                        .replace("{to}", object.get("to").toString());
                break;
            case "2":
                result = CHART_QUERY.replace("{file_package}", object.get("file_package").toString())
                        .replace("{job_name}", object.get("job_name").toString())
                        .replace("{from}", object.get("from").toString())
                        .replace("{to}", object.get("to").toString());
                result += " AND FILE_NAME='" + object.get("file_name") + "' ";
                break;
            case "3":
                result = CHART_QUERY.replace("{file_package}", object.get("file_package").toString())
                        .replace("{job_name}", object.get("job_name").toString())
                        .replace("{from}", object.get("from").toString())
                        .replace("{to}", object.get("to").toString());
                result += " AND FILE_NAME='" + object.get("file_name") + "' AND FUNTION_NAME='" + object.get("function_name") + "'";
                break;
        }
        result += " GROUP BY FILE_DATE";
        return result;
    }

    private static String getAndQuery(JSONObject object, String... names) {
        String result = "";
        for (String name : names) {
            if (object.containsKey(name) && object.get(name).toString().length() > 0) {
                result += " AND " + name + " ='" + object.get(name) + "'";
            }
        }
        return result;
    }
}
