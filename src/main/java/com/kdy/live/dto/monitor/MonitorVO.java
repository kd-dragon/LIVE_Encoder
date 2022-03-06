package com.kdy.live.dto.monitor;

import java.util.List;

import com.kdy.live.dto.live.LiveBroadcastVO;

import lombok.Data;

@Data
public class MonitorVO {
	private String method;
	private String message;
	private String result;
	private List<LiveBroadcastVO> liveInfo;
	
	private String lbSeq;
	private LiveBroadcastVO liveBroadcastVO;
}
