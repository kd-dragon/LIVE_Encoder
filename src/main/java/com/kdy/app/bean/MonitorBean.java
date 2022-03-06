package com.kdy.app.bean;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kdy.app.bean.dao.MonitorDAO;
import com.kdy.app.dto.system.StreamingVO;
import com.kdy.live.dto.monitor.LiveMonitorDTO;
import com.kdy.live.dto.monitor.LiveMonitorVO;

@Component
public class MonitorBean {
	
	private final MonitorDAO dao;

	@Autowired
	public MonitorBean(MonitorDAO dao) {
		super();
		this.dao = dao;
	}
	
	public List<StreamingVO> getStreamingList() throws Exception{
		return dao.getStreamingList();
	}
	
	//CPU/MEMORY/Drive
	public List<LiveMonitorVO> getMonitorStatData(LiveMonitorDTO dto) throws Exception{
		return dao.getMonitorStatData(dto);
	}
	
	
	//접속자수 화면 데이터
	public List<LiveMonitorVO> getMonitorViewsData(LiveMonitorDTO dto) throws Exception{
		
		List<String> dateList = dao.getDateList();
		
		if(dateList != null && dateList.size() > 0) {
			dto.setDateList(dateList);
			
			for(int i=0; i<dateList.size(); i++) {
				String date = dateList.get(i);
				
				switch (i) {
				case 0:
					dto.setDate1(date);
					break;
				case 1:
					dto.setDate2(date);
					break;
				case 2:
					dto.setDate3(date);
					break;
				case 3:
					dto.setDate4(date);
					break;
				case 4:
					dto.setDate5(date);
					break;
				case 5:
					dto.setDate6(date);
					break;
				case 6:
					dto.setDate7(date);
					break;

				default:
					break;
				}
			}
			
			return dao.getMonitorViewsData(dto);
			
		} else {
			return null;
		}
		
		
	}
	
}
