package com.kdy.live;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAspectJAutoProxy
@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = "com.kdy")
public class LiveEncoderMainApplication {
	
	/**
	 * [스케쥴러 작업 방식]
	 * @since 2.0
	 * 
	 * 
	 * - 패키지경로: com.kdy.live.sched
	 * - 설정파일경로: com.kdy.live.sched.conf
	 * 
	 * > 핵심 스케줄러 순서 및 역할
	 * 1. 대기중인 라이브 실행 (LiveProduceScheduler)
	 * 2. 라이브 인코딩 파일 감지 및 전달 처리 (WatchManageScheduler)
	 * 3. 실행중인 라이브 상태별 처리 (LiveStatusCheckScheduler)
	 * 4. 정상 종료된 라이브 처리 (LiveFinishCheckScheduler)
	 * 
	 * --
	 *
	 * > 장애처리
	 * 비정상 종료된 라이브 재시작 처리 (LiveAliveCheckScheduler)
	 * (이중화) 타 모듈 비정상 종료된 라이브 처리 (LiveInterruptedOthersScheduler)
	 * 
	 * --
	 * 
	 * > 모니터링
	 * FFMPEG TCP 통신 모니터링 Netty 스케줄러 (NettyFFmpegServerScheduler)
	 * 스트리밍 서버 CPU|Memory|접속자수 모니터링 (LiveMonitorScheduler)
	 * 
	 * @param args
	 */
	
	public static void main(String[] args) {
		SpringApplication.run(LiveEncoderMainApplication.class, args);
	}

}
