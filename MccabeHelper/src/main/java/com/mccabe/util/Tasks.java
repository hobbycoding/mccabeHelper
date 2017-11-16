package com.mccabe.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.Delete;
import org.apache.tools.ant.taskdefs.Echo;
import org.apache.tools.ant.taskdefs.Expand;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.Mkdir;
import org.apache.tools.ant.types.FileSet;

import com.mccabe.McCabeConfig;
import com.mccabe.anttasks.MccabeProject;
import com.mccabe.vo.ProjectForJava;

public class Tasks extends McCabeConfig {

	public static void instrument(ProjectForJava inst){
		try {
			Project project = new Project();
			
			inst.getFileSet().setProject(project);
			
			MccabeProject mccabeproject = new MccabeProject();
			mccabeproject.setTaskName("mccabeproject");
			mccabeproject.setProject(project);
			mccabeproject.setProgramName(inst.getProjectName());
//			mccabeproject.setParse(inst.isParse());
			mccabeproject.setPcfTemplate(new File(PCF_TEMPLATE));
			mccabeproject.setInstDir(new File(inst.getInstDir()));
			mccabeproject.setInstFile(new File(inst.getInstFile()));
			mccabeproject.setProjectDir(new File(inst.getProjectDir()));
			mccabeproject.setComDir(new File(inst.getComDir()));
			mccabeproject.setClean(inst.isClean());
			mccabeproject.setInstrument(inst.isInstrument());
			mccabeproject.setExport(inst.isExport());
			mccabeproject.addFileset(inst.getFileSet());
			mccabeproject.execute();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void echo(String message){
		DefaultLogger defaultLogger = new DefaultLogger();
		defaultLogger.setOutputPrintStream(System.out);
		defaultLogger.setMessageOutputLevel(Project.MSG_INFO);
		
		Project project = new Project();
		project.addBuildListener(defaultLogger);
		Echo e = new Echo();
		e.setTaskName(message);
		e.setProject(project);
		e.execute();
	}
	public static void copyDirToDir(String fromDir, String toDir, boolean overwrite) {
		try {
			Project project = new Project();
			
			Copy copy = new Copy();
			copy.setTaskName("copy");
			copy.setProject(project);
//			copy.setFile(new File(fromDir));
			FileSet fileset = new FileSet();
			fileset.setProject(project);
			fileset.setDir(new File(fromDir));
			fileset.setIncludes("**/**");
			copy.addFileset(fileset);
			copy.setTodir(new File(toDir));
			copy.setOverwrite(overwrite);
			copy.execute();
			
			
		} catch(Exception e) {
//			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//			PrintStream out = new PrintStream(outStream);
			e.printStackTrace();
//			results[0] = "FAIL";
//			results[1] = outStream.toString();
		}
	}
	public static void copyFileToDir(String file, String toDir, boolean overwrite) {
		try {
			Project project = new Project();
			
			Copy copy = new Copy();
			copy.setTaskName("copy");
			copy.setProject(project);
			copy.setFile(new File(file));
			copy.setTodir(new File(toDir));
			copy.setOverwrite(overwrite);
			copy.execute();
		} catch(Exception e) {
//			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//			PrintStream out = new PrintStream(outStream);
			e.printStackTrace();
//			results[0] = "FAIL";
//			results[1] = outStream.toString();
		}
	}
	
	public static void mkDir(String dir) {
		try {
			Project project = new Project();
			
			Mkdir mkdir = new Mkdir();
			mkdir.setTaskName("mkdir");
			mkdir.setProject(project);
			mkdir.setDir(new File(dir));
			mkdir.execute();
		} catch(Exception e) {
//			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//			PrintStream out = new PrintStream(outStream);
			e.printStackTrace();
//			results[0] = "FAIL";
//			results[1] = outStream.toString();
		}
	}
	
	public static void deleteFile(String file) {
		try {
			Project project = new Project();
			
			Delete delete = new Delete();
			delete.setTaskName("delete");
			delete.setProject(project);
			delete.setFile(new File(file));
			delete.execute();
		} catch(Exception e) {
			System.out.println("[McCabeConfig] 파일 " + file + " 를(을) 삭제하지 못했습니다.");
			e.printStackTrace();
		}
	}
	
	public static void deleteDir(String dir) {
		try {
			Project project = new Project();
			
			Delete delete = new Delete();
			delete.setTaskName("delete");
			delete.setProject(project);
			delete.setDir(new File(dir));
			delete.execute();
		} catch(Exception e) {
			System.out.println("[McCabeConfig] 디렉토리 " + dir + " 를(을) 삭제하지 못했습니다.");
			e.printStackTrace();
		}
	}
	
	public static void jar(String baseDir, String destFile) {
		try {
			Project project = new Project();
			
			Jar jar = new Jar();
			jar.setTaskName("jar");
			jar.setProject(project);
			jar.setBasedir(new File(baseDir));
			jar.setDestFile(new File(destFile));
			jar.execute();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void unjar(String jarFile, String destDir, String results[]) {
		try {
			Project project = new Project();
			
			Expand expand = new Expand();
			expand.setTaskName("unjar");
			expand.setProject(project);
			expand.setSrc(new File(jarFile));
			expand.setDest(new File(destDir));
			expand.execute();
		} catch(Exception e) {
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			PrintStream out = new PrintStream(outStream);
			e.printStackTrace(out);
			results[0] = "FAIL";
			results[1] = outStream.toString();
		}
	}

	public static void copyDirToDirInclude(String fromDir, String toDir,String include, boolean overwrite) {
			try {
				Project project = new Project();
				
				Copy copy = new Copy();
				copy.setTaskName("copy");
				copy.setProject(project);
	//			copy.setFile(new File(fromDir));
				FileSet fileset = new FileSet();
				fileset.setProject(project);
				fileset.setDir(new File(fromDir));
				fileset.setIncludes(include);
				copy.addFileset(fileset);
				copy.setTodir(new File(toDir));
				copy.setOverwrite(overwrite);
				copy.execute();
				
				
			} catch(Exception e) {
	//			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
	//			PrintStream out = new PrintStream(outStream);
				e.printStackTrace();
	//			results[0] = "FAIL";
	//			results[1] = outStream.toString();
			}
		}

	public static void main(String[] args) {
			copyDirToDirInclude("C:\\dev\\mccabe\\workspace\\dp_eup22\\program\\BJDUWS01", "C:\\dev\\mccabe\\workspace\\dp_eup22\\temp\\test", "**/template*", true);
		}
}
