package com.mccabe.util;

/**  

 *  

 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Vector;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.mccabe.McCabeConfig;

/**
 * 
 * @author vigilance
 * 
 * 
 */

public class SFTP_OLD extends McCabeConfig {

	public static void FTPGet(String iP, String id, String password, String localDir, String serverDir, String prefix) {

		int SFTPPORT = 22;
		Session session = null;
		Channel channel = null;
		ChannelSftp channelSftp = null;

		try {
			JSch jsch = new JSch();
			session = jsch.getSession(id, iP, SFTPPORT);
			session.setPassword(password);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
			channelSftp = (ChannelSftp) channel;
			channelSftp.cd(serverDir);

			Vector ls = channelSftp.ls(serverDir);
			if (ls != null) {
				// Iterate listing.
				for (int i = 0; i < ls.size(); i++) {
					LsEntry entry = (LsEntry) ls.elementAt(i);
					String filename = entry.getFilename();

					// Copy only directories to our new vector.
					if (!entry.getAttrs().isDir() && filename.startsWith(prefix)) {
						System.out.println("FILE FOUND!! ABOUT TO DOWNLOAD..."+serverDir+"/"+filename);
						byte[] buffer = new byte[1024];

						BufferedInputStream bis = new BufferedInputStream(channelSftp.get(filename));

						File newFile = new File(localDir + "/" + filename);

						OutputStream os = new FileOutputStream(newFile);
						BufferedOutputStream bos = new BufferedOutputStream(os);

						int readCount;

						while ((readCount = bis.read(buffer)) > 0) {
							bos.write(buffer, 0, readCount);
						}
						bis.close();
						bos.close();
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
//		System.exit(0);
		channelSftp.quit();
		session.disconnect();
	}

	public SFTP_OLD() {
	}

	public static void main(String[] args) {
//		SFTP_OLD.FTPGet("172.20.12.202", "appcm", "appcm23", "c:/temp", "/app/wcm/ttt", "WCV");
//		SFTP_OLD.FTPGet("172.20.12.202", "appcm", "appcm23", "c:/temp", REMOTE_TRACEFILES, "WCV");
		FTPTransfer.FTPGet("172.20.12.202", "appcm", "appcm23", "c:/temp", REMOTE_TRACEFILES, "WCV",false);
//		SFTP_OLD.FTPGet("172.20.12.71", "appcm", "appcm23", TRACEFILE_HOME + fs + job.getSysName(), REMOTE_TRACEFILES, job.getSysName().toUpperCase());

	}

}
