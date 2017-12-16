package com.mccabe.rest;

public class Query {
    private static final String OVERVIEW =  "SELECT SYSTEM_ID, JOB_CATEGORY, COUNT(JOB_CATEGORY) TOTAL_PROGRAM, COUNT(CASE WHEN COVERAGE > 0 THEN 1 END ) TOTAL_TESTED,\n" +
            "                                   COUNT(CASE WHEN COVERAGE >= 80 THEN 1 END) OVER_COVERAGE, COUNT(CASE WHEN COVERAGE < 80 THEN 1 END) UNDER_COVERAGE,\n" +
            "                                   SUM(TOTAL_FUNC_CNT) TOTAL_FUNC_CNT, SUM(TESTED) FUNC_TESTED, SUM(NUM_OF_LINE) TOTAL_LINE, SUM(TESTED_LINE) TOTAL_TESTED_LINE,\n" +
            "                                   (SUM(COVERAGE) / COUNT(FILE_PACKAGE)) COVERAGE, (COUNT(JOB_CATEGORY) - COUNT(CASE WHEN COVERAGE > 0 THEN 1 END )) UNTESTED\n" +
            "FROM (SELECT SYSTEM_ID, JOB_CATEGORY, FILE_PACKAGE, FILE_NAME, COUNT(FILE_NAME) TOTAL_FUNC_CNT, COUNT(FILE_NAME) - COUNT(DECODE(COV_COVERAGE, 0, 1)) TESTED,\n" +
            "        SUM(COV_COVERAGE) / CASE WHEN (COUNT(FILE_NAME) - COUNT(DECODE(COV_COVERAGE, 0, 1))) <= 0 THEN 1 ELSE (COUNT(FILE_NAME) - COUNT(DECODE(COV_COVERAGE, 0, 1))) END COVERAGE,\n" +
            "        SUM(NUM_OF_LINE) NUM_OF_LINE,  SUM(TESTED_LINE) TESTED_LINE\n" +
            "      FROM (SELECT SYSTEM_ID, JOB_CATEGORY, FILE_PACKAGE, FILE_NAME, FUNTION_NAME, COV_COVERAGE, NUM_OF_LINE, TESTED_LINE FROM REPORT_TABLE WHERE FILE_DATE = '{date}')\n" +
            "      GROUP BY SYSTEM_ID, JOB_CATEGORY, FILE_PACKAGE, FILE_NAME)\n" +
            "GROUP BY SYSTEM_ID, JOB_CATEGORY";

    public enum Category {
        SYSTEM_ID("시스템"), JOB_CATEGORY("업무분류"), TOTAL_PROGRAM("전체Program"), TOTAL_TESTED("테스트된 Program"),
        OVER_COVERAGE("80% 이상 Program"), UNDER_COVERAGE("80% 미만 Program"), TOTAL_FUNC_CNT("전체 Funtion"),
        FUNC_TESTED("테스트된 Function"), TOTAL_LINE("총라인수"), TOTAL_TESTED_LINE("테스트라인수"), COVERAGE("COVERAGE"), UNTESTED("미테스트 Program");
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
}
