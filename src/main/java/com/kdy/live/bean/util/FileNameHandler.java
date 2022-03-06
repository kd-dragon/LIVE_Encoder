package com.kdy.live.bean.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileNameHandler {
	
	private static Logger logger = LoggerFactory.getLogger(FileNameHandler.class);
	
	public static String fileNameWithoutFormat(String _fileName) {
		int idx = _fileName.lastIndexOf(".");
		if(idx > 0) {
			if(checkFileFormat(_fileName)) {
				logger.info("fileNameWithoutFormat('" + _fileName + "') will remove a format name");
				return _fileName.substring(0, idx);
			} else {
				logger.warn("fileNameWithoutFormat('" + _fileName + "') Doesn't have a format name");
				return _fileName;
			}
		} else {
			logger.warn("fileNameWithoutFormat('" + _fileName + "') Doesn't have a format name at all");
			return _fileName;
		}
	}
	
	public static String fileNameWithFormat(String _fileName, String _format) {
		int idx = _fileName.lastIndexOf(".");
		if(idx > 0) {
			if(checkFileFormat(_fileName)) {
				logger.info("fileNameWithFormat('" + _fileName + "') already have a format name");
				return _fileName;
			} else {
				logger.info("fileNameWithFormat('" + _fileName + "') will be added a format name");
				return _fileName + "." + _format;
			}
		} else {
			logger.warn("fileNameWithFormat('" + _fileName + "') will be added a format name");
			return _fileName + "." + _format;
		}
	}
	
	public static boolean checkFileFormat(String _fileName) {
		String tmpFileName = _fileName.toLowerCase();
		boolean isFormatOk = false;
		if(tmpFileName.endsWith("doc") || tmpFileName.endsWith("ppt") || tmpFileName.endsWith("pptx") 
				|| tmpFileName.endsWith("xls") || tmpFileName.endsWith("xlsx") || tmpFileName.endsWith("pdf")
				|| tmpFileName.endsWith("hwp") || tmpFileName.endsWith("txt") || tmpFileName.endsWith("bmp") 
				|| tmpFileName.endsWith("png") || tmpFileName.endsWith("gif") || tmpFileName.endsWith("mp3")
				|| tmpFileName.endsWith("tiff") || tmpFileName.endsWith("mp4") || tmpFileName.endsWith("wmv") 
				|| tmpFileName.endsWith("tif") || tmpFileName.endsWith("zip") || tmpFileName.endsWith("egg")
				|| tmpFileName.endsWith("docx") || tmpFileName.endsWith("xml") || tmpFileName.endsWith("rar")
				|| tmpFileName.endsWith("7z") || tmpFileName.endsWith("dotx") || tmpFileName.endsWith("docm")
				|| tmpFileName.endsWith("hwt") || tmpFileName.endsWith("html") || tmpFileName.endsWith("htm")
				|| tmpFileName.endsWith("rtf") || tmpFileName.endsWith("raw") || tmpFileName.endsWith("jpg")
				|| tmpFileName.endsWith("exe") || tmpFileName.endsWith("jar") || tmpFileName.endsWith("cell")
				|| tmpFileName.endsWith("avi") || tmpFileName.endsWith("flv") || tmpFileName.endsWith("webm")
				|| tmpFileName.endsWith("mkv") || tmpFileName.endsWith("swf") || tmpFileName.endsWith("tp")
				|| tmpFileName.endsWith("ts") || tmpFileName.endsWith("mov") || tmpFileName.endsWith("asf")
				) {
			isFormatOk = true;
		}
		
		return isFormatOk;
	}
}
