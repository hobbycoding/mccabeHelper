package com.mccabe.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;

import com.mccabe.McCabeConfig;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class FTPTransfer extends McCabeConfig {
	// 여러개의 파일을 전송한다.
//	public boolean FtpPut(String ip, int port, String id, String password, String uploaddir, String makedir, List files) {
//		boolean result = false;
//		FTPClient ftp = null;
//		int reply = 0;
//
//		try {
//			ftp = new FTPClient();
//			ftp.connect(ip, port);
//
//			reply = ftp.getReplyCode();
//			if (!FTPReply.isPositiveCompletion(reply)) {
//				ftp.disconnect();
//				return result;
//			}
//
//			if (!ftp.login(id, password)) {
//				ftp.logout();
//				return result;
//			}
//			ftp.setFileType(FTP.BINARY_FILE_TYPE);
//			ftp.enterLocalPassiveMode();
//			ftp.changeWorkingDirectory(uploaddir);
//			ftp.makeDirectory(makedir);
//			ftp.changeWorkingDirectory(makedir);
//
//			for (int i = 0; i < files.size(); i++) {
//				String sourceFile = (String) files.get(i); // 디렉토리+파일명
//				File uploadFile = new File(sourceFile);
//				FileInputStream fis = null;
//				try {
//					fis = new FileInputStream(uploadFile);
//					boolean isSuccess = ftp.storeFile(uploadFile.getName(), fis);
//					if (isSuccess) {
//						System.out.println(sourceFile + " 파일 FTP 업로드 성공");
//					}
//				} catch (IOException ioe) {
//					ioe.printStackTrace();
//				} finally {
//					if (fis != null) {
//						try {
//							fis.close();
//						} catch (IOException ioe) {
//							ioe.printStackTrace();
//						}
//					}
//				}
//			}
//
//			ftp.logout();
//			result = true;
//
//		} catch (SocketException se) {
//			se.printStackTrace();
//		} catch (IOException ioe) {
//			ioe.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (ftp != null && ftp.isConnected()) {
//				try {
//					ftp.disconnect();
//				} catch (IOException e) {
//				}
//			}
//		}
//		return result;
//	}
//
//	// 파일을 받는다.
//	public boolean FtpGet(String ip, int port, String id, String password, String localdir, String serverdir, String fileName) {
//		boolean result = false;
//		FTPClient ftp = null;
//		int reply = 0;
//
//		try {
//			ftp = new FTPClient();
//
//			reply = ftp.getReplyCode();
//			if (!FTPReply.isPositiveCompletion(reply)) {
//				ftp.disconnect();
//				return result;
//			}
//
//			if (!ftp.login(id, password)) {
//				ftp.logout();
//				return result;
//			}
//			ftp.setFileType(FTP.BINARY_FILE_TYPE);
//			ftp.enterLocalPassiveMode();
//			ftp.changeWorkingDirectory(serverdir);
//			File f = new File(localdir, fileName);
//			FileOutputStream fos = null;
//			try {
//				fos = new FileOutputStream(f);
//				boolean isSuccess = ftp.retrieveFile(fileName, fos);
//				if (isSuccess) {
//					System.out.println("다운로드 성공");
//				} else {
//					System.out.println("다운로드 실패");
//				}
//			} catch (IOException ioe) {
//				ioe.printStackTrace();
//			} finally {
//				if (fos != null)
//					try {
//						fos.close();
//					} catch (IOException ex) {
//					}
//			}
//			ftp.logout();
//		} catch (SocketException se) {
//			se.printStackTrace();
//		} catch (IOException ioe) {
//			ioe.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (ftp != null && ftp.isConnected()) {
//				try {
//					ftp.disconnect();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		return result;
//	}

	public void FTPPut() {
		FTPClient ftp = null;

		try {
			ftp = new FTPClient();
			ftp.setControlEncoding("UTF-8");

			// 접속 및 로그인
			ftp.connect("160.61.8.102");

			if( !ftp.login("mccabe", "password") ) {
				System.err.println("LOGIN FAILED!!!");
				ftp.disconnect();
				return;
			}

			// 작업 디렉토리 변경
			if( !ftp.changeWorkingDirectory("/mccabe/tracefiles") ) {
				System.err.println("Can not change dir!!");
				ftp.logout();
				ftp.disconnect();
				return;
			}

			// 업로드 테스트
			FileInputStream fis = null;
			String filePath = "d:/temp/passwd";//<UPLOAD_FILE_PATH>;

			try {
				fis = new FileInputStream(filePath);

				if( !ftp.storeFile("passwd", fis) ) {
					System.out.println("UPLOAD FAILED!!");
				}

			} catch(FileNotFoundException fnfe) {
				System.err.println("인풋 파일을 만들 수 없었습니다!! 경로명 : " + filePath);
			}

			ftp.logout();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ftp != null && ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException e) {

				}
			}
		}
	}
	public static void FTPGet(String iP,String id,String password,String localDir,String serverDir,String prefix, boolean isCut) {
		FTPClient ftp = null;
	
		try {
			ftp = new FTPClient();
//			ftp.setControlEncoding("UTF-8");
	
			// 접속 및 로그인
			ftp.connect(iP);
	
			if( !ftp.login(id, password) ) {
				System.err.println("LOGIN FAILED!!!");
				ftp.disconnect();
				return;
			}
	
			// 작업 디렉토리 변경
			if( !ftp.changeWorkingDirectory(serverDir) ) {
				System.err.println("Can not change dir!!");
				ftp.logout();
				ftp.disconnect();
				return;
			}
	
			FTPFile [] fileList = ftp.listFiles();
	
			// 파일 리스팅 & 다운로드 테스트
			for( int i = 0 ; i < fileList.length ; i++ ) {
				FTPFile file = fileList[i];
	
	
				if( file.isFile() && file.getName().startsWith(prefix)) {
					// FTP에서 넘어오는 파일 정보를 파싱하지 않고 그대로 봄
					System.out.println(file.getRawListing());
					// 다운로드 테스트
					System.out.println("FILE FOUND!! ABOUT TO DOWNLOAD...");
	
					FileOutputStream fos = null;
//					String filePath = localDir + ftp.printWorkingDirectory()+ "/" + file.getName();
					String filePath = localDir + "/" + file.getName();
	
					try {
						fos = new FileOutputStream(filePath);
	
						if( !ftp.retrieveFile(file.getName(), fos) ) {
							System.out.println("DOWNLOAD FAILED!!");
						}else{
							if(isCut){
								System.out.println(file.getName()+"-file deletion is "+ftp.deleteFile(file.getName()));
							}
						}
					} catch(FileNotFoundException fnfe) {
						System.err.println("아웃풋 파일을 만들 수 없었습니다!! 경로명 : " + filePath);
					}
				}
			}
	
			ftp.logout();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ftp != null && ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException e) {
	
				}
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			FTPTransfer.FTPGet("172.20.12.201", "appcm", "appcm23", "c:\\temp", REMOTE_TRACEFILES, "WCV",false);
			FTPTransfer.FTPGet("172.20.12.202", "appcm", "appcm23", "c:\\temp", REMOTE_TRACEFILES, "WCV",false);
			FTPTransfer.FTPGet("172.20.12.71", "appeif", "appeif23", "c:\\temp", REMOTE_TRACEFILES, "WCV",false);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
