package com.kdy.live.bean.montior;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kdy.app.dto.system.StreamingVO;
import com.kdy.live.dao.monitor.MoinitorManageDAOFactory;
import com.kdy.live.dto.monitor.LiveMonitorVO;
import com.kdy.live.dto.monitor.LiveViewsDTO;
import com.kdy.live.dto.monitor.LiveViewsVO;

@Component
public class LiveMonitorBean {
	
	@Autowired
	private MoinitorManageDAOFactory monitorManageDAOFactory;
	
	
	//스트리밍 모듈 리스트 가져오기
	public List<StreamingVO> selectStreamingList() throws Exception {
		return monitorManageDAOFactory.getDAO().selectStreamingList();
	}
	
	//스트리밍 모듈 상태정보 insert
	public int insertStreamingList(List<LiveMonitorVO> insertStreamingList) throws Exception{
		return monitorManageDAOFactory.getDAO().insertStreamingList(insertStreamingList);
	}
	
	//스트리밍 모듈 상태정보 이전 정보 delete
	public int deleteStreamingList() throws Exception{
		return monitorManageDAOFactory.getDAO().deleteStreamingList();
	}
	
	//채팅 여부에 따른 방송 중 목록 가져오기
	public List<String> onAirList() throws Exception{
		return monitorManageDAOFactory.getDAO().onAirList();
	}

	//접속자 수 insert
	public int insertLiveViewsList(List<LiveViewsVO> list) throws Exception {
		
		LiveViewsDTO dto = new LiveViewsDTO();
		dto.setViewsList(list);
		
		//날짜 세팅
		dto.setYear(new SimpleDateFormat("yyyy").format(new Date()));
		dto.setMonth(new SimpleDateFormat("MM").format(new Date()));
		dto.setDay(new SimpleDateFormat("dd").format(new Date()));
		dto.setHour(new SimpleDateFormat("HH").format(new Date()));
		dto.setMinute(new SimpleDateFormat("mm").format(new Date()));
		
		return monitorManageDAOFactory.getDAO().insertLiveViewsList(dto);
	}
	
}
