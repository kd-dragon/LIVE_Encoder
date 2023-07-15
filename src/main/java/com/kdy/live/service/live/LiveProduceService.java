package com.kdy.live.service.live;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import com.kdy.app.bean.util.LiveFileUtils;
import com.kdy.live.bean.status.LiveBroadcastSelectBean;
import com.kdy.live.bean.status.LiveBroadcastUpdateBean;
import com.kdy.live.dto.LiveSchedMemoryVO;
import com.kdy.live.dto.LiveSchedMemoryVO.RedisHashKeyword;
import com.kdy.live.dto.live.LiveBroadcastEvent;
import com.kdy.live.dto.live.LiveBroadcastVO;

@Component
@RequiredArgsConstructor
public class LiveProduceService {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private final ApplicationEventPublisher applicationEventPublisher;
	private final LiveBroadcastSelectBean selectBean;
	private final LiveBroadcastUpdateBean updateBean;

	@Qualifier("redisTemplateObject")
	private final RedisTemplate<String, Object> redisTemplate;
	
	/**
	 * # Live 방송 실행 서비스 
	 * 1. 대기중인 방송 조회 (예약이 아닌 즉시 시작인 경우에도 대기중으로 간주)
	 * 2. 라이브 방송 시작전 초기화 (인코딩, 녹화VOD 경로 등)
	 * 3. LiveSeqToVO - 싱글톤 객체 해시맵에 LB_SEQ 를 Key로 하여 객체 저장
	 * 4. 라이브 실행 Queue 에 객체 push. -> 이후 LiveEncodeService 에서 인코딩처리
	 * @throws Exception
	 */
	public void service(LiveSchedMemoryVO memoryVO) throws Exception {
		
		LiveBroadcastVO lbvo = selectBean.onWaitingChannel();

		if(lbvo == null) {
			logger.debug("Live on waiting is not exists");
			return;
		}
		if(memoryVO.getLiveSeqToVO().get(lbvo.getLbSeq()) != null) {
			logger.warn("###### Already binded Live Channel [" + lbvo.getLbTitle() + "] #####");
			return;
		}

		// 방송상태 변경 (status: 8)
		if(lbvo.getLbStatus() != null && lbvo.getLbStatus().equals("0")) {
			updateBean.statusStart(lbvo);
		}
		// live VO 초기화 (인코딩 경로 정보, 썸네일 등)
		LiveFileUtils.initializeLiveInfo(lbvo, memoryVO);

		// Set Serial Number
		updateBean.updateLiveSerialNo(lbvo);

		// vo to in-memory hashmap
		memoryVO.getLiveSeqToVO().put(lbvo.getLbSeq(), lbvo);

		// Live 인코딩 요청
		LiveBroadcastEvent liveEvent = new LiveBroadcastEvent(applicationEventPublisher, lbvo);
		applicationEventPublisher.publishEvent(liveEvent);

		// Redis 방송상태 (시작/종료 구분값 SET)
		ValueOperations<String, Object> valueOperation = redisTemplate.opsForValue();
		String key = RedisHashKeyword.LIVESTATUS.toString() + lbvo.getLbSeq();
		valueOperation.set(key, "1");
	}
}
