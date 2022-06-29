package com.kdy.app.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kdy.app.bean.MonitorBean;
import com.kdy.app.dto.system.StreamingVO;
import com.kdy.app.service.IF.MonitorServiceIF;
import com.kdy.live.dto.monitor.LiveMonitorDTO;
import com.kdy.live.dto.monitor.LiveMonitorVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MonitorService implements MonitorServiceIF{
	
	private final MonitorBean monitorBean;

	@Override
	public List<StreamingVO> getStreamingList() throws Exception {
		return monitorBean.getStreamingList();
	}
	
	@Override
	public LiveMonitorDTO getMonitorData(LiveMonitorDTO dto) throws Exception {
		
		List<LiveMonitorVO> list = null;
		
		if(dto.getType().equals("views")) {
			list = monitorBean.getMonitorViewsData(dto);
		} else {
			list = monitorBean.getMonitorStatData(dto);
		}
		
		if(list == null || list.size() == 0) {
			list = new ArrayList<LiveMonitorVO>();
		}
		
		dto.setLiveMonitorList(list);
		
		return dto;
	}


}
