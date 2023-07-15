package com.kdy.live.sched;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kdy.live.dto.LiveSchedMemoryVO;
import com.kdy.live.service.live.LiveInterruptedOthersService;

import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@Component
@RequiredArgsConstructor
public class LiveInterruptedOthersScheduler {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private final LiveInterruptedOthersService liveInterruptedOthersService;
	private final LiveSchedMemoryVO memoryVO;
	
	/**
	 * 
	 * 이중화/다중화 구성시 타 모듈의 비정상 종료 라이브 검사하여 재시작하는 스케줄러입니다.
	 * 
	 * 나의 MacAddress로 생성되지 않은 방송 중에 20초간 Duration이 업데이트되지 않는 방송을 조회합니다.
	 * 조회된 방송은 MacAddress, Duration, ffmpeg Pid 등이 초기화 업데이트 됩니다.
	 * 
	 *  
	 * @throws Exception
	 */
	
	@Scheduled(fixedDelay = 5000, initialDelay = 1000)
	@SchedulerLock(name="InterruptedCheck", lockAtLeastFor="1s", lockAtMostFor="4s")
	public void execute() throws Exception {
		Thread.currentThread().setName(getClass().getSimpleName());
		if(memoryVO.getSystemOnOff()) {
			logger.debug("System [ON] >> execute()");
			
			//LiveInterruptedService 호출
			liveInterruptedOthersService.service();
			return;
		}
		logger.error("System [OFF]");
	}
}
