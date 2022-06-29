package com.kdy.live.sched;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kdy.live.dto.LiveSchedMemoryVO;
import com.kdy.live.dto.netty.NettyVO;
import com.kdy.live.service.monitor.WatchManagerService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WatchManageScheduler {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private final WatchManagerService watchMainService;
	private final LiveSchedMemoryVO memoryVO;
	
	/**
	 * live hls 파일 감지하여 Redis에 PUT 하는 서비스 
	 * @throws Exception
	 */
	@Scheduled(fixedDelay=5000, initialDelay=5000)
	public void execute() throws Exception {
		Thread.currentThread().setName("TG_"+getClass().getSimpleName());
		logger.debug(">> execute()");
		
		// Redis 사용시에만 동작
		if(memoryVO.getRedisUseYn().equalsIgnoreCase("Y")) {
			if(memoryVO.getSystemOnOff()) {
				// live watch thread check service
				watchMainService.service(memoryVO);
			} 
		}
	}	
}
