package com.kdy.app.dto.api.request;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="방송 전체 ")
public class RequestBroadcastAllDTO {
	
	@ApiModelProperty(hidden=true)
	private String replaceRootPath;
	
	//상태값 조회시 필요
	@ApiModelProperty(value="[검색옵션] 라이브 방송 상태 (0:대기, 1:방송중, 2:방송종료, 3:일시정지, 4:재시작, 5:녹화중, 9:오류)", required = true)
    private List<String> searchStatus;
	
	//검색 조건
	@ApiModelProperty(value="[검색옵션] 방송 공개 여부 (Y:공개, N:비공개)")
	private String searchOpen="";
	
	@ApiModelProperty(value="[검색옵션] 검색어 타입 (title:제목, id:등록자) ")
	private String searchType="";
	
	@ApiModelProperty(value="[검색옵션] 검색어")
	private String searchText="";
	
	@ApiModelProperty(value = "[검색옵션] 시작날짜 ex)2021-06-10")
	private String searchStDate="";
	
	@ApiModelProperty(value = "[검색옵션] 종료날짜 ex)2021-06-10")
	private String searchEnDate="";
	
	@ApiModelProperty(value="[검색옵션] 정렬 기준 (reg:방송동록일, start:방송시작일)", example="reg")
	private String orderByType="reg";
	
	@ApiModelProperty(hidden=true)
	private int rootUpCategoryCode;
	
}
