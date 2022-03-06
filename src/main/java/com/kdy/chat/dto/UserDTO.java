package com.kdy.chat.dto;

import lombok.Data;

@Data
public class UserDTO {
	private String userId;
	private String sessionId;
	private String roomId;
	private String userName;
}
