package com.kdy.app.dto.api.response;

import java.util.List;

import com.kdy.app.dto.live.ResultVO;
import com.kdy.live.dto.monitor.LiveMonitorVO;

import lombok.Data;

@Data
public class ResponeseMonitorDTO {

	private List<LiveMonitorVO> monitorList; //모니터 데이터 리스트
	
	private ResultVO result;
	
}
