package com.mccabe.report;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import com.mccabe.McCabeConfig;
import com.mccabe.util.FTPTransfer;
import com.mccabe.util.FileUtil;
import com.mccabe.util.OSUtil;
import com.mccabe.util.StringUtil;
import com.mccabe.util.Tasks;
import com.mccabe.vo.Job;
import com.mccabe.vo.PCF;
import com.mccabe.vo.Program;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

public class ReportWorks extends McCabeConfig {
    private static Properties properties;

    private boolean existOriginalSource(PCF pcf) {
        File src = new File(pcf.getSrcDir() + fs + pcf.getSrcFile());
        return src.exists();
    }

    private ArrayList<String> makeCommandSetForReport(PCF pcf, Job job) {
        ArrayList<String> commands = new ArrayList<>();
        StringBuffer sb = new StringBuffer();

        // purge project database
        sb.append(CLI);
        sb.append(" purge -LEVEL all -PCF ");
        sb.append(pcf.getPcfFile().getAbsolutePath());
        commands.add(sb.toString());
        sb.delete(0, sb.capacity());

        ArrayList<File> traceFiles = pcf.getAccumulateTraceFiles();
        // make cli for tracefile importing
        for (int i = 0; i < traceFiles.size(); i++) {
            sb.append(CLI);
            sb.append("import -instout ");
            sb.append(traceFiles.get(i).getAbsolutePath());
            sb.append(" -pcf ");
            sb.append(pcf.getPcfFile().getAbsolutePath());
            commands.add(sb.toString());
            sb.delete(0, sb.capacity());
        }
        // make cli for untested report : txt
        sb.append(CLI);
        sb.append("listing -coverage  -pcf ");
        sb.append(pcf.getPcfFile().getAbsolutePath());
        sb.append(" -noparse -coverage -output ");
        sb.append(REPORT_DIR + fs + job.getSysName() + fs + pcf.getProjectName().concat(".txt"));
        commands.add(sb.toString());
        sb.delete(0, sb.capacity());

        // make cli for coverage report : csv
        sb.append(CLI);
        sb.append("metrics -ss  -ssheader -sssummary -detail branch -coverage " + REPORT_TEMPLATE_NAME + " -pcf ");
        sb.append(pcf.getPcfFile().getAbsolutePath());
        sb.append(" -output ");
        sb.append(REPORT_DIR + fs + job.getSysName() + fs + pcf.getProjectName().concat("_branch").concat(".csv"));
        commands.add(sb.toString());
        sb.delete(0, sb.capacity());

        // make cli for coverage report : csv
        sb.append(CLI);
        sb.append("metrics -ss -ssheader -sssummary -report xbat_codecov " + REPORT_TEMPLATE_NAME + " -pcf ");
        sb.append(pcf.getPcfFile().getAbsolutePath());
        sb.append(" -output ");
        sb.append(REPORT_DIR + fs + job.getSysName() + fs + pcf.getProjectName().concat("_codecov").concat(".csv"));
        commands.add(sb.toString());
        sb.delete(0, sb.capacity());

        // cli for publishing for 8.0
        if (job.isPublish()) {
            sb.append(CLI);
            sb.append("publish -pcf ");
            sb.append(pcf.getPcfFile().getAbsolutePath());
            sb.append(" -outputdir ");
            sb.append(HUDSON_WEB_ROOT + fs + job.getSysName());
            commands.add(sb.toString());
            sb.delete(0, sb.capacity());
        }
        return commands;
    }

    private PCF initializeTraceFileSet(PCF pcf, boolean isAccumulated) throws Exception {
        if (isAccumulated) {
            pcf.setAccumulateTraceFiles(FileUtil.getFilesRecursive(new File(TRACEFILE_HOME), "", pcf.getProjectNameMinusTrId(), ".out", 0));
        } else {
            pcf.setAccumulateTraceFiles(FileUtil.getFilesRecursive(new File(TRACEFILE_HOME + fs + properties.getProperty("programName")), "", pcf.getProjectName(), ".out", 0));
        }
        return pcf;
    }

