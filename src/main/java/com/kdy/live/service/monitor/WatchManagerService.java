package com.kdy.live.service.monitor;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kdy.live.bean.montior.WatchFileHandler;
import com.kdy.live.bean.util.code.LiveBroadcastStatus;
import com.kdy.live.dto.LiveSchedMemoryVO;
import com.kdy.live.dto.live.LiveBroadcastVO;

@Component
public class WatchManagerService {
	private Logger logger = LoggerFactory.getLogger("ffmpeg");
	
	private final WatchFileHandler watchFileHandler;
	
	@Autowired
	public WatchManagerService(WatchFileHandler watchFileHandler) {
		this.watchFileHandler = watchFileHandler;
	}
	
	/**
	 * Watch Service 비정상 종료시 재시작 처리
	 * @param memoryVO
	 */
	public void service(LiveSchedMemoryVO memoryVO) {
		
		Iterator<String> keys = memoryVO.getLiveSeqToVO().keySet().iterator();
		
		while(keys.hasNext()) {
			String broadcastKey = keys.next();
			LiveBroadcastVO lbvo = memoryVO.getLiveSeqToVO().get(broadcastKey);
			if(lbvo == null) { continue; }
				
			// 라이브 중 Watch Monitoring 시작 (Status: 1 또는 4)
			if(lbvo.getLbStatus().equals(LiveBroadcastStatus.OnAir.getTitle())
					|| lbvo.getLbStatus().equals(LiveBroadcastStatus.Restart.getTitle())) {

				// WatchMontior가 Null이면 WatchFileHandler 호출, 리스너 생성
				if(lbvo.getWatchMonitor() != null) { continue; }
				if(memoryVO.getIsAdaptive()) {
					// 라이브 TS, M3U8 파일 모니터링 (적응형 스트리밍 O)
					watchFileHandler.adaptiveMonitoring(lbvo);
					continue;
				}
				// 라이브 TS, M3U8 파일 모니터링 (적응형 스트리밍 X)
				watchFileHandler.monitoring(lbvo);
				continue;
			}
			// 라이브 종료 이후 Watch Monitoring 종료
			if(lbvo.getWatchMonitor() != null) {
				try {
					lbvo.getWatchMonitor().stop();
				} catch (Exception e) {
					e.printStackTrace();
					logger.info(e.getMessage());
				} finally {
					lbvo.setWatchMonitor(null);
					logger.info("Finish Monitoring >> " + lbvo.getLbTitle());
				}
			}
		}
	}
}
