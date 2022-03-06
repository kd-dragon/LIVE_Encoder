package com.kdy.live.bean.montior;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.gson.Gson;
import com.kdy.app.dto.system.StreamingVO;
import com.kdy.live.dto.monitor.LiveMonitorVO;

@Component
public class LiveStreamingMonitorBean {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Value("${live.monitor.useYn}")
	private String monitorUseYn;
	
	@Value("${live.monitor.userId}")
	private String userId;
	
	@Value("${live.monitor.userPw}")
	private String userPw;
	
	@Value("${live.monitor.port}")
	private String port;
	
	
	private WebClient client = null;
	private String token = null;
	
	public LiveMonitorVO getSteamingStatus(StreamingVO svo) throws Exception{
		
		String url = "http://"+svo.getStreamingIp()+":"+port;
		
		client = WebClient.builder()
				  .baseUrl(url)
				  .build();
		
		LiveMonitorVO lvo = new LiveMonitorVO();
		lvo.setStreamingSeq(svo.getStreamingSeq());
		
		//라이브 스트리밍 로그인
		if(!liveStreamLogin()) { //스트리밍 연결 실패시
			return null;
		}
		
		
		lvo.setStreamingSeq(svo.getStreamingSeq());
		lvo.setDisk(getDiskGbyte());
		lvo.setCpu(getCPUGbyte());
		lvo.setMemory(getMemoryGbyte());
		
		
		return lvo;
	}
	
	//Stream 모듈 actuator 접근할 수 있도록 로그인/Token값 가져오기
	private boolean liveStreamLogin() {
		
		try {
			
			Gson gson = new Gson();
			
			JSONObject json = new JSONObject();
			json.put("username", userId);
			json.put("password", userPw);
			
			String rtn =  client.post()
					.uri("/monitorLogin.do")
					.accept(MediaType.APPLICATION_JSON)
					.bodyValue(json)
					.retrieve().bodyToMono(String.class)
					.block();

			Map<String, Object> rtnMap = new HashMap<String, Object>();
			rtnMap = gson.fromJson(rtn, rtnMap.getClass()); 
			
			String status = (String)rtnMap.get("status");
			
			if(status.equals("SUCCESS")) {
				token = rtnMap.get("token").toString();
			}
			
			
			return true;
			
		} catch (Exception e) {
			logger.error("[Error] Live Stream Sched :: Login Fail "+e.getMessage());
			token = null;
			return false;
		}
		
	}
	
	private double getDiskGbyte() {
		try {
			
			String rtn = client.get()
					.uri("/monitor/system.dist.usage")
					.header("Authorization", "Bearer "+ token)
					.retrieve().bodyToMono(String.class)
					.block();
			
			Gson gson = new Gson();
			Map<String, Object> map = new HashMap<String, Object>();
			map = gson.fromJson(rtn, map.getClass());  
			
			String disk = (String)map.get("value");
			long diskSize = Long.parseLong(disk);
			
			return toGB(diskSize);
			
		}catch (Exception e) {
			logger.error("[Error] Live Stream Sched :: Disk Data "+ e.getMessage());
			return 0.0;
		}
		
		
	}
	
	private double getCPUGbyte() throws URISyntaxException {
		
		try {
			
			String rtn = client.get()
						.uri("/monitor/metrics/system.cpu.usage")
						.header("Authorization", "Bearer "+ token)
						.retrieve().bodyToMono(String.class)
						.block();
					
			Gson gson = new Gson();
			Map<String, Object> map = new HashMap<String, Object>();
			map = gson.fromJson(rtn, map.getClass());  
			
			double cpu = (double) ((Map)((List)map.get("measurements")).get(0)).get("value");
			return cpu * 100;
			
		} catch (Exception e) {
			logger.error("[Error] Live Stream Sched :: CPU Data "+ e.getMessage());
			return 0.0;
		}
		
	}
	
	private double getMemoryGbyte() {
		try {
			
			String rtn = client.get()
					.uri("/monitor/metrics/jvm.memory.used")
					.header("Authorization", "Bearer "+ token)
					.retrieve().bodyToMono(String.class)
					.block();
					
			Gson gson = new Gson();
			Map<String, Object> map = new HashMap<String, Object>();
			map = gson.fromJson(rtn, map.getClass());  

			Double mem = (Double) ((Map)((List)map.get("measurements")).get(0)).get("value");
			
			return (long) mem.longValue()/(1e+6); //mb
			
		} catch (Exception e) {
			logger.error("[Error] Live Stream Sched :: Memory Data "+ e.getMessage());
			return 0.0;
		}
	}
	
	/*
	public void getTcpNetTraffic(LiveMonitorVO vo) throws Exception {
		Sigar sigar = new Sigar();
		
		long inboundBytes = 0;
	    long outboundBytes = 0;
		 
		for (String name : sigar.getNetInterfaceList()) {
		  inboundBytes += sigar.getNetInterfaceStat(name).getRxBytes();
		  outboundBytes += sigar.getNetInterfaceStat(name).getTxBytes();
		}
	    
	    NetStat stat = sigar.getNetStat();
		
	    vo.setInbound(stat.getAllInboundTotal());
	    vo.setOutbound(stat.getAllOutboundTotal());
	    
	}
	 */
	
	
	private double toGB(long size) {
		return size/1024/1024/1024;
	}
	
	

}
