package com.mccabe;

import com.mccabe.inst.McProcess;
import com.mccabe.report.ReportWorks;
import com.mccabe.temp.DBInsert;
import com.mccabe.temp.DeletesDataFromFile;
import com.mccabe.temp.PathVecChanger;
import com.mccabe.util.LibClassLoader;
import com.mccabe.util.PackageAdder;
import com.mccabe.util.SFTP;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("uses. [properties path] | [inst | sftp | report | pathvec | PackageAdd | insertDB]");
            System.exit(0);
        }
        LibClassLoader.loadJarIndDir();
        switch (args[1]) {
            case "inst" :
                McProcess.main(args);
                break;
            case "report" :
                ReportWorks.main(args);
                break;
            case "sftp" :
                SFTP.main(args);
                break;
            case "pathvec" :
                PathVecChanger.main(args);
                break;
            case "PackageAdd" :
                PackageAdder.main(args);
                break;
            case "insertDB" :
                DBInsert.main(args);
                break;
            case "Deletes_the_file_data_of_the_last_file_by_fileList" :
                DeletesDataFromFile.main(args);
                break;
            default: System.out.println("uses. [inst | sftp | report | pathvec | PackageAdd | insertDB]");
        }
    }
}
