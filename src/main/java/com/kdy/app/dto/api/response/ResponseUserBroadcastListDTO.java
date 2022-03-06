package com.kdy.app.dto.api.response;

import java.util.List;

import com.kdy.app.dto.live.AppBroadcastVO;
import com.kdy.app.dto.live.ResultVO;

import lombok.Data;

@Data
public class ResponseUserBroadcastListDTO {
	
	private List<AppBroadcastVO> onairbroadcasts; // 방송중인 방송 리스트
	private List<AppBroadcastVO> waitbroadcasts; // 방송 대기중인 방송 리스트
	
	private ResultVO result;

}
