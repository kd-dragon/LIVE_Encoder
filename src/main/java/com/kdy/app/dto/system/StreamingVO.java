package com.kdy.app.dto.system;

import lombok.Data;

@Data
public class StreamingVO {
	private String streamingSeq;
	private String streamingIp;
	private String streamingPort;
	private String streamingRegDate;
	private String streamingDesc;
	private Boolean streamingStatus;
}
