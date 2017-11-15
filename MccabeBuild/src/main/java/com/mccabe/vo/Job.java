package com.mccabe.vo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.mccabe.McCabeConfig;

public class Job extends McCabeConfig {
	
	private int switchLever;
	/**
	 * in case of tys , id means trId(ex : 20110726[SEQ:4-digits])
	 * in case of scourt , id means sysName(ex : EAP,ECM,WDM,ECF etc)
	 */
	private String sysName;
	/**
	 * in case of tys , compileMap means file-set(classified by businessCode[AP_CORE_SP,Common etc]) to be compiled.
	 * in case of scourt , compileMap means file-set(classified by applType[Web,Java,EJB etc]) to be compiled.
	 */
	private HashMap<String, ArrayList<File>> compileMap;
	
	/**
	 * in case of scourt , scope can be [svn] or [all].
	 */
	private String scope;
	private boolean isPublish;

	private String repositoryRoot;
	
	private ArrayList<String> includeModules;

	public String getRepositoryRoot() {
		return repositoryRoot;
	}


	public void setRepositoryRoot(String repositoryRoot) {
		this.repositoryRoot = repositoryRoot;
	}


	/**
	 * id means sysName(ex : eap,ecm,wdm,ecf etc)
	 * @return the id
	 */
	public String getSysName() {
		return sysName;
	}


	/**
	 * @param id the id to set
	 */
	public void setSysName(String id) {
		this.sysName = id;
	}


	/**
	 * compileMap means file-set(classified by applType[Web,Java,EJB etc]) to be compiled.
	 * @return the compileMap
	 */
	public HashMap<String, ArrayList<File>> getCompileMap() {
		return compileMap;
	}


	/**
	 * @param compileMap the compileMap to set
	 */
	public void setCompileMap(HashMap<String, ArrayList<File>> compileMap) {
		this.compileMap = compileMap;
	}


	/**
	 * @return the switchLever
	 */
	public int getSwitchLever() {
		return switchLever;
	}


	/**
	 * @param switchLever the switchLever to set
	 */
	public void setSwitchLever(int switchLever) {
		this.switchLever = switchLever;
	}


	/**
	 * @return the scope
	 */
	public String getScope() {
		return scope;
	}


	/**
	 * @param scope the scope to set
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}


	/**
	 * @return the isPublish
	 */
	public boolean isPublish() {
		return isPublish;
	}


	/**
	 * @param isPublish the isPublish to set
	 */
	public void setPublish(boolean isPublish) {
		this.isPublish = isPublish;
	}


	public ArrayList<String> getIncludeModules() {
		return includeModules;
	}


	public void setIncludeModules(ArrayList<String> includeModules) {
		this.includeModules = includeModules;
	}

}
