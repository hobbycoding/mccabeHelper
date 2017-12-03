package com.mccabe.report;
import java.io.File;
import java.util.ArrayList;
import java.util.Properties;

import com.mccabe.McCabeConfig;
import com.mccabe.util.FileUtil;
import com.mccabe.vo.Job;

public class WriteIndex extends McCabeConfig {

	public WriteIndex(Properties properties) {
		super(properties);
	}

	public static void main(String[] args){
		File file = new File("C:/McCabeConfig/IQ/8.1/mclog._lg");
		System.out.println(file.getAbsolutePath());
		System.out.println(file.getName());
		System.out.println(file.getParent());
		System.out.println(file.getParent().lastIndexOf("\\")+1);
		System.out.println(file.getParent().substring(file.getParent().lastIndexOf("\\")+1));
	}

	public void generateIndexHTML(Job tf){
		StringBuffer sb = new StringBuffer();
		sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Frameset//EN\" \"http://www.w3.org/TR/html4/frameset.dtd\">");
		sb.append("<html>");
		sb.append("<head>");
		sb.append("<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
		sb.append("<link type=\"image/x-icon\" href=\"images/mccabe_iq.ico\" rel=\"shortcut icon\">");
		sb.append("<title>McCabeConfig IQ Report for "+tf.getSysName()+"</title>");
		sb.append("</head>");
		sb.append("");
		sb.append("<frameset rows=\"30%,70%\">");
		sb.append("<frame name=\"dirListFrame\" src=\"list.html\">");
		sb.append("<frame name=\"detailFrame\" src=\"\">");
		sb.append("</frameset>");
		sb.append("");
		sb.append("</html>");
		FileUtil.fileOut(sb.toString(), HUDSON_WEB_ROOT+fs+tf.getSysName()+fs+"index.html", false);
	}

	public void generateListHTML(Job tf) throws Exception{
		StringBuffer sb = new StringBuffer();
		sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
		sb.append("<html>");
		sb.append("<head>");
		sb.append("<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
		sb.append("<title>McCabeConfig IQ Report list for "+tf.getSysName()+"</title>");
		sb.append("<link href=\"css/generalstyles.css\" rel=\"StyleSheet\" type=\"text/css\">");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("<table class=\"listing\" width=\"100%\" border=\"0\">");
		sb.append("<tbody>");
		sb.append("<tr>");
		sb.append("<td><b>Program Summary</b></td>");
		sb.append("</tr>");
		sb.append("</tbody>");
		sb.append("</table>");
		sb.append("<table class=\"listing\" width=\"100%\" border=\"0\">");
		sb.append("<tbody>");


		ArrayList<File> publishedIntroFiles = FileUtil.getFilesRecursive(new File(HUDSON_WEB_ROOT+fs+tf.getSysName()), "", "alldir-detail.html", ".html", 0);
		String programName = "";
		for (File file : publishedIntroFiles) {
			
//			/ciserv/tomcat6/webapps/ROOT/ecm/ecm_ECMJava_JavaSource_scourt_ecm_vo_ECM01a0Vo/alldir-detail.html
//			<td><a target="detailFrame" href="ecm_ECMJava_JavaSource_scourt_ecm_dao_EPRDao/alldir-detail.html">Files</a>
			programName = file.getParent().substring(file.getParent().lastIndexOf(fs)+1);
			sb.append("<tr>");
			sb.append("<td><a target=\"detailFrame\" href=\""+programName+fs+"alldir-detail.html\">"+programName+"</a>");
			sb.append("</td>");
			sb.append("</tr>");
		}


		sb.append("</tbody>");
		sb.append("</table>");
		sb.append("</body>");
		sb.append("</html>");

		FileUtil.fileOut(sb.toString(), HUDSON_WEB_ROOT+fs+tf.getSysName()+fs+"list.html", false);
	}

}