    private PCF parse(File pcfFile) throws IOException {

        PCF pcf = new PCF();
        pcf.setPcfFile(pcfFile);

        Reader reader = new FileReader(pcfFile.getAbsolutePath());
        BufferedReader bufferedReader = new BufferedReader(reader);
        try {
            String line = "";
            while ((line = bufferedReader.readLine()) != null && line != "") {
                if (line.startsWith("PROGRAM ")) {
                    pcf.setProjectName(line.substring(line.indexOf(" ") + 1).replaceAll("\"", ""));
                    // pcf.setTrId(pcf.getProjectName().substring(0,
                    // pcf.getProjectName().indexOf("_")));
                }
                if (line.startsWith("INSTDIR ")) {
                    pcf.setInstDir(line.substring(line.indexOf(" ") + 1).replaceAll("\"", ""));
                }
                if (line.startsWith("INSTOUT ")) {
                    pcf.setInstOut(line.substring(line.indexOf(" ") + 1).replaceAll("\"", ""));
                }
                if (line.startsWith("COMDIR ")) {
                    pcf.setComDir(line.substring(line.indexOf(" ") + 1).replaceAll("\"", ""));
                }
                if (line.startsWith("DIR ")) {
                    pcf.setSrcDir(line.substring(line.indexOf(" ") + 1).replaceAll("\"", ""));
                }
                if (line.startsWith("cw_")) {
                    String[] temp = line.split(" ");
                    pcf.setLanguage(temp[0].split("_")[1]);
                    pcf.setSrcFile(temp[1].replaceAll("\"", ""));
                }
            }
        } catch (Exception e) {
        } finally {
            bufferedReader.close();
            reader.close();
        }
        return pcf;
    }

    private void integrateCSV(Job job) throws Exception {
        HashMap<String, Program> programMap = new HashMap<String, Program>();
        ArrayList<File> csvFiles = FileUtil.getFilesRecursive(new File(REPORT_DIR + fs + job.getSysName()), "", "", ".csv", 0);
        for (File file : csvFiles) {
//			System.out.println(file.getName());
            Program program = new Program(file);
            program.parse(); // all of methods
            program.calc();
            programMap.put(program.getName(), program); // all of methods
        }
        generateXLS(job, programMap);
    }

    /**
     * @param job
     * @param programMap
     * @throws IOException
     * @throws WriteException
     * @throws RowsExceededException
     */
    private void generateXLS(Job job, HashMap<String, Program> programMap) throws IOException, WriteException, RowsExceededException {
//		WritableWorkbook workbook = Workbook.createWorkbook(new File("C:/dev/mccabe/workspace/scourt_opensns/temp/eap/_summary_" + job.getSysName() + ".xls"));
        WritableWorkbook workbook = Workbook.createWorkbook(new File(REPORT_DIR + fs + job.getSysName() + fs + "_summary_" + job.getSysName() + ".xls"));
        WritableSheet sheet = workbook.createSheet("summary", 0);

        // Header IQ
        sheet.addCell(new Label(0, 0, "Class-Name"));
        sheet.addCell(new Label(1, 0, "Total-Lines"));
        sheet.addCell(new Label(2, 0, "Covered-Lines"));
        sheet.addCell(new Label(3, 0, "Total-Branches"));
        sheet.addCell(new Label(4, 0, "Covered-Branches"));

        // DecimalFormat df = new DecimalFormat("00.00");
        int i = 0;
        for (Program program : programMap.values()) {
            i++;
            // Data IQ
            sheet.addCell(new Label(0, i, program.getName()));
            sheet.addCell(new jxl.write.Number(1, i, program.getSumTotalLines()));
            sheet.addCell(new jxl.write.Number(2, i, program.getSumCovLines()));
            sheet.addCell(new jxl.write.Number(3, i, program.getSumTotalBranches()));
            sheet.addCell(new jxl.write.Number(4, i, program.getSumCovBranches()));

        }
        workbook.write();
        workbook.close();
    }

