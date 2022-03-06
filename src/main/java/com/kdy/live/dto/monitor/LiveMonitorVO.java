package com.kdy.live.dto.monitor;

import lombok.Data;

@Data
public class LiveMonitorVO {
	
	private Double cpu;
	private Double memory;
	private Double disk;
	
	private long inbound;
	private long outbound;
	
	private String streamingSeq;
	
	private String monitorDate;
	private String monitorData;
	
	//views
	private String lbSeq;
	private String lbTitle;
	private String date1;
	private String date2;
	private String date3;
	private String date4;
	private String date5;
	private String date6;
	private String date7;
	
}
