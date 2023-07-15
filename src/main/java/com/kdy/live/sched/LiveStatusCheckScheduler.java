package com.kdy.live.sched;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kdy.live.dto.LiveSchedMemoryVO;
import com.kdy.live.service.live.LiveStatusCheckService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LiveStatusCheckScheduler {
	private Logger logger = LoggerFactory.getLogger(getClass());

	private final LiveStatusCheckService liveStatusCheckService;
	private final LiveSchedMemoryVO memoryVO;
	
	/**
	 * Live 상태별 처리 스케쥴러
	 */
	@Scheduled(fixedDelay=5000, initialDelay=5000)
	public void execute() throws Exception {
		Thread.currentThread().setName(getClass().getSimpleName());
		
		if(memoryVO.getSystemOnOff()) {
			logger.debug("System [ON] >> execute()");
			// live status check service call
			liveStatusCheckService.service();
			return;
		} 
		logger.warn("System [OFF]");
	}
}
