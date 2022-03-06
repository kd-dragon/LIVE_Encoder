package com.kdy.app.dto.api.response;

import java.util.List;

import com.kdy.app.dto.api.request.RequestBroadcastListDTO;
import com.kdy.app.dto.live.AppBroadcastVO;
import com.kdy.app.dto.live.ResultVO;

import lombok.Data;

@Data
public class ResponseBroadcastListDTO {
	
	private List<AppBroadcastVO> broadcasts; //방송 리스트
	
	private int totalCount; //방송 총 갯수
	private int currentPage; //현재 페이지
	private int blockCount; //페이징 단위
	
	private ResultVO result;
	private RequestBroadcastListDTO request;
	
	private int onairCnt;
	private int pauseCnt;
	private int errorCnt;
	private int restartCnt;
	private int totalStatusCnt;
	
}
