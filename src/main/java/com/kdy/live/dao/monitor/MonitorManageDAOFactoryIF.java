package com.kdy.live.dao.monitor;

import java.util.List;

import com.kdy.app.dto.system.StreamingVO;
import com.kdy.live.dto.monitor.LiveMonitorVO;
import com.kdy.live.dto.monitor.LiveViewsDTO;

public interface MonitorManageDAOFactoryIF {
	
	//스트리밍 모듈 리스트 가져오기
	public List<StreamingVO> selectStreamingList() throws Exception;
	
	//스트리밍 모듈 상태정보 insert
	public int insertStreamingList(List<LiveMonitorVO> insertStreamingList) throws Exception;
	
	public int deleteStreamingList() throws Exception;
	
	//현재 라이브중인 방송 목록 가져오기
	public List<String> onAirList() throws Exception;
	
	//접속자수 Insert
	public int insertLiveViewsList(LiveViewsDTO dto) throws Exception;
	
}
