package com.kdy.app.dto.api.request;

import java.util.List;

import lombok.Data;

@Data
public class RequestUserBroadcastListDTO {
	
	private String searchTag;
	
	
	private String replaceRootPath;
	private List<String> lbStatusList;

}
