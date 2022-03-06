package com.kdy.live.dto.live;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.springframework.data.redis.listener.ChannelTopic;

import lombok.Data;

@Data
public class LiveBroadcastVO {
	
	//tglive_broadcast
	 private String lbSeq;				//라이브 방송 시퀀스
	 private String lbTitle;			//라이브 방송 제목
	 private String lbDesc;				//라이브 방송 설명
	 private String lbRegUserId;		//라이브 방송 등록자 ID
	 private String lbRegDate;			//라이브 방송 등록일자
	 private String lbStatus;			//라이브 방송 상태(0: 대기, 1: 방송중, 2: 완료, 3:일시정지, 4:재시작, 5: 녹화, 9:오류)
	 private String lbPresetCd;         //라이브 방송 프리셋 CD
	 private String lbVodSaveYn;        //라이브 VOD 저장 여부(Y/N)
	 private String lbCategorySeq;      //카테고리 시퀀스
	 private String lbVodSeq;			//라이브 VOD 시퀀스
	 private String lbSerialNo;			//스케줄러 시리얼 번호
	 private String lbChatYn;

	//tglive_channel
	 private String lcSeq;              //라이브 채널 시퀀스
	 private String lcUrl;              //라이브채널URL

	//tglive_broadcast_job
	 private String lbjSeq;				//라이브 Job Seq
	 private String lbjProcessId;       //라이브 프로세스 ID
	 private double lbjDuration=0;		//라이브 방송 시간
	 private String lbjLogMsg="SUCCESS";//라이브 로그 메시지
	 private String lbjLogDesc;			//라이브 로그 상세
	
	 
	private String presetData; //프리셋 정보 (가로:세로)
	// paging
	private Integer rowNum;
	// vod save path
	private String vodTempSavePath;
	//WebRootPath
	private String replaceRootPath;
	// hls path
	private String hlsFilePath;
	private String vodSavePath; //recording_file_path
	private String recordCopyPath; //녹화영상 복사할 경로 (Streaming 하기위해 복사)
	private String recordCopyName;
	// vod save file number
	// private Integer vodFileNum=0;
	// ffmpeg result val
	private Boolean ffmpegRetval;
	
	// vod recording percent
	private List<Double> recordDurations = new LinkedList<>();
	private Double totalDuration;
	private String recordPercent;
	private String estimatedEndTime;
	private Boolean isTotalProcess=false;
	private long vodSize; //영상 크기
	private String vodWidth;
	private String vodHeight;
	//thumbnail
	private int thumbnailCnt;
	private String thumbnailFormat;
	private String thumbnailTime;
	
	private int reStartNum=0; //라이브 재시작시 생성될 ts파일 num
	private double currentDuration=0; //현재 라이브 시간
	
	private FileAlterationMonitor watchMonitor = null; //와치 모니터객체
	
	//Duration 구하기
	private long vodStartDate=0; //VOD 시작시간
	private double vodDuration=0; //VOD duration
	private double liveDurationDate=0; //Live duration 구할때 사용하는 temp변수
	private long stopDate=0; //일시정지 요청 시작시간
	private double stopDuration=0; //총 일시정지 시간
	
	private int contentsSeq; //MCMS LIVE : vod저장시 필요
	
	
	private String vodSeq;
	
	//redis pub-sub channel topic
	private ChannelTopic topic;
	
	//무중단 서비스 관련
	private String lbjDurationUpdateDate;
	private String endYn;
	
	private int lbmAccessorCnt;
	
}
