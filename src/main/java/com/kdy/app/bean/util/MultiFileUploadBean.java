package com.kdy.app.bean.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.kdy.app.dto.util.FileVO;
import com.kdy.live.dto.LiveSchedMemoryVO;


@Component
public class MultiFileUploadBean {
	
	Logger logger = LoggerFactory.getLogger(MultiFileUploadBean.class);
	
	private final LiveSchedMemoryVO memoryVO;
	
	@Autowired
	public MultiFileUploadBean(LiveSchedMemoryVO memoryVO) {
		this.memoryVO = memoryVO;
	}
	
	private String FILE_UPLOAD_PATH;
	
	public List<FileVO> service(List<MultipartFile> uploadFile, String fileAddPath) throws Exception{
		FILE_UPLOAD_PATH = memoryVO.getEtcFileUploadPath();
		String filePath = FILE_UPLOAD_PATH + fileAddPath;
		
		List<FileVO> list = new ArrayList<FileVO>();
		FileVO fileVo = null;
		RandomGUID myGUID = null;
		File destFile = null;
		
		File dir = new File(MultiSlashChk.path(filePath));
		
		if (!dir.isDirectory()) {
			dir.mkdirs();
		}
		
		int fileNum = 1;
		for (MultipartFile mf : uploadFile) {
			String originalFileName = new String(mf.getOriginalFilename().getBytes("UTF-8"), "UTF-8"); // 원본 파일 명 
			
			if ("".equals(originalFileName)) {
				fileNum ++;
				continue;
			}
			
			myGUID = new RandomGUID();
			String ext = originalFileName.substring(originalFileName.lastIndexOf('.') + 1); //확장자
			ext = ext.toLowerCase();

			List<String> extArray = new ArrayList<String>();
			extArray.add("gif");
			extArray.add("jpg");
			extArray.add("jpeg");
			extArray.add("png");
			extArray.add("bmp");
			extArray.add("tiff");
			
			extArray.add("txt");

			extArray.add("cell");
			extArray.add("hwp");

			extArray.add("doc");
			extArray.add("docx");
			extArray.add("xls");
			extArray.add("xlsx");
			extArray.add("ppt");
			extArray.add("pptx");

			extArray.add("pdf");
			extArray.add("pdf");

			extArray.add("zip");

			if (extArray.indexOf(ext) == -1) {
				logger.error("File Extension Error > "+ext);
				return null;
			}
			
			String serverFileName = myGUID.toString() + "." + ext; //서버이름

			String uploadFileFullPath = filePath + "/" + serverFileName;
			
			destFile = new File(MultiSlashChk.path(uploadFileFullPath));
			mf.transferTo(destFile);

			fileVo = new FileVO();
			fileVo.setFilePath(MultiSlashChk.path(filePath + "/"));
			fileVo.setFileName(originalFileName);
			fileVo.setFileNameServer(destFile.getName());
			fileVo.setFileIdx(fileNum);
			//fileVo.setFileSize(destFile.length());
			//fileVo.setDestFile(destFile);

			list.add(fileVo);
			
			fileNum ++; 
		}
		
		return list;
	}
	
	public FileVO service(MultipartFile uploadFile, String fileAddPath) throws Exception{

		String filePath = FILE_UPLOAD_PATH + fileAddPath;
		
		FileVO fileVo = null;
		RandomGUID myGUID = null;
		File destFile = null;
		
		File dir = new File(MultiSlashChk.path(filePath));
		
		if (!dir.isDirectory()) {
			dir.mkdirs();
		}
		
			String originalFileName = new String(uploadFile.getOriginalFilename().getBytes("UTF-8"), "UTF-8"); // 원본 파일 명 
			
			if ("".equals(originalFileName)) {
				return null;
			}
			
			myGUID = new RandomGUID();
			String ext = originalFileName.substring(originalFileName.lastIndexOf('.') + 1); //확장자
			ext = ext.toLowerCase();

			List<String> extArray = new ArrayList<String>();
			extArray.add("gif");
			extArray.add("jpg");
			extArray.add("jpeg");
			extArray.add("png");
			extArray.add("bmp");
			extArray.add("tiff");

			extArray.add("txt");
			
			extArray.add("cell");
			extArray.add("hwp");

			extArray.add("doc");
			extArray.add("docx");
			extArray.add("xls");
			extArray.add("xlsx");
			extArray.add("ppt");
			extArray.add("pptx");

			extArray.add("pdf");
			extArray.add("pdf");

			extArray.add("zip");

			if (extArray.indexOf(ext) == -1) {
				logger.error("File Extension Error > "+ext);
				return null;
			}
			
			String serverFileName = myGUID.toString() + "." + ext; //서버이름

			String uploadFileFullPath = filePath + "/" + serverFileName;
			
			destFile = new File(MultiSlashChk.path(uploadFileFullPath));
			uploadFile.transferTo(destFile);

			fileVo = new FileVO();
			fileVo.setFilePath(MultiSlashChk.path(filePath + "/"));
			fileVo.setFileName(originalFileName);
			fileVo.setFileNameServer(destFile.getName());
			//fileVo.setFileSize(destFile.length());
			//fileVo.setDestFile(destFile);

		
		return fileVo;
	}
	
	public FileVO fullPathUpload(MultipartFile uploadFile, String filefullPath) throws Exception{

		FileVO fileVo = null;
		RandomGUID myGUID = null;
		File destFile = null;
		
		File dir = new File(MultiSlashChk.path(filefullPath));
		
		if (!dir.isDirectory()) {
			dir.mkdirs();
		}
		
			String originalFileName = new String(uploadFile.getOriginalFilename().getBytes("UTF-8"), "UTF-8"); // 원본 파일 명 
			
			if ("".equals(originalFileName)) {
				return null;
			}
			
			myGUID = new RandomGUID();
			String ext = originalFileName.substring(originalFileName.lastIndexOf('.') + 1); //확장자
			ext = ext.toLowerCase();

			List<String> extArray = new ArrayList<String>();
			extArray.add("gif");
			extArray.add("jpg");
			extArray.add("jpeg");
			extArray.add("png");
			extArray.add("bmp");
			extArray.add("tiff");

			extArray.add("txt");
			
			extArray.add("cell");
			extArray.add("hwp");

			extArray.add("doc");
			extArray.add("docx");
			extArray.add("xls");
			extArray.add("xlsx");
			extArray.add("ppt");
			extArray.add("pptx");

			extArray.add("pdf");
			extArray.add("pdf");

			extArray.add("zip");

			if (extArray.indexOf(ext) == -1) {
				logger.error("File Extension Error > "+ext);
				return null;
			}
			
			String serverFileName = myGUID.toString() + "." + ext; //서버이름

			String uploadFileFullPath = filefullPath + "/" + serverFileName;
			
			destFile = new File(MultiSlashChk.path(uploadFileFullPath));
			uploadFile.transferTo(destFile);

			fileVo = new FileVO();
			fileVo.setFilePath(MultiSlashChk.path(filefullPath + "/"));
			fileVo.setFileName(originalFileName);
			fileVo.setFileNameServer(destFile.getName());
			//fileVo.setFileSize(destFile.length());
			//fileVo.setDestFile(destFile);

		
		return fileVo;
	}
	
}
