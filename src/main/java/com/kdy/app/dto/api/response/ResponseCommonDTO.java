package com.kdy.app.dto.api.response;

import com.kdy.app.dto.live.ResultVO;

import lombok.Data;

@Data
public class ResponseCommonDTO {
	
	private Object request;  // 전송 요청시 전달했던 객체
	private ResultVO result; // 전송 결과
}
