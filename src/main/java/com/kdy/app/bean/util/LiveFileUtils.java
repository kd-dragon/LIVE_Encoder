package com.kdy.app.bean.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kdy.live.bean.util.code.StreamType;
import com.kdy.live.dto.LiveSchedMemoryVO;
import com.kdy.live.dto.live.LiveBroadcastVO;

public class LiveFileUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(LiveFileUtils.class);
	
	public static LiveBroadcastVO initializeLiveInfo(LiveBroadcastVO lbvo, LiveSchedMemoryVO memoryVO) throws Exception {
		
		// rtsp, rtmp 사용시 hls file 경로 설정
		lbvo.setHlsFilePath(getHlsFilePath(lbvo, memoryVO.getLiveFileLocalPath()));
		
		// 영상 녹화시 vod 저장 경로 설정
		if(lbvo.getLbVodSaveYn().equalsIgnoreCase("Y")) {
			
			String year = new SimpleDateFormat("yyyy").format(new Date());
			String month = new SimpleDateFormat("MM").format(new Date());
			
			//녹화파일 저장경로
			//(recordPath=D:/workspace/MMCA_Movie_3.6.1/src/main/webapp)/upload/encoding/video/2021/08/
			lbvo.setVodSavePath(MultiSlashChk.path(memoryVO.getEncodingVodFilePath() + "encoding/video/"+ year+"/"+month+"/"));
			
			//녹화파일을 스트리밍하기위해 복사할 경로
			lbvo.setRecordCopyPath(MultiSlashChk.path(lbvo.getVodSavePath()+lbvo.getLbSeq() + "/")); // >>DB 저장 값
			//lbvo.setRecordCopyPath(memoryVO.getRecordingFilePath()+lbvo.getLbSeq() + "/");

			//WebRoot
			lbvo.setReplaceRootPath(memoryVO.getReplaceRootPath());
			//Thumnail 경로
			lbvo.setThumbnailCnt(memoryVO.getThumbnailCnt());
			lbvo.setThumbnailFormat(memoryVO.getThumbnailFormat());
			lbvo.setThumbnailTime(memoryVO.getThumbnailTime());
			
			//녹화파일(TS파일) 개수 세팅
			lbvo.setReStartNum(getRestartNum(lbvo, memoryVO.getIsAdaptive()));
		}
		
		return lbvo;
	}
	
	// 라이브 재시작시 녹화 파일 정보가져오기
	public static int getRestartNum(LiveBroadcastVO lbvo, boolean isAdaptive) throws Exception {
		
		int count = 0;
		File liveTempDir = null;
		
		if(isAdaptive) {
			//ts 파일 경로
			liveTempDir = new File(MultiSlashChk.path(lbvo.getHlsFilePath() + StreamType.high.ordinal()));
		} else {
			liveTempDir = new File(lbvo.getHlsFilePath());
		}
		
		File[] liveFileList = liveTempDir.listFiles();
		
		if(liveFileList != null) {
			count = liveFileList.length;
			if(count >= 2) {
				//m3u8 제외 후 마지막 손상된 ts 파일 Num 덮어쓰기
				count = count - 2;
			} else {
				count = 0;
			}
		}
		
		return count;
	}
	
	private static String getHlsFilePath(LiveBroadcastVO lbvo, String liveFileLocalPath) throws Exception {
		
		String hlsFilePath = MultiSlashChk.path(liveFileLocalPath + "encoding/" + lbvo.getLbSeq() + "/");
		
		if(lbvo.getLcUrl().contains("rtsp://") || lbvo.getLcUrl().contains("rtmp://")) {
			
			try {
				File m3u8FileDir = new File(hlsFilePath);
				
				if(!m3u8FileDir.isDirectory()) {
					m3u8FileDir.mkdirs();
				}
			
			} catch(Exception e) {
				logger.error("getHlsFilePath error : \n{}", e);
			}
		}
		return hlsFilePath;
	}
}
