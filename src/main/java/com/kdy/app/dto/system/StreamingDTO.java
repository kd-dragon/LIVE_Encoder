package com.kdy.app.dto.system;

import java.util.List;

import lombok.Data;

@Data
public class StreamingDTO {
	
	private List<StreamingVO> streamingList;
	
	private int totalCount;
	private int blockCount;
	private int currentPage=1;
	private int startNum;
	private int totalPage;
	private String pagingHtml;
	
	private String dupChk;
	
	private String streamingSeq;
	private String streamingIp;
	private String streamingDesc;
}
