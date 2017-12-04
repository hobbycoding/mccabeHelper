package com.mccabe.util;

import com.github.javaparser.ast.body.Parameter;

public class MCCABERoleSet {
    public static String convert(Parameter param) {
        if (param.toString().contains("[]")) {
            if (param.getType().toString().equals("String"))
                return "java.lang.String[]";
        }
        String v = param.getType().toString();
        if (v.equals("String[]"))
            v = "java.lang.String[]";
        if (v.contains("<"))
            v = v.substring(0, v.indexOf("<"));
        return v;
    }
}
