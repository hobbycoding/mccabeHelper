package com.mccabe.util;

import com.github.javaparser.ast.body.Parameter;

public class MCCABERoleSet {
    private static int ColumnBase = 26;
    private static int DigitMax = 7; // ceil(log26(Int32.Max))
    private static String Digits = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String convert(Parameter param) {
        if (param.toString().contains("[]")) {
            if (param.getType().toString().equals("String"))
                return "String[]";
        }
        String v = param.getType().toString();
        if (v.equals("String[]"))
            v = "String[]";
        if (v.contains("<"))
            v = v.substring(0, v.indexOf("<"));
        return v;
    }

    public static String convert(String s) {
        String n = s.substring(s.indexOf("(") + 1, s.indexOf(")"));
        if (n.length() > 0) {
            if (n.contains(",")) {
                String result = "";
                for (String v : n.split(",")) {
                    result += v.substring(v.lastIndexOf(".") + 1, v.length()) + ",";
                }
                return s.substring(0, s.indexOf("(") + 1).concat(result.substring(0, result.lastIndexOf(",")).concat(")"));
            } else {
                String c = n.substring(n.lastIndexOf(".") + 1, n.length());
                s = s.replace(n, c);
            }
        }
        return s;
    }

    public static String getModuleLetter(int value) {
        return String.format( "%4s", getModuleLetters(value).trim());
    }

    private static String getModuleLetters(int value) {
        char charVal = (char) ('A' + value % 26);
        int div = value / 26;
        if (div > 0) {
            return getModuleLetter(div - 1) + charVal;
        } else {
            return "" + charVal;
        }
    }
}
