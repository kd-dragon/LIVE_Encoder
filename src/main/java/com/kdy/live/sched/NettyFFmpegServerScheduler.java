package com.kdy.live.sched;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kdy.live.bean.netty.NettyServerRunnable;
import com.kdy.live.bean.netty.ffmpeg.NettyFfmpegInitializer;
import com.kdy.live.dto.netty.NettyVO;

@Component
public class NettyFFmpegServerScheduler {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private NettyVO vo;
	
	@Autowired
	private NettyFfmpegInitializer initializer;
	
	/*
	 *  라이브 인코딩 정보 수집용 스케쥴러/ FFMPEG 프로세스와 TCP 통신
	 */
	@Scheduled(fixedDelay = 10000, initialDelay = 1000)
	public void service() throws Exception {
		Thread.currentThread().setName("TG_"+getClass().getSimpleName());
		logger.debug(">> execute()");
		
		if(vo.getFfmpegUseYn().equalsIgnoreCase("Y")) {
			if (vo.getThread("ffmpeg") == null) {
				NettyServerRunnable runnable = new NettyServerRunnable(vo, initializer, vo.getFfmpegPort(), "ffmpeg");
				runnable.start();
				vo.setThread("ffmpeg", runnable);
			}
		}
	}
}
