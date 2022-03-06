package com.kdy.app.dto.api.request;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="방송목록 엑셀다운로드 요청")
public class RequestBroadcastExcelDTO {
	
	//상태값 조회시 필요
	@ApiModelProperty(value="[검색옵션] 라이브 방송 상태 (0:대기, 1:방송중, 2:방송종료, 3:일시정지, 4:재시작, 5:녹화중, 9:오류)", example="['0','1']" )
	private List<String> searchStatus;
	
	//정렬
	@ApiModelProperty(value="[정렬] 라이브 목록 정렬 기준 (start: 방송시작일시순, reg: 등록일순)", example="start")
	private String orderByType;
	
}
