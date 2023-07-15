package com.kdy.live.service.live;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kdy.app.bean.LiveChatSaveBean;
import com.kdy.live.bean.status.LiveBroadcastFinishBean;
import com.kdy.live.bean.status.LiveBroadcastSelectBean;
import com.kdy.live.bean.util.MacAddressUtil;
import com.kdy.live.dto.LiveSchedMemoryVO;
import com.kdy.live.dto.live.LiveBroadcastVO;

@Component
@RequiredArgsConstructor
public class LiveFinishCheckService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private final LiveSchedMemoryVO memoryVO;
	private final LiveBroadcastSelectBean selectBean;
	private final LiveBroadcastFinishBean finishBean;
	private final LiveChatSaveBean chatSaveBean;

	
	/*
	 * 라이브 종료 후 처리 
	 * 1. 날짜 만기된 데이터 처리 (Status On-Air 또는 Pause -> Finished)
	 * 2. Finish Queue 처리 (FFMPEG 종료 이후의 In-Memory 관리)
	 * 3. 채팅 저장 처리 (Redis 채팅 목록 -> Object (ChatDTO) -> DB Insert)
	 */
	public void service() throws Exception {
		
		String serialNo = MacAddressUtil.getMacAddress() == null ? memoryVO.getSerialNo() : MacAddressUtil.getMacAddress();
		
		List<LiveBroadcastVO> expiredList = selectBean.expiredChannel(serialNo);
		// 만료된 라이브 종료 처리
		if(expiredList == null || expiredList.size() == 0) {
			return;
		}
		for(LiveBroadcastVO lbvo : expiredList) {
			lbvo.setLbSerialNo(serialNo);
			finishBean.processFinish(lbvo);

			// 채팅 저장 처리
			if(lbvo.getLbChatYn() == null || lbvo.getLbChatYn().equalsIgnoreCase("Y")) {
				continue;
			}
			chatSaveBean.checkChattingInRedis(lbvo.getLbSeq(), "SCHED");
		}
	}
	
	
	
}
