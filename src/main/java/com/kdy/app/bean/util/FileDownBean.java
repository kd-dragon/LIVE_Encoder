package com.kdy.app.bean.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileDownBean {
	private static final Logger logger = LoggerFactory.getLogger(FileDownBean.class);

	public static boolean fileDownload(String filePath, String downFileName, HttpServletResponse response) throws Exception {
		
		FileInputStream fileInputStream = null;
		boolean retval = false;
		
		try {
			// 1. 파일 가져오기
			File downloadFile = new File(MultiSlashChk.path(filePath));
			
			// 2. 파일 존재 여부 검사
			if (!downloadFile.isFile()) {
				return retval;
			} 
			
			// 3. 파일명 UTF-8 Encoding
			String encFileName = URLEncoder.encode(downFileName, "UTF-8");

			// 3. Response 설정
	        response.setHeader("Content-Disposition", "attachment; filename=\"" + encFileName + "\";");
	        response.setHeader("Content-Transfer-Encoding", "binary"); 
	        
	        // 4. FileDownload
	        fileInputStream = new FileInputStream(downloadFile); 
	        OutputStream outputStream = response.getOutputStream();
	        
	        int readCount = 0;
            byte[] buffer = new byte[1024];
            
            while ((readCount = fileInputStream.read(buffer)) != -1) {
            	outputStream.write(buffer, 0, readCount);
            }
            retval = true;
            
		} catch (Exception e) {
			retval = false;
			e.printStackTrace();
			logger.error(e.getMessage());
		} finally {
			if(fileInputStream != null) {
				fileInputStream.close();
			}
		}
		return retval;
	}
}
