package com.kdy.live.service.system;

import java.text.NumberFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kdy.live.bean.util.system.SystemConfigSelectBean;
import com.kdy.live.dto.LiveSchedMemoryVO;
import com.kdy.live.dto.system.SystemConfigVO;

@Component
public class SystemConfigUpdateService {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private final SystemConfigSelectBean systemConfigSelectBean;
	private final LiveSchedMemoryVO memoryVO;
	
	@Autowired
	public SystemConfigUpdateService(SystemConfigSelectBean systemConfigSelectBean, LiveSchedMemoryVO memoryVO) {
		this.systemConfigSelectBean = systemConfigSelectBean;
		this.memoryVO = memoryVO;
	}
	
	public Boolean updateSystemConfig() {
		
		Boolean retval = true;
		SystemConfigVO svo = null;
		
		try {
			
			svo = systemConfigSelectBean.getSystemConfig();
			
			if(svo == null) {
				logger.warn("### SystemConfigVO is NULL, Select SystemConfig FAILED ###");
				return false;
			}
			
			memoryVO.setLiveFileLocalPath(svo.getLiveFileLocalPath());
			memoryVO.setEncodingVodFilePath(svo.getEncodingVodFilePath());
			memoryVO.setEtcFileUploadPath(svo.getEtcFileUploadPath());
			memoryVO.setReplaceRootPath(svo.getReplaceRootPath());
			memoryVO.setLiveStreamingUri(svo.getLiveStreamingUri());
			memoryVO.setVodStreamingUri(svo.getVodStreamingUri());
			memoryVO.setThumbnailCnt(svo.getThumbnailCnt());
			memoryVO.setThumbnailTime(svo.getThumbnailTime());
			memoryVO.setThumbnailFormat(svo.getThumbnailFormat());
			memoryVO.setThumbnailFormat(svo.getThumbnailFormat());
			memoryVO.setVodOriginalFilePath(svo.getVodOriginalFilePath());
			memoryVO.setVodTempFilePath(svo.getVodTempFilePath());
			memoryVO.setDefaultThumbnail(svo.getDefaultThumbnail());
			memoryVO.setLiveFileNasPath(svo.getLiveFileNasPath());
			
			int workerCnt = svo.getEncoderWorkerCnt() != null ? svo.getEncoderWorkerCnt() : 5;
			int serverCnt = svo.getEncoderServerCnt() != null ? svo.getEncoderServerCnt() : 1;
			int maxWorkerThreadCnt = (int) Math.floor((double)workerCnt/serverCnt) + 1;
			logger.info("worker[{}], server[{}], max-worker-thread[{}]", workerCnt, serverCnt, maxWorkerThreadCnt);
			memoryVO.setMaxWorkerThreadCnt(maxWorkerThreadCnt);
			
		} catch(Exception e) {
			logger.error(e.getMessage());
			for(StackTraceElement st : e.getStackTrace()) {
				if(st.toString().startsWith("com.kdy.live")) {
					logger.error(st.toString());
				}
			}
			retval = false;
		}
		
		return retval;
	}
	
	public void printSystemStatus() {
		
		StringBuilder sb = new StringBuilder();
		Runtime runtime = Runtime.getRuntime();;
		NumberFormat format =  NumberFormat.getInstance();
		
		long maxMemory = runtime.maxMemory();
		long allocatedMemory = runtime.totalMemory();
		long freeMemory = runtime.freeMemory();
		
		logger.debug("[Thread :: " + Thread.currentThread().getName() + " ]");
		sb.append("\nfree memory: " + format.format(freeMemory / 1024) + "\n");
		sb.append("allocated memory: " + format.format(allocatedMemory / 1024) + "\n");
		sb.append("max memory: " + format.format(maxMemory / 1024) + "\n");
		sb.append("total free memory: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024) + "\n");
		
		logger.info(sb.toString());
		sb = null;
	}
}
