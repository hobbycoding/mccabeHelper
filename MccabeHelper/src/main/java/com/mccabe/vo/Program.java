package com.mccabe.vo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import com.mccabe.McCabeConfig;
import com.mccabe.util.StringUtil;

public class Program {

	private File csvFile;
	private String name;
	private String mappingKey;//class name
	private ArrayList<Module> modules;
	private double linePercentageCov;
	private double sumCovLines;
	private double sumTotalLines;
	private double branchPercentageCov;
	private double sumCovBranches;
	private double sumTotalBranches;
	
	private boolean isInclude;
	
	public Program(File csvFile){
		this.csvFile = csvFile;
	}
	
	/**
	 * for include method list
	 * @param job
	 */
	public void parse(Job job){
		ArrayList<String> includeModules = job.getIncludeModules();
		try {
			modules = new ArrayList<Module>();
			Reader reader = new FileReader(this.csvFile);
			BufferedReader bufferedReader = new BufferedReader(reader);
			String line = "";
			while ((line = bufferedReader.readLine()) != null && line != "") {
				if(line.indexOf("Program: ")>-1){
					this.name = line.substring("Program: ".length());
					this.mappingKey = name.substring(name.lastIndexOf("_")+1);
				}
				line = StringUtil.replace(line, "\"", "");
				int index = line.indexOf("(");
				if(index > -1){//ex) "EAP113_1m01Cmd.execute(HttpServletRequest,HttpServletResponse)",27,0,6,0
					String classNamePlusModuleName = line.substring(0, index);
					if(includeModules.contains(classNamePlusModuleName)){
						modules.add(new Module(line));
						this.isInclude = true;
						System.out.println("Included module ,["+line.substring(0, line.indexOf(")")+1)+"]");
					}else{
//						System.out.println("Excluded module ,["+line.split(",")[0]+"].");
					}
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
#1
Coverage Report

Program: EAPWeb_JavaSource_scourt_eap_cmd_eap100_EAP111s06Cmd
Module Name,Lines,Test Lines,Branches,Test Branches
EAP111s06Cmd.EAP111s06Cmd(),3,0,1,0
"EAP111s06Cmd.execute(HttpServletRequest,HttpServletResponse)",22,0,6,0
"EAP111s06Cmd.parseDataSet(gauceControlObj,EAP01a0Vo)",112,0,7,0
Total:,137,0,14,0

#2
Coverage Report

Program: WSCWeb_src_scourt_wsc_cmd_wsc100_WSC104m02Cmd
Module Name,Lines,Test Lines,Branches,Test Branches
WSC104m02Cmd.WSC104m02Cmd(),3,3,1,1
"WSC104m02Cmd.execute(javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse)",31,24,9,4

	 * 
	 */
	public void parse() {
		try {
			modules = new ArrayList<Module>();
			Reader reader = new FileReader(this.csvFile);
			BufferedReader bufferedReader = new BufferedReader(reader);
			String line = "";
			while ((line = bufferedReader.readLine()) != null && line != "") {
				if (line.indexOf("Program: ") > -1) {
					this.name = line.substring("Program: ".length());
					this.mappingKey = name.substring(name.lastIndexOf("_") + 1);
					continue;
				}
				line = StringUtil.replace(line, "\"", "");
				int index = line.indexOf("(");
				if (index > -1) {
					modules.add(new Module(line));
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void calc(){
		double totalLines = 0.0;
		double covLines = 0.0;
		double totalBranches = 0.0;
		double covBranches = 0.0;
		for(Module module:this.modules){
			totalLines = totalLines + module.getTotalLine();
			covLines = covLines + module.getCovLine();
			totalBranches = totalBranches + module.getTotalBranch();
			covBranches = covBranches + module.getCovBranch();
		}
		this.sumTotalLines = totalLines;
		this.sumCovLines = covLines;
		this.sumTotalBranches = totalBranches;
		this.sumCovBranches = covBranches;
		this.linePercentageCov = ( covLines / totalLines ) * 100;
		this.branchPercentageCov = ( covBranches / totalBranches ) * 100;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the mappingKey
	 */
	public String getMappingKey() {
		return mappingKey;
	}
	/**
	 * @return the modules
	 */
	public ArrayList<Module> getModules() {
		return modules;
	}
	/**
	 * @return the linePercentageCov
	 */
	public double getLinePercentageCov() {
		return linePercentageCov;
	}
	/**
	 * @return the sumCovLines
	 */
	public double getSumCovLines() {
		return sumCovLines;
	}
	/**
	 * @return the sumTotalLines
	 */
	public double getSumTotalLines() {
		return sumTotalLines;
	}
	/**
	 * @return the branchPercentageCov
	 */
	public double getBranchPercentageCov() {
		return branchPercentageCov;
	}
	/**
	 * @return the sumCovBranches
	 */
	public double getSumCovBranches() {
		return sumCovBranches;
	}
	/**
	 * @return the sumTotalBranches
	 */
	public double getSumTotalBranches() {
		return sumTotalBranches;
	}
	public boolean isInclude() {
		return isInclude;
	}
}
