package com.kdy.app.dto.vod;

import java.util.List;

import lombok.Data;

@Data
public class LiveVodDTO {
	
	private List<LiveVodVO> liveVodList;
	
	//페이징시 필요한 parameter
	private int totalCount;
	private int blockCount=5;
	private int currentPage=1;
	private int startNo;
	private int endNo;
	private String pagingHtml;
	
	//검색 parameter
	private String lbCategorySeq="";
	private String contentsSeq="";
	private String videoTitle="";
	
	private String authSeq;
	
}
