package com.kdy.app.dto.system;

import java.util.List;

import lombok.Data;

@Data
public class LoginIpLogDTO {

	private List<LoginIpLogVO> ipLogList;
	
	private int totalCount;
	private int blockCount;
	private int currentPage=1;
	private int startNum;
	private int totalPage;
	private String pagingHtml;
	
	private String searchIp="";
	private String searchText="";
	private String searchTextType="";
	private String searchStDate="";
	private String searchEnDate="";
	
}
