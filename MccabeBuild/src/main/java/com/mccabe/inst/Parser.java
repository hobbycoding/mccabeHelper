package com.mccabe.inst;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.mccabe.McCabeConfig;
import com.mccabe.vo.Job;

public class Parser extends McCabeConfig {

	
	private Job transfer;
	private String transferFileName;

	/**
	 * @param fileName
	 * @see fileName is transfer list file. ex) java_app_new_file_20110602_153230.lst.2
	 */
	public Parser(String fileName) {
//		super();
		transferFileName = fileName;
		HashMap<String, ArrayList<File>> compileMap = new HashMap<String, ArrayList<File>>();
		this.transfer = new Job();
		transfer.setSwitchLever(MCCABE_OFF);
		transfer.setCompileMap(compileMap);
		transfer.setSysName("");
	}

	/**
	 * @return the transfer
	 */
	public Job getTransfer() {
		return transfer;
	}


	/**
	 * @param transfer the transfer to set
	 */
	public void setTransfer(Job transfer) {
		this.transfer = transfer;
	}
	
	private void collectCompileList(String[] param) {
		HashMap<String, ArrayList<File>> compileMap = transfer.getCompileMap();
		String businessCode;
		File file;
		try {
			businessCode = param[0];
			file = new File(param[1]);
			ArrayList<File> list = null;
			if (compileMap.containsKey(businessCode)) {
				list = compileMap.get(businessCode);
				list.add(file);
				compileMap.put(businessCode, list);
			} else {
				list = new ArrayList<File>();
				list.add(file);
				compileMap.put(businessCode, list);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param param
	 * common/com/tyib/ld/ut/util/BusnDateUtil.java
	 * apps/AP_CORE/com/tyib/cd/sp/ac/appcomn/BzarComnMGT/BzarComnFnctSVC.java
	 * @return
	 * common
	 * apps/AP_CORE
	 */
	private String[] splitStr(String param) {
		
		String [] splitedArray = param.split("/com/");
		splitedArray[1]="com/".concat(splitedArray[1]);
		
		return splitedArray;
	}

//	private String getProperty(String key, String defaultValue) {
//		// Read properties file.
//		Properties properties = new Properties();
//
//		try {
//			// eclipse config
//			properties.load(new FileInputStream(CONFIG_PATH));
//		} catch (IOException e) {
//			System.out.println("There is no build.properties.");
//		}
//
//		return properties.getProperty(key, defaultValue);
//	}

//	private String getProperty(String key) {
//		return this.getProperty(key, "");
//	}
//
//
//
//	
//	private void fileOut(String print, String tempFileName) {
//
//		try {
//			PrintWriter pw = new PrintWriter(new FileOutputStream(tempFileName, false), true);
//			pw.println(print);
//			pw.close();
//		} catch (Exception e) {
//		}
//	}
	
}
