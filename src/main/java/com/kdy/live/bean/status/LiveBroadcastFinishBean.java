package com.kdy.live.bean.status;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdy.app.bean.util.FileCopyUploadBean;
import com.kdy.live.bean.util.code.LiveBroadcastStatus;
import com.kdy.live.bean.util.system.ProcessManageFactory;
import com.kdy.live.dto.LiveSchedMemoryVO;
import com.kdy.live.dto.LiveSchedMemoryVO.RedisHashKeyword;
import com.kdy.live.dto.live.LiveBroadcastVO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LiveBroadcastFinishBean {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private final LiveSchedMemoryVO memoryVO;
	private final LiveBroadcastUpdateBean updateBean;
	private final ProcessManageFactory processManageFactory;
	private final ObjectMapper objectMapper;
	private final FileCopyUploadBean fileCopyUploadBean;
	
	@Qualifier("redisTemplateObject")
	private RedisTemplate<String, Object> redisTemplate;
	
	/*
	 *  [Finished 상태 처리]
	 *  기간 만료된 라이브 자동 종료 처리
	 *  1. FFMPEG Pid Kill
	 *  2. Live Status DB Update
	 *  3. Finished Queue에 Object offer
	 */
	public void processFinish(LiveBroadcastVO lbvo) throws Exception {
		String pid = lbvo.getLbjProcessId();
		if(processManageFactory.template().checkPID(pid)) {
			processManageFactory.template().killProcess(pid);
		} else {
			logger.error("Expired LiveBroadcast FFMPEG Kill 처리 중 해당하는 PID[{}]가 존재하지 않습니다", pid);
		}
		
		lbvo.setLbjProcessId("0");
		
		//라이브 총 방송 시간 세팅
		lbvo.setLbjDuration(lbvo.getLiveDurationDate() + lbvo.getCurrentDuration());
		lbvo.setCurrentDuration(0); //초기화
		
		//라이브 녹화
		if(lbvo.getLbVodSaveYn().equalsIgnoreCase("Y")) {
			
			//Local TS 파일 NAS로 복사
			if(memoryVO.getRecordMoveYn().equalsIgnoreCase("Y")) {
				LocalFileToNasServer(lbvo);
			}
			
			lbvo.setLbStatus(LiveBroadcastStatus.Recording.getTitle());
			updateBean.statusRecording(lbvo); //status
			updateBean.updateLiveBroadcastJob(lbvo); //processId
		} else {
			lbvo.setLbStatus(LiveBroadcastStatus.Finished.getTitle());
			updateBean.statusFinished(lbvo); //status
			updateBean.updateLiveBroadcastJob(lbvo); //processId
			updateBean.jobLogMove(lbvo); //종료된 방송 JOB 데이터 JOB_LOG로 이동
			updateBean.jobDataDelete(lbvo); //JOB 데이터 지우기
		}
		
		memoryVO.getLiveSeqToVO().remove(lbvo.getLbSeq());
		
		//Redis 방송 상태 (시작 / 종료 구분 값 SET)
		ValueOperations<String, Object> valueOperation = redisTemplate.opsForValue();
		String key = RedisHashKeyword.LIVESTATUS.toString() + lbvo.getLbSeq();
		valueOperation.set(key, "2", 5, TimeUnit.MINUTES);
	}
	
	//Local TS 파일 NAS로 복사
	private void LocalFileToNasServer(LiveBroadcastVO lbvo) throws Exception {
		
		String recordKey = RedisHashKeyword.RECORD.toString() + lbvo.getLbSeq() + lbvo.getLbSerialNo();
		List<String> recordList = objectMapper.convertValue(redisTemplate.opsForList().range(recordKey, 0, -1), new TypeReference<List<String>>() {});

		//녹화 영상 리스트가 존재할 경우 영상 옮기기
		if(recordList != null) {
			
			//로켤 경로
			String localPath = memoryVO.getLiveFileLocalPath() + "encoding/" + lbvo.getLbSeq() + "/";
			
			//적응형인 경우 '0' (high) 영상만 인코딩
			if(memoryVO.getIsAdaptive()) {
				localPath = localPath + "0/";
			}
			
			//NAS 경로
			String nasPath = memoryVO.getLiveFileNasPath() + "encoding/" + lbvo.getLbSeq() + "/";
			for(int i = 0; i < recordList.size(); i++) {
				fileCopyUploadBean.service(localPath, nasPath, recordList.get(i));
			}
			
			//파일 지우기
			if(memoryVO.getHlsDeleteYn().equalsIgnoreCase("Y")) {
				File delFile = new File(memoryVO.getLiveFileLocalPath() + "encoding/" + lbvo.getLbSeq() + "/");
				FileUtils.cleanDirectory(delFile);
				delFile.delete();
			}
		}
	}
}
