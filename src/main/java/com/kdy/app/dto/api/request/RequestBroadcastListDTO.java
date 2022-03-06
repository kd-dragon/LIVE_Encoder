package com.kdy.app.dto.api.request;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="방송목록조회 요청")
public class RequestBroadcastListDTO {
	
	//페이징시 필요한 parameter
	@ApiModelProperty(value="페이징 단위 (default: 10)", example="10", required = true)
	private Integer blockCount;
	
	@ApiModelProperty(value="현재 페이지 번호 (default: 1)", example="1", required = true)
	private Integer currentPage=1;
	
	@ApiModelProperty(hidden=true)
	private Integer startNum;
	
	@ApiModelProperty(hidden=true)
	private String replaceRootPath;
	
	//상태값 조회시 필요
	@ApiModelProperty(value="[검색옵션] 라이브 방송 상태 (0:대기, 1:방송중, 2:방송종료, 3:일시정지, 4:재시작, 5:녹화중, 9:오류)", required = true)
    private List<String> searchStatus;
	
	//검색 조건
	@ApiModelProperty(value="[검색옵션] 방송 공개 여부 (all:전체, Y:공개, N:비공개)", example = "all")
	private String searchOpen="";
	
	@ApiModelProperty(value="[검색옵션] 검색어 타입 (all:전체, title:제목, id:등록자) default:0", example="all")
	private String searchType="";
	
	@ApiModelProperty(value="[검색옵션] 검색어")
	private String searchText="";
	
	@ApiModelProperty(value = "[검색옵션] 시작날짜 ex)20210610")
	private String searchStDate="";
	
	@ApiModelProperty(value = "[검색옵션] 종료날짜 ex)20210610")
	private String searchEnDate="";
	
	@ApiModelProperty(value="[검색옵션] 정렬 기준 (reg:동록일, broadcast:방송시작일)", example="reg")
	private String orderByType="";
	
	private int rootUpCategoryCode;
}
