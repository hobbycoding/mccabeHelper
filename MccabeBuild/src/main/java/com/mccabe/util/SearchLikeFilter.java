package com.mccabe.util;
import java.io.File;
import java.io.FilenameFilter;


public class SearchLikeFilter implements FilenameFilter{
    private String searchWord;
    public SearchLikeFilter(String searchWord){
        this.searchWord = searchWord;
    }
    public boolean accept(File dir, String name) {
        if (name != null) {
          return name.indexOf(searchWord)>-1;
        }
        return false;
    }
    
}