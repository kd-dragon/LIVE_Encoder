package com.kdy.app.dto.userHome;

import java.util.List;
import lombok.Data;

@Data
public class NoticeListDTO {
	
	//공지사항 리스트
	private List<NoticeVO> noticeList;
	
	//페이징시 필요한 parameter
	private int totalCount;
	private int blockCount=10;
	private int currentPage=1;
	private int startNo;
	private int endNo;
	private int totalPage;
	private String pagingHtml;
	
	//검색
	private String searchType="";
	private String searchText="";
	private String searchStrDate="";
	private String searchEndDate="";
	
	private String userId;
	
	
}
