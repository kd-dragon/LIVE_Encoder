package com.kdy.app.dto.api.response;

import java.util.List;

import com.kdy.app.dto.api.request.RequestBroadcastListDTO;
import com.kdy.app.dto.live.AppBroadcastVO;
import com.kdy.app.dto.live.ResultVO;

import lombok.Data;

@Data
public class ResponseBroadcastListExcelDTO {
	
	private List<AppBroadcastVO> broadcasts; //방송 리스트
	private ResultVO result;
	private RequestBroadcastListDTO request;
	
}
