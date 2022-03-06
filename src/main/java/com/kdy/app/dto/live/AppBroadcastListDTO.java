package com.kdy.app.dto.live;

import java.util.List;

import lombok.Data;

@Data
public class AppBroadcastListDTO {
	
	private List<AppBroadcastVO> liveList; //방송 리스트
	
	//페이징시 필요한 parameter
	private int totalCount;
	private int blockCount;
	private int currentPage=1;
	private int startNum;
	private int totalPage;
	
	private String pagingHtml;
	
	private String replaceRootPath;
	
	private String searchStatus="";
	
	//상태값 조회시 필요
	private List<String> searchStatusList;
	
	//검색 조건
	private String searchOpen="";  		//공개구분
	private String searchTextType="";	//검색어 상태	
	private String searchText="";		//검색어
	private String searchStDate="";		//시작 날짜
	private String searchEnDate="";		//종료 날짜
	private String orderByType="";		//정렬 기준
	
	//상태별 카운트
	private int onairCnt;
	private int pauseCnt;
	private int errorCnt;
	private int restartCnt;
	private int startCnt;
	private int totalStatusCnt;
	
	//삭제시 필요한 parameter
	private String[] lbSeqs;
	private String lbSeq;
	
	private int rootUpCategoryCode;
	
	private String authSeq;
	private String categorySeq;
	
}
