package com.mccabe.temp;

import java.io.File;
import java.io.FileWriter;

public class WLog {
	FileWriter log = null;
	public WLog(File file){
		try{
			log = new FileWriter(file);
		}catch(Exception e){}
	}
	
	public void write(String str){
		try{
			log.write(str);
			log.write(System.getProperty("line.separator"));
			log.flush();
		}catch(Exception e){}
		
	}
	public void write(Exception ex){
		try{
			log.write(ex.toString());
			log.flush();
		}catch(Exception e){}
		
	}
	public void close(){
		try{
			log.close();
		}catch(Exception e){}
		
	}
}
