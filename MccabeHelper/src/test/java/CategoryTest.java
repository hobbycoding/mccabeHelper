import com.mccabe.util.KyoboUtil;

import java.util.Map;
import java.util.Properties;

public class CategoryTest {
    public static void main(String[] args) {
        Properties properties = new Properties();
//        properties.setProperty("fullPath", "2.시스템명세모델::03.프로세스컴포넌트::퇴직보험사무처리::급부::퇴직보험거치Pbi::퇴직보험거치Pbi");
        properties.setProperty("fullPath", "2.시스템명세모델::06.배치::회계분개허브::구ERP분개허브파일송신Job::구ERP분개허브파일송신Auto");
        KyoboUtil.createContent(properties);
        for (Map.Entry entry : properties.entrySet())
            System.out.println(entry.getKey() + " : " + entry.getValue());
    }
}
