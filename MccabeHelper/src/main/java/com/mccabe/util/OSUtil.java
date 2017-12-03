package com.mccabe.util;

import java.io.*;

import com.mccabe.McCabeConfig;

public class OSUtil {

    /**
     * @param command
     * @throws IOException
     */
    public static void executeCommand(String command) throws IOException {
        System.out.println(command);
        BufferedReader br;
        String line;
        Process proc = Runtime.getRuntime().exec(command);
        // Process proc =
        // Runtime.getRuntime().exec("/usr/java5_64/bin/javac -nowarn -verbose -encoding MS949 -J-Xms5120m -J-Xmx5120m -J-Xmn2560m -J-Xss1m -classpath /star/transfer/stage/lib/BKLIB_Cache.jar:/star/transfer/stage/lib/EAIFTE.jar:/star/transfer/stage/lib/EAIFTEWrapper.jar:/star/transfer/stage/lib/EAIMQWrapper.jar:/star/transfer/stage/lib/FramePlus4_Batch.jar:/star/transfer/stage/lib/FramePlus4_Core.jar:/star/transfer/stage/lib/INICrypto_v3.1.8_signed.jar:/star/transfer/stage/lib/LIB_Cache.jar:/star/transfer/stage/lib/LIB_Cphr.jar:/star/transfer/stage/lib/LIB_Filter.jar:/star/transfer/stage/lib/MQWrapper.jar:/star/transfer/stage/lib/MQWrapper_sdk.jar:/star/transfer/stage/lib/TyibAppMessage.jar:/star/transfer/stage/lib/WMQFTE.jar:/star/transfer/stage/lib/WTCWrapper.jar:/star/transfer/stage/lib/Wrapper.jar:/star/transfer/stage/lib/activation.jar:/star/transfer/stage/lib/api.jar:/star/transfer/stage/lib/commons-codec-1.3.jar:/star/transfer/stage/lib/commons-net-2.0.jar:/star/transfer/stage/lib/commons-net-ftp-2.0.jar:/star/transfer/stage/lib/external-debug.jar:/star/transfer/stage/lib/fteNotice.jar:/star/transfer/stage/lib/issacweb.jar:/star/transfer/stage/lib/jdom.jar:/star/transfer/stage/lib/kftc.jar:/star/transfer/stage/lib/mail.jar:/star/transfer/stage/lib/mina.jar:/star/transfer/stage/lib/nls-debug.jar:/star/transfer/stage/lib/opencsv-2.2.jar:/star/transfer/stage/lib/poi-3.6-20091214.jar:/star/transfer/stage/lib/rulesFacade.jar:/star/transfer/stage/lib/sign.jar:/star/transfer/stage/lib/unetBKExtDec.jar:/star/transfer/stage/lib/unetBKExtEnc.jar:/star/transfer/stage/lib/xalan.jar:/star/transfer/stage/lib/yessignMasterKey.jar:/star/transfer/stage/lib/BKLIB_Cache.jar:/star/transfer/stage/lib/EAIFTE.jar:/star/transfer/stage/lib/EAIFTEWrapper.jar:/star/transfer/stage/lib/EAIMQWrapper.jar:/star/transfer/stage/lib/FramePlus4_Batch.jar:/star/transfer/stage/lib/FramePlus4_Core.jar:/star/transfer/stage/lib/INICrypto_v3.1.8_signed.jar:/star/transfer/stage/lib/LIB_Cache.jar:/star/transfer/stage/lib/LIB_Cphr.jar:/star/transfer/stage/lib/LIB_Filter.jar:/star/transfer/stage/lib/MQWrapper.jar:/star/transfer/stage/lib/MQWrapper_sdk.jar:/star/transfer/stage/lib/TyibAppMessage.jar:/star/transfer/stage/lib/WMQFTE.jar:/star/transfer/stage/lib/WTCWrapper.jar:/star/transfer/stage/lib/Wrapper.jar:/star/transfer/stage/lib/activation.jar:/star/transfer/stage/lib/api.jar:/star/transfer/stage/lib/commons-codec-1.3.jar:/star/transfer/stage/lib/commons-net-2.0.jar:/star/transfer/stage/lib/commons-net-ftp-2.0.jar:/star/transfer/stage/lib/external-debug.jar:/star/transfer/stage/lib/fteNotice.jar:/star/transfer/stage/lib/issacweb.jar:/star/transfer/stage/lib/jdom.jar:/star/transfer/stage/lib/kftc.jar:/star/transfer/stage/lib/mail.jar:/star/transfer/stage/lib/mina.jar:/star/transfer/stage/lib/nls-debug.jar:/star/transfer/stage/lib/opencsv-2.2.jar:/star/transfer/stage/lib/poi-3.6-20091214.jar:/star/transfer/stage/lib/rulesFacade.jar:/star/transfer/stage/lib/sign.jar:/star/transfer/stage/lib/unetBKExtDec.jar:/star/transfer/stage/lib/unetBKExtEnc.jar:/star/transfer/stage/lib/xalan.jar:/star/transfer/stage/lib/yessignMasterKey.jar:/mccabe/tr/temp/common:/mccabe/tr/temp/apps/AP_CORE:/star/transfer/stage/deploy/common:/star/transfer/stage/deploy/apps/AP_CORE: -sourcepath /mccabe/tr/temp/apps/AP_CORE common/com/tyib/cd/sp/dto/bdo/busnbnsmng/dtyuntmng/GdsPrftRcgtBDO.java apps/AP_CORE/com/tyib/cd/sp/dc/busnbnsmng/EmpOtcmMngDM/BusnEmpMpayPtclDAO.java");

        br = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
        br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
        try {
            proc.waitFor();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        proc.destroy();
    }
}
