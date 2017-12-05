package com.mccabe.util;

import com.github.javaparser.ast.body.Parameter;

public class MCCABERoleSet {
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
                    result+= v.substring(v.lastIndexOf(".") + 1, v.length()) + ",";
                }
                return s.substring(0, s.indexOf("(") + 1).concat(result.substring(0, result.lastIndexOf(",")).concat(")"));
            } else {
                String c = n.substring(n.lastIndexOf(".") + 1, n.length());
                s = s.replace(n, c);
            }
        }
        return s;
    }

    public static String getModuleLetter(int i) {
        return "   " + (char)(i + 65);
    }
}
