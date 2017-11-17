package com.mccabe;

import com.mccabe.inst.McProcess;
import com.mccabe.report.ReportWorks;
import com.mccabe.temp.PathVecChanger;
import com.mccabe.util.PackageAdder;
import com.mccabe.util.SFTP;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("uses. [properties path] | [sftp | report | pathvec | PackageAdd]");
            System.exit(0);
        }
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
            default: System.out.println("uses. [sftp | report | pathvec | PackageAdd]");
        }
    }
}
