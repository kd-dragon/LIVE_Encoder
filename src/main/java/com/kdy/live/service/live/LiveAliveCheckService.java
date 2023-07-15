package com.kdy.live.service.live;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.kdy.app.bean.util.LiveFileUtils;
import com.kdy.live.bean.status.LiveBroadcastSelectBean;
import com.kdy.live.bean.status.LiveBroadcastUpdateBean;
import com.kdy.live.bean.util.MacAddressUtil;
import com.kdy.live.bean.util.code.LiveBroadcastStatus;
import com.kdy.live.bean.util.system.ProcessManageFactory;
import com.kdy.live.dto.LiveSchedMemoryVO;
import com.kdy.live.dto.live.LiveBroadcastEvent;
import com.kdy.live.dto.live.LiveBroadcastVO;

@Component
@RequiredArgsConstructor
public class LiveAliveCheckService {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private final LiveBroadcastSelectBean 	selectBean;
	private final LiveSchedMemoryVO 		memoryVO;
	private final LiveBroadcastUpdateBean 	updateBean;
	private final ApplicationEventPublisher applicationEventPublisher;
	private final ProcessManageFactory processManageFactory;

	public void service() throws Exception {
		
		/**
		 * Live모듈 강제종료 또는 서버의 문제로 재기동시 기존에 진행중이었던 라이브를 체크하여 자동 재시작합니다.
		 * 또는 타 모듈에서 장애발생한 방송을 LiveInterruptedOthersService에서 가져온 뒤에 내 방송으로 초기화하면
		 * 이 서비스에서 해당 라이브를 자동 재시작합니다.
		 * 
		 * - MacAddress는 Live모듈이 다중화설치되었을 때 구분용으로 사용합니다.
		 * - MacAddress를 못가져올 때 application.yml -> live.serialNo 관리자가 직접 구분한 값을 가져옵니다.
		 */
		
		List<LiveBroadcastVO> aliveList = selectBean.selectByInterruptedBroadcast
				(MacAddressUtil.getMacAddress() == null? memoryVO.getSerialNo() : MacAddressUtil.getMacAddress());

		if(aliveList == null || aliveList.size() == 0) {
			logger.debug(">> execute() - aliveList is null or size is 0");
			return;
		}
		logger.debug(">> execute() - aliveList Size : {}", aliveList.size());
		for (LiveBroadcastVO lbvo : aliveList) {//방송 잔여 시간이 1분 초과인 경우
			if (lbvo.getEndYn().equalsIgnoreCase("N")) {
				lbvo.setFfmpegRetval(null);
				lbvo.setLbStatus(LiveBroadcastStatus.Restart.getTitle());
				lbvo.setLiveDurationDate(lbvo.getLbjDuration());

				//라이브 시작전 VO
				LiveFileUtils.initializeLiveInfo(lbvo, memoryVO);

				// Live 객체 공유 해시맵에 put.
				memoryVO.getLiveSeqToVO().put(lbvo.getLbSeq(), lbvo);

				// Live 인코딩 요청
				LiveBroadcastEvent liveEvent = new LiveBroadcastEvent(applicationEventPublisher, lbvo);
				applicationEventPublisher.publishEvent(liveEvent);
				continue;
				//방송 잔여 시간이 1분 미만인 경우
			}
			//라이브 정상종료 -> 종료시간 업데이트
			updateBean.updateLiveEndDate(lbvo.getLbSeq());
		}
	}
}
