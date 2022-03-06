package com.kdy.chat.dto;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import lombok.Data;

@Component
@Data
public class ChatMemoryDTO {
	private Map<String, UserDTO> userListMap = new ConcurrentHashMap<String, UserDTO>();
	private MultiValueMap<String, String> roomUserRepository = new LinkedMultiValueMap<String, String>();
}
