package com.kdy.live.dto;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.kdy.live.dto.live.LiveBroadcastVO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;

@ToString
@Getter
@Setter 
@Component
public class LiveSchedMemoryVO {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Value("${live.osType}")
	private String osType;
	
	@Value("${live.serialNo}")
	private String serialNo;
	
	@Value("${live.encoderPath}")
	private String encoderPath;
	
	@Value("${live.hls.deleteYn}")
	private String hlsDeleteYn;
	
	@Value("${spring.redis.useYn}")
	private String redisUseYn;
	
	@Value("${logging.file.path.ffmpeg}")
	private String ffmpegLogPath;
	
	//encoding 타입( basic(high-1080p, mid-720p), advance(high-1080p, mid-720p, low-360p))
	@Value("${encoding.type}")
	private String encodingType;
	
	//적응형 인코딩 사용 여부
	@Value("${encoding.isAdaptive}")
	private Boolean isAdaptive;
	
	//인코딩 코덱 설정 사용 여부
	@Value("${encoding.codec.enabled}")
	private Boolean codecEnabled;
	
	//recorder 모듈 사용 여부
	@Value("${live.recordable}")
	private boolean recordable;
	
	//vod 녹화 시 채팅 녹화 여부
	@Value("${live.chat.recYn}")
	private String chatRecYn;
	
	//녹화파일 경로 이동 여부
	@Value("${live.record-file-local-to-nas}")
	private String recordMoveYn;
	
	//멀티 모듈 사용 여부 (이중화, 다중화시 사용)
	@Value("${live.multiplex.enabled}")
	private Boolean multiplexEnabled;
	
	// system dynamic configuration 
	private String liveFileLocalPath;
	private String encodingVodFilePath;
	private String etcFileUploadPath;
	private String replaceRootPath;
	private String liveStreamingUri;
	private String vodStreamingUri;
	private Integer maxWorkerThreadCnt=1;
	private int thumbnailCnt; //썸네일 갯수
	private String thumbnailTime; //썸네일 간격
	private String thumbnailFormat; //썸네일 포맷
	private String vodOriginalFilePath; //VOD 직접등록시 원본파일 저장 경로
	private String vodTempFilePath; //VOD uploader 영상 업로드 경로 > local 경로 (original path(NAS)로 복사)
	private String defaultThumbnail; //Live 기본 이미지
	private String liveFileNasPath;
	
	// system dynamic configuration
	
	@Autowired
	private ApplicationContext applicationContext;
	
	//시스템 ON/OFF (true: On / false: Off)
	private Boolean systemOnOff = true; 
	
	//live hls 인코딩 쓰레드 개수 (초기값 0)
	private Integer currentLiveBatchCount=0;
	
	//live 쓰레드별 LiveBroadcastVO
	private ConcurrentHashMap<String, LiveBroadcastVO> liveSeqToVO = new ConcurrentHashMap<String, LiveBroadcastVO>();
	
	private FFprobe ffprobe = null;
	
	private FFmpeg ffmpeg = null;
	
	private String ffmpegPath;
	
	private String ffprobePath;
	
	public FFprobe getFfprobe() {
		String ffprobePath = encoderPath + "ffprobe";
		if(osType.equalsIgnoreCase("window")) {
			ffprobePath += ".exe";
		}
		try {
			logger.info("OS Type : [" + osType + "], FFprobe Path : " + ffprobePath);
			this.ffprobe = new FFprobe(ffprobePath);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		
		return this.ffprobe;
	}
	
	public FFmpeg getFfmpeg() {
		String ffmpegPath = encoderPath + "ffmpeg";
		if(osType.equalsIgnoreCase("window")) {
			ffmpegPath += ".exe";
		}
		try {
			logger.info("OS Type : [" + osType + "], FFmpeg Path : " + ffmpegPath);
			this.ffmpeg = new FFmpeg(ffmpegPath);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		
		return this.ffmpeg;
	}
	
	public String getFfprobePath() {
		String ffprobePath = encoderPath + "ffprobe";
		if(osType.equalsIgnoreCase("window")) {
			ffprobePath += ".exe";
		}
		
		this.ffprobePath = ffprobePath;
		
		return this.ffprobePath;
	}
	
	public String getFfmpegPath() {
		String ffmpegPath = encoderPath + "ffmpeg";
		if(osType.equalsIgnoreCase("window")) {
			ffmpegPath += ".exe";
		}
		
		this.ffmpegPath = ffmpegPath;
		
		return this.ffmpegPath;
	}
	
	// Thread-safe batch count 조정 (true: 증가, false: 감소)
	public synchronized void adjustCurrentLiveBatchCount(boolean isIncrease) {
		if(isIncrease) {
			this.currentLiveBatchCount += 1;
		} else {
			this.currentLiveBatchCount -= 1;
		}
	}
	
	public enum RedisHashKeyword {
		BACKUP, RECORD, RECENABLE, LIVESTATUS, RECSTARTDATE, RECDURATION
	}
}
	