    private ArrayList<String> setIncludeModule(File includeModulesCSVFile) {
        ArrayList<String> includeModules = new ArrayList<String>();
        try {
            Reader reader = new FileReader(includeModulesCSVFile);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line = "";
            line = bufferedReader.readLine();// first line is skipped.
            while ((line = bufferedReader.readLine()) != null && line != "") {
                // ex) WCV,민사,CV450SbBean,"txnBatchDaeri(SCBox, Vector, Vector)",2012-09-04,이근열
                line = StringUtil.replace(line, "\"", "");
                System.out.println(line);
                try {
                    String[] cells = line.split(",");
                    String className = cells[2].trim();
                    if (className.indexOf(".java") > -1) className = StringUtil.replace(className, ".java", "");

                    String moduleName = cells[3].trim();
                    if (moduleName.indexOf("(") > -1) moduleName = moduleName.substring(0, moduleName.indexOf("("));

                    String classNamePlusModuleName = className + "." + moduleName;
                    if (includeModules.contains(classNamePlusModuleName)) {
                        System.out.println("[" + classNamePlusModuleName + "] is already exist!");
                    } else {
                        includeModules.add(classNamePlusModuleName);
                    }
                } catch (ArrayIndexOutOfBoundsException aiobe) {
                    aiobe.printStackTrace();
                    System.out.println("[" + line + "] could not be parsed !!!!!");
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return includeModules;
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        properties = changeProperties(args);
        ReportWorks works = new ReportWorks();

        Job job = new Job();
        job.setSysName(properties.getProperty("programName", ""));
        try {
            log("programName--->" + job.getSysName());
            File projectFolder = new File(PROJECT_DIR);
            Path fileList_json = Paths.get(projectFolder + fs + job.getSysName() + fs + "fileList.json");
            JSONArray fileList = null;
            if (Files.exists(fileList_json)) {
                fileList = (JSONArray) new JSONParser().parse(new String(Files.readAllBytes(fileList_json), "UTF-8"));
            }
            if (new File(REPORT_DIR + fs + job.getSysName()).exists() && REMOVE_REPORT_DIR) {
                System.out.println(REPORT_DIR + fs + job.getSysName() + " exist. delete.");
                OSUtil.executeCommand((OS.equalsIgnoreCase("windows") ? "cmd /c rmdir /Q /S " : "rm -f ") + REPORT_DIR + fs + job.getSysName());
            }
            OSUtil.executeCommand((OS.equalsIgnoreCase("windows") ? "cmd /c mkdir " : "mkdir -p ") + REPORT_DIR + fs + job.getSysName());
            OSUtil.executeCommand((OS.equalsIgnoreCase("windows") ? "cmd /c mkdir " : "mkdir -p ") + TRACEFILE_HOME + fs + job.getSysName());
            ArrayList<File> pcfFiles = FileUtil.getFilesRecursive(new File(projectFolder + fs + job.getSysName()), "", "", ".pcf", 0);
            FileJob defaultJob = new FileJob(job.getSysName());
            HashMap<String, FileJob> subjobList = new HashMap<>();
            if (properties.containsKey("subjobs")) {
                String raw = properties.getProperty("subjobs").substring(properties.getProperty("subjobs").indexOf("[") + 1, properties.getProperty("subjobs").lastIndexOf("]"));
                log("[subJob property " + raw + "]");
                for (String subjob : raw.split(",")) {
                    subjob = subjob.trim();
                    log("[SubJob Found. " + subjob + "]");
                    subjobList.put((properties.getProperty("programName") + fs + subjob.trim()).replace(fs, "_"), new FileJob(properties.getProperty("programName") + "_" + subjob.replace(fs, "_")));
                }
            }
            for (File file : pcfFiles) {
                if (fileList != null && !fileList.contains(file.getName().replace(".pcf", "")))
                    continue;
                PCF pcf = works.parse(file);
                if (works.existOriginalSource(pcf)) {
                    pcf = works.initializeTraceFileSet(pcf, false);
                    ArrayList<String> commands = works.makeCommandSetForReport(pcf, job);
                    for (String command : commands) {
                        OSUtil.executeCommand(command);
                    }
                    defaultJob.write(pcf.getProjectName());
                    for (Map.Entry<String, FileJob> entry : subjobList.entrySet()) {
                        log("Subjob [" + pcf.getProjectName() + "] startWith [" + entry.getKey() + "]");
                        if (pcf.getProjectName().startsWith(entry.getKey()))
                            entry.getValue().write(pcf.getProjectName());
                    }
                }
            }
            if (fileList != null)
                Files.delete(fileList_json);
            if (job.isPublish()) {
                WriteIndex wi = new WriteIndex();
                wi.generateIndexHTML(job);
                wi.generateListHTML(job);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void clearFile(File file) throws IOException {
        log("clear file. [" + file.getAbsolutePath() + "]");
        if (file.exists())
            file.delete();
        file.createNewFile();
    }

    static class FileJob {
        private File branch;
        private File codecov;
        private String programName;

        public FileJob(String jobName) throws IOException {
            this.programName =  properties.getProperty("programName");
            this.branch = new File(REPORT_DIR + fs + programName + fs + jobName + "_branch.csv");
            this.codecov = new File(REPORT_DIR + fs + programName + fs + jobName + "_codecov.csv");
            clearFile(branch);
            clearFile(codecov);
        }

        public void write(String projectname) throws IOException {
            log("Write file [" + branch.toURI() + "]");
            log("Write file [" + codecov.toURI() + "]");
            Files.write(Paths.get(branch.toURI()),
                    Files.readAllBytes(Paths.get(REPORT_DIR + fs + programName + fs + projectname.concat("_branch").concat(".csv"))), StandardOpenOption.APPEND);
            Files.write(Paths.get(codecov.toURI()),
                    Files.readAllBytes(Paths.get(REPORT_DIR + fs + programName + fs + projectname.concat("_codecov").concat(".csv"))), StandardOpenOption.APPEND);
        }
    }

}
