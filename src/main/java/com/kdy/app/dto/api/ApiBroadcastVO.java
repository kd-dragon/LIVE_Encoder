package com.kdy.app.dto.api;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class ApiBroadcastVO {
	
	//broadcast
	private String lbSeq;
	
	@NotNull(message="Title must not be Null")
	@Size(min=1, max=100, message="Title size must be 1~100")
	private String lbTitle;
	private String lbStatus;
	@NotEmpty(message="Channel ID is Empty")
	private String lcName;
	private String lbRegUserId;
	private String lbOpenYn;
	private String lbRegDate;
	@NotEmpty(message="StartDate is Empty")
	private String lbStartDate;
	@NotEmpty(message="EndDate is Empty")
	private String lbEndDate;
	
	
	private String lbjDuration; 
	
	
	private String totalDuration;  //시작시간, 종료시간으로 계산 (예상 방송 시간)
	private String passDuration;   //진행 방송 시간 (실제로 방송한 시간)
	
	//썸네일
	private String lbfImgEncPath;
	private String lbfImgEncNm;
	
	private String fullCategoryName;
	
	private String lbDesc;
}
