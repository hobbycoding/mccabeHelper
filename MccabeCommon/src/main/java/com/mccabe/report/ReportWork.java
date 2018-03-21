package com.mccabe.report;

import com.mccabe.Mccabe;
import com.mccabe.util.CommandUtil;
import com.mccabe.util.FileUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import static com.mccabe.Mccabe.McCABE_PATH.*;
import static com.mccabe.Mccabe.McCABE_Properties.*;

public class ReportWork extends Mccabe {
    public static void main(String[] args) throws Exception {
        checkAndSetProperties(args);
        ReportWork reportWork = new ReportWork();
        reportWork.start();
    }

    public void start() {
        try {
            createReportFolder();
            Job job = new Job(programName.getString());
            File projectFolder = new File(PROJECT_DIR.getPath());
            ArrayList<File> pcfFiles = FileUtil.findPCFFilesFromProjectDir(new File(projectFolder.getPath()));

            for (File file : pcfFiles) {
                PCFObject pcf = parse(file);
                if (existOriginalSource(pcf)) {
                    pcf = initializeTraceFileSet(pcf, false);
                    ArrayList<String> commands = makeCommandSetForReport(pcf, job);
                    for (String command : commands) {
                        CommandUtil.runNormalCommand(command);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void createReportFolder() throws Exception {
        String name = McCABE_Properties.programName.getString();
        if (new File(REPORT_DIR.getPath() + fs + name).exists() && removeReportDir.getBoolean()) {
            logger.debug(REPORT_DIR.getPath() + fs + name + " exist. delete.");
            CommandUtil.runNormalCommand((McCABE_Properties.isWindows.getBoolean() ?
                    "cmd /c rmdir /Q /S " : "rm -f ") + REPORT_DIR.getPath() + fs + name);
        }
        CommandUtil.runNormalCommand((McCABE_Properties.isWindows.getBoolean() ?
                "cmd /c mkdir " : "mkdir -p ") + REPORT_DIR.getPath() + fs + name);
        CommandUtil.runNormalCommand((McCABE_Properties.isWindows.getBoolean() ?
                "cmd /c mkdir " : "mkdir -p ") + TRACEFILE_HOME.getPath() + fs + name);
    }

    private PCFObject initializeTraceFileSet(PCFObject pcf, boolean isAccumulated) throws Exception {
        if (isAccumulated) {
            pcf.setAccumulateTraceFiles(FileUtil.getFilesRecursive(new File(TRACEFILE_HOME.getPath()), "", pcf.getProjectNameMinusTrId(), ".out", 0));
        } else {
            pcf.setAccumulateTraceFiles(FileUtil.getFilesRecursive(new File(TRACEFILE_HOME + fs + programName.getString()), "", pcf.getProjectName(), ".out", 0));
        }
        return pcf;
    }

    private boolean existOriginalSource(PCFObject pcf) {
        File src = new File(pcf.getSrcDir() + fs + pcf.getSrcFile());
        return src.exists();
    }

    private ArrayList<String> makeCommandSetForReport(PCFObject pcf, Job job) throws Exception {
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
                REPORT_DIR.getPath() + fs + job.getSysName() + fs + pcf.getProjectName().concat(".txt"));

        // make cli for coverage report : csv
        commands.add(CLI + ("metrics -ss  -ssheader -sssummary -detail branch -coverage " +
                reportTemplateName.getString() + " -pcf " + pcf.getPcfFile().getAbsolutePath() +
                " -output " + REPORT_DIR + fs + job.getSysName() + fs + pcf.getProjectName().concat("_branch").concat(".csv")));

        // make cli for coverage report : csv
        commands.add(CLI + "metrics -ss -ssheader -sssummary -report xbat_codecov " + reportTemplateName.getString() + " -pcf " +
                pcf.getPcfFile().getAbsolutePath() + " -output " +
                REPORT_DIR + fs + job.getSysName() + fs + pcf.getProjectName().concat("_codecov").concat(".csv"));

        // cli for publishing for 8.0
        if (job.isPublish()) {
            commands.add(CLI + "publish -pcf " + pcf.getPcfFile().getAbsolutePath() +
                    " -outputdir " + fs + job.getSysName());
        }
        return commands;
    }

    public class Job {

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

        public Job(String programName) {
            this.sysName = programName;
        }

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

    private PCFObject parse(File pcfFile) throws IOException {
        PCFObject pcf = new PCFObject();
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
}
