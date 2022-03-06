package com.kdy.app.dto.api.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "VOD 저장목록 조회 요청")
public class RequestVodListDTO {
	
	//검색 parameter
	@ApiModelProperty(value = "[검색옵션] 시작날짜 ex)20210610")
	private String searchStrDate;
	
	@ApiModelProperty(value = "[검색옵션] 종료날짜 ex)20210610")
	private String searchEndDate;
	
	@ApiModelProperty(value="[검색옵션] 등록자 ID 검색")
	private String searchRegUserid;
	
	@ApiModelProperty(value="[검색옵션] VOD 제목 검색")
	private String searchVodTitle;
	
	@ApiModelProperty(value="[검색옵션] VOD 파일명 검색")
	private String searchFileName;
	
	
	//페이징시 필요한 parameter
	@ApiModelProperty(value="페이징 단위 (default: 10)", example="10", required = true)
	private int blockCount=10;
	
	@ApiModelProperty(value="현재 페이지 번호 (default: 1)", example="1", required = true)
	private int currentPage=1;
	
	@ApiModelProperty(hidden=true)
	private Integer startNo;
	
	
	@ApiModelProperty(hidden=true)
	private String replaceRootPath;
	
}
