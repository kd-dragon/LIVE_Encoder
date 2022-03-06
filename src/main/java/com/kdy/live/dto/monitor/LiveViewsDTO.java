package com.kdy.live.dto.monitor;

import java.util.List;

import lombok.Data;

@Data
public class LiveViewsDTO {
	private List<LiveViewsVO> viewsList;
	
	private String year;
	private String month;
	private String day;
	private String hour;
	private String minute;
}
