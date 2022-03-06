package com.kdy.app.service.IF;

import java.util.List;

import com.kdy.app.dto.system.StreamingVO;
import com.kdy.live.dto.monitor.LiveMonitorDTO;

public interface MonitorServiceIF {
	
	public List<StreamingVO> getStreamingList() throws Exception;
	
	public LiveMonitorDTO getMonitorData(LiveMonitorDTO dto) throws Exception;

}
