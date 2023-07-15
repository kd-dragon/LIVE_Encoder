package com.kdy.live.service.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.gson.Gson;
import com.kdy.app.dto.system.StreamingVO;
import com.kdy.live.bean.montior.LiveMonitorBean;
import com.kdy.live.bean.montior.LiveStreamingMonitorBean;
import com.kdy.live.dto.monitor.LiveMonitorVO;
import com.kdy.live.dto.monitor.LiveViewsDTO;
import com.kdy.live.dto.monitor.LiveViewsVO;

@Component
@RequiredArgsConstructor
public class LiveMonitorService {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private final LiveMonitorBean liveMonitorBean;
	private final LiveStreamingMonitorBean liveStreamingMonitorBean;
	
	@Value("${server.chat.host}")
	private String chatIp;
	
	@Value("${server.chat.port}")
	private String chatPort;
	
	
	//스트리밍 모듈별 상태
	public void liveStreamingMonitor() throws Exception {
		logger.debug("LiveMonitorService start !");
		
		//등록된 Streaming module 정보 가져오기
		List<StreamingVO> streamingList = liveMonitorBean.selectStreamingList();
		List<LiveMonitorVO> insertStreamingList = new ArrayList<LiveMonitorVO>();
		
		if(streamingList != null && streamingList.size() > 0) {
			for(StreamingVO vo : streamingList) {
				LiveMonitorVO lvo =liveStreamingMonitorBean.getSteamingStatus(vo);
				
				if(lvo != null) {
					insertStreamingList.add(lvo);
				}
			}
		}

		if(insertStreamingList.size() > 0) {
			try {
				//streaming status insert
				liveMonitorBean.insertStreamingList(insertStreamingList);

			} catch (Exception e) {
				logger.error("LiveMonitorService . liveStreamingMonitor() insert error :: "+e.getMessage());
			}
		}
		liveMonitorBean.deleteStreamingList();
	}
	
	
	//방송별 접속자 수 
	public void liveViewCountMonitor() throws Exception {
		
		/* 방송 접속자 수 */
		List<String> onAirList = liveMonitorBean.onAirList(); //방송 list (채팅 o)
		if(onAirList == null || onAirList.size() == 0) {
			return;
		}
			
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("broadcastList", onAirList);

		WebClient webClient = WebClient.builder()
										.baseUrl("http://"+chatIp+":"+chatPort)
										.build();
		List<LiveViewsVO> rtnList = (List<LiveViewsVO>) webClient.post()
				.uri("/api/getLiveViewsCount")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(new Gson().toJson(map))
				.retrieve()
				.bodyToMono(LiveViewsDTO.class)
				.block()
				.getViewsList()
				;
		if (rtnList.size() <= 0) { return;}
		try {
			liveMonitorBean.insertLiveViewsList(rtnList);
		} catch (Exception e) {
			logger.error("LiveMonitorService . liveViewCountMonitor() :: "+e.getMessage());
		}
	}
	
}
