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
            String c = n.substring(n.lastIndexOf(".") + 1, n.length());
            s = s.replace(n, c);
        }
        return s;
    }
}
