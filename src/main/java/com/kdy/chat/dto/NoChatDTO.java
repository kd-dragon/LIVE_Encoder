package com.kdy.chat.dto;

import lombok.Data;

@Data
public class NoChatDTO {

	private String userId;
	private String sessionId;
	private String lbSeq;
	private int liveUserCnt;
	private String userName;
	private noChatType type;
	private String connectStatus;
	
	public enum noChatType {
		JOIN, LEAVE
	}
}

