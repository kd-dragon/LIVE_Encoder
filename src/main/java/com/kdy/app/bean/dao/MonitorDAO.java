package com.kdy.app.bean.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kdy.app.dto.system.StreamingVO;
import com.kdy.live.dto.monitor.LiveMonitorDTO;
import com.kdy.live.dto.monitor.LiveMonitorVO;

@Repository
public class MonitorDAO{
	
	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;
	
	public List<StreamingVO> getStreamingList() throws Exception{
		return sqlSessionTemplate.selectList("app_monitor.getStreamingList");
	}
	
	//CPU/MEMORY/Drive
	public List<LiveMonitorVO> getMonitorStatData(LiveMonitorDTO dto) throws Exception{
		return sqlSessionTemplate.selectList("app_monitor.getMonitorStatData", dto);
	}
	
	public List<String> getDateList() throws Exception{
		return sqlSessionTemplate.selectList("app_monitor.getDateList");
	}
	
	public List<LiveMonitorVO> getMonitorViewsData(LiveMonitorDTO dto) throws Exception{
		return sqlSessionTemplate.selectList("app_monitor.getMonitorViewsData", dto);
	}

}
