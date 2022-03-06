package com.kdy.app.bean.util;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FileCopyUploadBean {
	
	Logger logger = LoggerFactory.getLogger(FileCopyUploadBean.class);
	
	public void service(String tempPath, String movePath, String fileName) {

		// 21.01.20
		// 대용량 파일 (2GB 이상) 복사하는 로직 (temp -> move)
		// Storage 경로와 Local 경로가 다를 경우 실행
		if(!tempPath.equals(movePath)) {
			
			logger.info("tempPath :: {} ", tempPath+fileName);
			logger.info("movePath :: {} ", movePath+fileName);
			try {
				File tempFile = new File(MultiSlashChk.path(tempPath+fileName));
				File moveFileDirectory = new File(MultiSlashChk.path(movePath));
				File moveFile = new File(MultiSlashChk.path(movePath + fileName));
				
				//업로드 디렉토리 존재 유무 체크 → 없을 경우 생성
				if (!moveFileDirectory.isDirectory()) {
					moveFileDirectory.mkdirs();
				}
				
				FileUtils.copyFile(tempFile, moveFile);
				
				/*
				FileInputStream fis = new FileInputStream(tempPath + fileName);
				FileOutputStream fos = new FileOutputStream(movePath + fileName);
				FileChannel isbc = fis.getChannel();
				FileChannel ogbc = fos.getChannel();
						
				ByteBuffer buf = ByteBuffer.allocateDirect(1024);
						
				while(isbc.read(buf) != -1) {
					buf.flip();
					ogbc.write(buf);
					buf.clear();
				}
						
				if(tempFile.exists()) {
					ogbc.close();
					isbc.close();
					fos.close();
					fis.close();
							
					buf = null;
					ogbc = null;
					isbc = null;
					fos = null;
					fis = null;
				}
				*/
			} catch (Exception e) {
				logger.info(" ■■■   encodingProgress END  ■■■ ");
				logger.info(e.getMessage());
				e.printStackTrace();
				//localTempFile.delete();
				return;
			}
		}
		
	}

}
