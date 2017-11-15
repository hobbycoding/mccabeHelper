package com.mccabe.inst;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import com.mccabe.McCabeConfig;
import com.mccabe.util.OSUtil;
import com.mccabe.util.Tasks;
import com.mccabe.vo.Job;
import com.mccabe.vo.ProjectForJava;

public class InstrumentWorks extends McCabeConfig {
	
	private ArrayList<ProjectForJava> initializeInstrument(Job job){
		ArrayList<ProjectForJava> projects = new ArrayList<ProjectForJava>();
		Iterator<String> ikey = job.getCompileMap().keySet().iterator();
		while (ikey.hasNext()) {
			String compileKey = ikey.next();
			ArrayList<File> files = job.getCompileMap().get(compileKey);
//			files = FileUtil.omitFiles(files, new String[]{"sb.java","vo.java","sbhome.java","local.java"});// added by scourt request
			for (File file : files) {
				ProjectForJava project = new ProjectForJava(job, compileKey, file);
				projects.add(project);
			}
		}
		return projects;
	}
	
	/**
	 * @param job
	 */
	public void instrument(Job job) {
		ArrayList<ProjectForJava> projects = initializeInstrument(job);
		int totalTask = projects.size();
		int remainTask = totalTask;
		for (ProjectForJava project : projects) {
			Tasks.instrument(project);
			Tasks.echo(project.getProjectName().concat(" - this project is created,reparse,instrumented."));
			Tasks.echo("##### Remained Tasks ["+remainTask+"/"+totalTask+"], Remained Time(Min:Sec) -> about... "+(remainTask*5/2)/60+":"+(remainTask*5/2)%60);
			remainTask--;
		}
	}

	public static void main(String[] args) throws Exception {
		Properties ps = changeProperties(args);
		InstrumentWorks works = new InstrumentWorks();

		Job job = new Job();
		job.setSwitchLever(Integer.parseInt(ps.getProperty("switchLever", String.valueOf(MCCABE_OFF))));
		job.setSysName(ps.getProperty("programName", ""));
//		/tmp/EUP2.2/svnfiles/src/eap/EAPEJB/ejbModule
		job.setRepositoryRoot(SRC_DIR);
		job.setScope("all");
	
		if (job.getSysName().equalsIgnoreCase("") || job.getScope().equalsIgnoreCase("")) {
			System.out.println("[scope] or [sysName] value is not specified!");
			System.out.println("[scope] value can be [svn] or [all].");
			System.out.println("[sysName] value can be [ecm]... etc.");
		} else {
			ListManager listMan = new ListManager(job.getSysName());
			if (job.getScope().equalsIgnoreCase("svn")) {
				listMan.parseSVNLogFile(job);
				works.instrument(job);
				// for mccabe's testing
			} else if (job.getScope().equalsIgnoreCase("all")) {
				if (new File(PROJECT_DIR + fs + job.getSysName()).exists())
					OSUtil.executeCommand((OS.equalsIgnoreCase("windows") ? "cmd /c rmdir /Q /S " :"rm -f ") + PROJECT_DIR + fs + job.getSysName());
				if (new File(INSTRUMENTED_SRC_DIR + fs + job.getSysName()).exists())
					OSUtil.executeCommand((OS.equalsIgnoreCase("windows") ? "cmd /c rmdir /Q /S " :"rm -f ")+INSTRUMENTED_SRC_DIR + fs + job.getSysName());
				OSUtil.executeCommand((OS.equalsIgnoreCase("windows") ? "cmd /c " : "") + "mkdir -p "+PROJECT_DIR + fs + job.getSysName());
				OSUtil.executeCommand((OS.equalsIgnoreCase("windows") ? "cmd /c " : "") + "mkdir -p "+INSTRUMENTED_SRC_DIR + fs + job.getSysName());
				listMan.gatheringList(job);
				works.instrument(job);
			}
		}
	}

	public static void main_test(String[] args){

		System.out.println("xxx");
	}
}
