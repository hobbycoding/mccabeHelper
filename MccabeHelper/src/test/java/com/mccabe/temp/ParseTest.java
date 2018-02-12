package com.mccabe.temp;
import java.io.File;
import java.io.IOException;

public class ParseTest {
    public static void main(String[] args) throws IOException {
        String path = "/Users/hyoju/Downloads/view/files/DEV-deploy_kv3_app_cor-TEST-COVERAGE_src_kv3_batch_arp_job_fpvtlacomgt_fpvtlacoinatrtnprcjob_FpVtlAcoInatRtnPrcManual";
        DBInsert.SourceFile file = new DBInsert.SourceFile(new File(""));
        file.parseReportCSVFile(path);
    }
}
