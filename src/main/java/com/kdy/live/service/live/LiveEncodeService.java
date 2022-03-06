package com.kdy.live.service.live;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.kdy.live.bean.status.LiveBroadcastUpdateBean;
import com.kdy.live.bean.thread.LiveEncodeRunnable;
import com.kdy.live.dto.LiveSchedMemoryVO;
import com.kdy.live.dto.live.LiveBroadcastEvent;
import com.kdy.live.dto.live.LiveBroadcastVO;

@Component
public class LiveEncodeService {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private final LiveSchedMemoryVO memoryVO;
	
	private final LiveBroadcastUpdateBean updateBean;
	
	@Autowired
	public LiveEncodeService(	LiveSchedMemoryVO 				memoryVO
							  , LiveBroadcastUpdateBean 		updateBean) 
	{
		this.memoryVO 	= memoryVO;
		this.updateBean	= updateBean;
	}
	
	@EventListener
	public void service(LiveBroadcastEvent event) throws Exception {
		
		logger.error("thread id -> {}, class name -> {}", Thread.currentThread().getId(), getClass().getSimpleName());
		int threadCurCnt = memoryVO.getCurrentLiveBatchCount();
		
		// 현재 스레드 갯수 < Maximum 스레드 갯수
		if(threadCurCnt < memoryVO.getMaxWorkerThreadCnt()) {
			
			// Live Event Object
			LiveBroadcastVO lbvo = event.getLbvo();
			
			if(lbvo != null) {
				logger.info("$$$$$$$$ lbvo : {}", lbvo.toString());
				
				// VOD 처리
				if(lbvo.getLcUrl().equals("VOD")){ //라이브 VOD
					long currentTime = System.currentTimeMillis();
					logger.info("VOD Update Status [{}], StartDate [{}]", "OnAir", currentTime);
					
					// 라이브 상태 변경 ( 진행중 : 1)
					lbvo.setLbjProcessId("VOD");
					lbvo.setVodDuration(lbvo.getLbjDuration());
					
					try {
						updateBean.statusOnAir(lbvo);
						updateBean.updateLiveBroadcastJob(lbvo);
					} catch (Exception e) {
						logger.error("VOD Live - error : \n{}", e);
					}
					//VOD 시작 시간 insert
					lbvo.setVodStartDate(currentTime);
					
				} else {
					// 라이브 스레드 실행
					logger.info("Current Live Worker Count >>>>> " + memoryVO.getCurrentLiveBatchCount() + "");
					
					LiveEncodeRunnable liveEncodeRunnable = new LiveEncodeRunnable(lbvo, memoryVO);
					memoryVO.adjustCurrentLiveBatchCount(true);
					
					liveEncodeRunnable.start();
				}
			} else {
				logger.debug("Waiting Live Object doesn't Exist");
			}
		} else {
			logger.info("LiveEncodeService >> EncodingThread Exceed [current:" + threadCurCnt + "|max:" + memoryVO.getMaxWorkerThreadCnt() +"]");
		}
	}
}
