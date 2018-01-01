package com.mccabe.report;

import com.mccabe.McCabeConfig;
import com.mccabe.util.FileUtil;
import com.mccabe.util.OSUtil;
import com.mccabe.util.StringUtil;
import com.mccabe.vo.Job;
import com.mccabe.vo.PCF;
import com.mccabe.vo.Program;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.mccabe.util.KyoboUtil.getMatchedFiles;

public class ReportWorks extends McCabeConfig {

    public ReportWorks(Properties properties) {
        super(properties);
    }

    private boolean existOriginalSource(PCF pcf) {
        File src = new File(pcf.getSrcDir() + fs + pcf.getSrcFile());
        return src.exists();
    }

    private ArrayList<String> makeCommandSetForReport(PCF pcf, Job job) {
        ArrayList<String> commands = new ArrayList<>();

        // purge project database
        commands.add(CLI + "purge -LEVEL all -PCF " + pcf.getPcfFile().getAbsolutePath());

        ArrayList<File> traceFiles = pcf.getAccumulateTraceFiles();
        // make cli for tracefile importing
        for (int i = 0; i < traceFiles.size(); i++) {
            commands.add(CLI + "import -instout " + traceFiles.get(i).getAbsolutePath() + " -pcf " + pcf.getPcfFile().getAbsolutePath());
        }

        // make cli for untested report : txt
        commands.add(CLI + "listing -coverage  -pcf " + pcf.getPcfFile().getAbsolutePath() + " -noparse -coverage -output " +
                REPORT_DIR + fs + job.getSysName() + fs + pcf.getProjectName().concat(".txt"));

        // make cli for coverage report : csv
        commands.add(CLI + ("metrics -ss  -ssheader -sssummary -detail branch -coverage " +
                REPORT_TEMPLATE_NAME + " -pcf " + pcf.getPcfFile().getAbsolutePath() +
                " -output " + REPORT_DIR + fs + job.getSysName() + fs + pcf.getProjectName().concat("_branch").concat(".csv")));

        // make cli for coverage report : csv
        commands.add(CLI + "metrics -ss -ssheader -sssummary -report xbat_codecov " + REPORT_TEMPLATE_NAME + " -pcf " +
                pcf.getPcfFile().getAbsolutePath() + " -output " +
                REPORT_DIR + fs + job.getSysName() + fs + pcf.getProjectName().concat("_codecov").concat(".csv"));

        // cli for publishing for 8.0
        if (job.isPublish()) {
            commands.add(CLI + "publish -pcf " + pcf.getPcfFile().getAbsolutePath() +
                    " -outputdir " + HUDSON_WEB_ROOT + fs + job.getSysName());
        }
        return commands;
    }

    private PCF initializeTraceFileSet(PCF pcf, boolean isAccumulated) throws Exception {
        if (isAccumulated) {
            pcf.setAccumulateTraceFiles(FileUtil.getFilesRecursive(new File(TRACEFILE_HOME), "", pcf.getProjectNameMinusTrId(), ".out", 0));
        } else {
            pcf.setAccumulateTraceFiles(FileUtil.getFilesRecursive(new File(TRACEFILE_HOME + fs + property.getProperty("programName")), "", pcf.getProjectName(), ".out", 0));
        }
        return pcf;
    }

    private PCF parse(File pcfFile) throws IOException {
        PCF pcf = new PCF();
        pcf.setPcfFile(pcfFile);
        Reader reader = new FileReader(pcfFile.getAbsolutePath());
        BufferedReader bufferedReader = new BufferedReader(reader);
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null && line != "") {
                if (line.startsWith("PROGRAM ")) {
                    pcf.setProjectName(line.substring(line.indexOf(" ") + 1).replaceAll("\"", ""));
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
        ReportWorks works = new ReportWorks(changeProperties(args));
        works.work();
    }

    private void work() {
        try {
            createFolder();
            Job job = new Job(property.getProperty("programName", ""));
            File projectFolder = new File(PROJECT_DIR);
            JSONArray fileList = getFileListFromJson(job, projectFolder);
            ArrayList<File> pcfFiles;
            pcfFiles = FileUtil.findPCFFilesFromProjectDir(new File(projectFolder + fs + job.getSysName()), fileList);
            FileJob defaultJob = new FileJob(job.getSysName());
            HashMap<String, FileJob> subJobList = getSubJobList();
            for (File file : pcfFiles) {
                PCF pcf = parse(file);
                if (existOriginalSource(pcf)) {
                    pcf = initializeTraceFileSet(pcf, false);
                    ArrayList<String> commands = makeCommandSetForReport(pcf, job);
                    for (String command : commands) {
                        OSUtil.executeCommand(command);
                    }
                    defaultJob.write(pcf.getProjectName());
                    doSubJobProcess(subJobList, pcf);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doSubJobProcess(HashMap<String, FileJob> subJobList, PCF pcf) throws IOException {
        for (Map.Entry<String, FileJob> entry : subJobList.entrySet()) {
            log("Subjob [" + pcf.getProjectName() + "] startWith [" + entry.getKey() + "]");
            if (pcf.getProjectName().startsWith(entry.getKey()))
                entry.getValue().write(pcf.getProjectName());
        }
    }

    private HashMap<String, FileJob> getSubJobList() throws IOException {
        HashMap<String, FileJob> subjobList = new HashMap<>();
        if (property.containsKey("subjobs") && property.getProperty("subjobs").length() > 2) {
            String raw = property.getProperty("subjobs").substring(property.getProperty("subjobs").indexOf("[") + 1, property.getProperty("subjobs").lastIndexOf("]"));
            log("[subJob property " + raw + "]");
            for (String subjob : raw.split(",")) {
                subjob = subjob.trim();
                log("[SubJob Found. " + subjob + "]");
                subjobList.put((property.getProperty("programName") + fs + subjob.trim()).replace(fs, "_"), new FileJob(property.getProperty("programName") + "_" + subjob.replace(fs, "_")));
            }
        }
        return subjobList;
    }

    private JSONArray getFileListFromJson(Job job, File projectFolder) throws ParseException, IOException {
        JSONArray fileList = null;
        Path fileList_json = Paths.get(projectFolder + fs + job.getSysName() + fs + "fileList.json");
        if (Files.exists(fileList_json)) {
            fileList = (JSONArray) new JSONParser().parse(new String(Files.readAllBytes(fileList_json), "UTF-8"));
        }
        if (fileList != null && property.containsKey("selected")) {
            log("Selected package Found. " + property.getProperty("selected"));
            return getMatchedFiles(fileList, null, property);
        }
        return fileList;
    }

    private void createFolder() throws IOException {
        String name = property.getProperty("programName");
        if (new File(REPORT_DIR + fs + name).exists() && REMOVE_REPORT_DIR) {
            System.out.println(REPORT_DIR + fs + name + " exist. delete.");
            OSUtil.executeCommand((OS.equalsIgnoreCase("windows") ? "cmd /c rmdir /Q /S " : "rm -f ") + REPORT_DIR + fs + name);
        }
        OSUtil.executeCommand((OS.equalsIgnoreCase("windows") ? "cmd /c mkdir " : "mkdir -p ") + REPORT_DIR + fs + name);
        OSUtil.executeCommand((OS.equalsIgnoreCase("windows") ? "cmd /c mkdir " : "mkdir -p ") + TRACEFILE_HOME + fs + name);
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
            this.programName =  property.getProperty("programName");
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
