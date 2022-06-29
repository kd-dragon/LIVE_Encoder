package com.kdy.live.sched;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kdy.live.dto.LiveSchedMemoryVO;
import com.kdy.live.service.live.LiveAliveCheckService;

import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@Component
@RequiredArgsConstructor
public class LiveAliveCheckScheduler {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private final LiveAliveCheckService liveAliveCheckService;
	
	private final LiveSchedMemoryVO memoryVO;
	
	/**
	 * 실시간 라이브 장애 여부를 체크하여 자동 재시작하는 스케줄러입니다.
	 * 또한 타 모듈에서 장애발생한 방송이 내 모듈로 배정되었을 때 이 서비스에서 해당 라이브를 자동 재시작합니다.
	 * 
	 * 
	 * @throws exception
	 */
	
	@Scheduled(fixedDelay = 5000, initialDelay = 1000)
	@SchedulerLock(name="aliveCheck", lockAtLeastFor="1s", lockAtMostFor="4s")
	public void execute() throws Exception {
		Thread.currentThread().setName("TG_" + getClass().getSimpleName());
		if(memoryVO.getSystemOnOff()) {
			logger.debug("System [ON] >> execute()");
			
			//LiveAliveCheckService 호출
			liveAliveCheckService.service();
		} 
		
		logger.warn("System [OFF]");
	}
}
