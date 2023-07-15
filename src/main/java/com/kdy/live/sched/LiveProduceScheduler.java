package com.kdy.live.sched;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kdy.live.dto.LiveSchedMemoryVO;
import com.kdy.live.service.live.LiveProduceService;

import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@Component
@RequiredArgsConstructor
public class LiveProduceScheduler {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private final LiveProduceService liveProduceService;
	private final LiveSchedMemoryVO memoryVO;
	
	/**
	 * 
	 * 대기중엔 라이브 방송을 조회하여 시작하는 스케줄러입니다. (즉시 시작도 해당 스케쥴러에서 조회합니다)
	 * -> 라이브 모듈의 첫번째 프로세스에 해당됩니다.
	 */
	
	@Scheduled(fixedDelay=5000, initialDelay=1000)
	@SchedulerLock(name="produceLive", lockAtLeastFor="1s", lockAtMostFor="4s")
	public void execute() throws Exception {
		Thread.currentThread().setName(getClass().getSimpleName());
		if(memoryVO.getSystemOnOff()) {
			logger.debug("System [ON] >> execute()");
			
			// live check 서비스 호출
			liveProduceService.service(memoryVO);
			return;
		}
		logger.warn("System [OFF]");
	}
}
