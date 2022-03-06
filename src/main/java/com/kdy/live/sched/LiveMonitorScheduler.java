package com.kdy.live.sched;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kdy.live.service.monitor.LiveMonitorService;
import com.kdy.live.service.system.SystemConfigUpdateService;

@Component
public class LiveMonitorScheduler {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Value("${live.monitor.useYn}")
	private String monitorUseYn;
	
	private final LiveMonitorService liveMonitorService;
	
	private final SystemConfigUpdateService systemConfigUpdateService;
	
	@Autowired
	public LiveMonitorScheduler(LiveMonitorService liveMonitorService,
								SystemConfigUpdateService systemConfigUpdateService) {
		
		this.liveMonitorService 		= liveMonitorService;
		this.systemConfigUpdateService  = systemConfigUpdateService;
	}
	
	
	/**
	 * 스트리밍 모니터링 (CPU, Memory 등) 스케쥴러
	 * 시스템 설정 업데이트
	 * 
	 * @author KDY
	 * @throws Exception
	 */
//	@Scheduled(cron = "0 0/10 * * * ?")
	@Scheduled(fixedDelay = 60000, initialDelay = 1000)
	public void execute() throws Exception {
		
		if(monitorUseYn.equals("Y")) {
			liveMonitorService.liveStreamingMonitor(); //스트리밍 모듈별 상태
			liveMonitorService.liveViewCountMonitor(); //방송별 접속자 수 
		} else {
			logger.warn("Live Monitor Sched [OFF]");
		}
		
		//필수
		systemConfigUpdateService.updateSystemConfig(); // 시스템 설정 업데이트
		systemConfigUpdateService.printSystemStatus(); // 시스템 상태 출력
		
	}
	
}
