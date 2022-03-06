package com.kdy.live.sched;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kdy.live.dto.LiveSchedMemoryVO;
import com.kdy.live.service.live.LiveFinishCheckService;

@Component
public class LiveFinishCheckScheduler {
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	private final LiveFinishCheckService liveFinishCheckService;
	
	@Autowired
	public LiveFinishCheckScheduler(LiveFinishCheckService liveFinishCheckService, LiveSchedMemoryVO memoryVO) {
		this.liveFinishCheckService = liveFinishCheckService;
	}
	
	/**
	 * 방송 종료 데이터 처리 스케쥴러 (즉시 종료/ 녹화 처리)
	 * 
	 * 2021.12.01~
	 * 녹화는 녹화 모듈에서 별도로 처리합니다.
	 * 
	 * @author KDY
	 * @throws Exception
	 */
	
	@Scheduled(fixedDelay=3000, initialDelay=10000)
	public void execute() throws Exception {
		Thread.currentThread().setName("TG_"+getClass().getSimpleName());
		// live finish check service call
		logger.debug(">> execute()");
		liveFinishCheckService.service();
	}
}
