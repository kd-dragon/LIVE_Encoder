package com.kdy.live.dto.ffmpeg;

import lombok.Data;

@Data
public class ProgressVO {
	
	// 현재 job 인코딩 진행된 영상 시간 (Nanosec)
	private long curProgressTimeNs=0;
	// 이전 job 인코딩 진행된 영상 시간 (Nanosec)
	private long prevProgressTimeNs=0;
	// job 별 진행된 인코딩 영상 시간의 총 합
	private long sumProgressTimeNsGap=0;
	// job 별 진행된 인코딩 영상 시간 평균 값
	private double averageProgressTimeNsGap=0.0;
	// job 진행 횟수
	private int progressCount=1;
	// 인코딩 완료까지 남은 job 횟수 예측 값
	private long estimatedEndCount=0;
	// 현재 job 시작시점 시스템 시각 (Milisec)
	private long curSystemTimeMs=0;
	// 이전 job 시작시점 시스템 시각 (Milisec)
	private long prevSystemTimeMs=0;
	// job 별 소요된 시스템 시각
	private long systemTimeMsGap=0;
	// 인코딩 완료까지 예상 소요 시간 (sec) 
	private long estimatedEndTime=0;
}
