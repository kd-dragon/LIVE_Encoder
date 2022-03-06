package com.kdy.live.dto.monitor;

import java.util.List;

import lombok.Data;

@Data
public class LiveMonitorDTO {
	
	private String type;
	private String streamingSeq;
	
	private List<LiveMonitorVO> liveMonitorList;
	
	
	//접속자 수 가져오기
	private List<String> dateList;
	private String date1;
	private String date2;
	private String date3;
	private String date4;
	private String date5;
	private String date6;
	private String date7;
	
	private String viewsType;
	
	private String authSeq;
	private String categorySeq;

}
