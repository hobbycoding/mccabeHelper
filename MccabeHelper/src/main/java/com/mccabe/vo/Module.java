package com.mccabe.vo;

import com.mccabe.util.StringUtil;

public class Module {
	
	private String programName;
	private String name;
	private double totalLine;
	private double covLine;
	private double totalBranch;
	private double covBranch;
//	private int sumOfTotalLine;
//	private int sumOfCovLine;
//	private int sumOfTotalBranch;
//	private int sumOfCovBranch;
	public Module(String line) {
		// TODO Auto-generated constructor stub
		//ECOMApplicationDataU.importData(),15,10,9,4
		//ECOMApplicationDataU.importDirs(devon.core.config.LConfiguration),15,0,3,0
		//"ECOMApplicationDataU.importData(org.w3c.dom.Element,java.lang.String)",10,7,5,2
		line = StringUtil.replace(line, "\"", "");
		try {
			String[] extractModule = line.split("\\),");
			//extractModule[0] : ECOMApplicationDataU.importData(org.w3c.dom.Element,java.lang.String
			//extractModule[1] : 10,7,5,2
			this.name = extractModule[0]+")";
			String[] records = extractModule[1].split(",");
			this.totalLine = Double.parseDouble(records[0]);
			this.covLine = Double.parseDouble(records[1]);
			this.totalBranch = Double.parseDouble(records[2]);
			this.covBranch = Double.parseDouble(records[3]);
		} catch (ArrayIndexOutOfBoundsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * @return the programName
	 */
	public String getProgramName() {
		return programName;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the totalLine
	 */
	public double getTotalLine() {
		return totalLine;
	}
	/**
	 * @return the covLine
	 */
	public double getCovLine() {
		return covLine;
	}
	/**
	 * @return the totalBranch
	 */
	public double getTotalBranch() {
		return totalBranch;
	}
	/**
	 * @return the covBranch
	 */
	public double getCovBranch() {
		return covBranch;
	}
}
