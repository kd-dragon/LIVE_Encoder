package com.kdy.live.bean.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kdy.live.bean.encoding.EncodeManagerBean;
import com.kdy.live.dto.LiveSchedMemoryVO;
import com.kdy.live.dto.live.LiveBroadcastVO;

@Component
public class LiveEncodeWorker {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private final EncodeManagerBean encodeManageBean;
	private final LiveSchedMemoryVO memoryVO;

	
	@Autowired
	public LiveEncodeWorker (EncodeManagerBean encodeManageBean, LiveSchedMemoryVO memoryVO) {
		this.encodeManageBean = encodeManageBean;
		this.memoryVO = memoryVO;
		
	}
	
	/**
	 * 라이브 > hls 변환 작업
	 * @param lbvo
	 * @throws Exception
	 */
	public void service(LiveBroadcastVO lbvo) throws Exception {
		
		if(lbvo != null) {
			//커맨드 라인 생성 및 ffmpeg 실행
			Boolean retval = encodeManageBean.executeLiveEncodeTCP(lbvo); 
			lbvo.setFfmpegRetval(retval);
			memoryVO.adjustCurrentLiveBatchCount(false);
			
		} else {
			logger.error("######## LiveBroadcastVO IS NULL ########");
		}
	}
	

}
