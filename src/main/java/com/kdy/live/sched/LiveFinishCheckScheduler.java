package com.kdy.live.sched;

import com.kdy.live.dto.LiveSchedMemoryVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kdy.live.service.live.LiveFinishCheckService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LiveFinishCheckScheduler {
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	private final LiveFinishCheckService liveFinishCheckService;

	private final LiveSchedMemoryVO memoryVO;
	
	/**
	 * 방송 종료 데이터 처리 스케쥴러 (즉시 종료/ 녹화 처리)
	 *
	 */
	
	@Scheduled(fixedDelay=3000, initialDelay=10000)
	public void execute() throws Exception {
		Thread.currentThread().setName(getClass().getSimpleName());
		// live finish check service call
		if(memoryVO.getSystemOnOff()) {
			logger.debug("System [ON] >> execute()");
			liveFinishCheckService.service();
			return;
		}
		logger.error("System [OFF]");

	}
}
