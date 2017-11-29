package com.mccabe.util;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.comments.Comment;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseJavaDoc {

    public void parse() throws IOException, ParseException {
        String regex = "(@\\p{Alnum}+)(.*)";
        File file = new File("/Users/hyoju/Downloads/OerpJrnlHubFlSnAuto.java");
        CompilationUnit cu = JavaParser.parse(file);
        List<Comment> list = cu.getComments();
        String rawString = list.get(0).getContent();
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(rawString);
        while (match.find()) {
            System.out.println(match.group(1).replace("@", "") + " : " + match.group(2).trim());
        }

    }
}
