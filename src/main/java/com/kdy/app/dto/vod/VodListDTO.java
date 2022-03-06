package com.kdy.app.dto.vod;

import java.util.List;

import lombok.Data;

@Data
public class VodListDTO {
	
	private List<VodVO> vodList; //vod 리스트
	
	//검색 parameter
	private String searchStrDate;
	private String searchEndDate;
	private String searchRegUserName;
	private String searchVodTitle;
	private String searchFileName;
	
	private String replaceRootPath;
	
	//페이징시 필요한 parameter
	private int totalCount;
	private int blockCount=10;
	private int currentPage=1;
	private int startNo;
	private int endNo;
	private String pagingHtml;
	private int totalPage;
	
	//삭제시 필요한 Data
	private String[] vodSeqs;
	private String vodSeq;
	private String userId;
	
	private String authSeq;
	private String categorySeq;
	
	private int rootUpCategoryCode;

}
