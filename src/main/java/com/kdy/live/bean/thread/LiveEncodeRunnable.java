package com.kdy.live.bean.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kdy.live.dto.LiveSchedMemoryVO;
import com.kdy.live.dto.live.LiveBroadcastVO;

public class LiveEncodeRunnable extends Thread {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private final LiveBroadcastVO lbvo;
	private final LiveEncodeWorker worker;
	
	public LiveEncodeRunnable(LiveBroadcastVO lbvo, LiveSchedMemoryVO memoryVO) {
		this.lbvo = lbvo;
		worker = memoryVO.getApplicationContext().getBean(LiveEncodeWorker.class);
	}
	
	@Override
	public void run() {
		try {
			worker.service(lbvo);
		} catch(Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

}
