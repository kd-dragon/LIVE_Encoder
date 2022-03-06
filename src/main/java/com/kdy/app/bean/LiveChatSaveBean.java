package com.kdy.app.bean;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdy.app.bean.dao.LiveBroadcastDAO;
import com.kdy.app.dto.live.AppBroadcastListDTO;
import com.kdy.live.dto.LiveSchedMemoryVO.RedisHashKeyword;
import com.kdy.live.dto.live.ChatDTO;

@Component
public class LiveChatSaveBean {
	private Logger logger = LoggerFactory.getLogger(getClass());

	private final LiveBroadcastDAO dao;
	private final RedisTemplate<String, Object> redisTemplate;
	private final ObjectMapper objectMapper;
	
	@Autowired
	public LiveChatSaveBean(  LiveBroadcastDAO 													dao
							, @Qualifier("redisTemplateObject") RedisTemplate<String, Object> 	redisTemplate
							, ObjectMapper 														objectMapper) {
		
		this.dao 			= dao;
		this.redisTemplate 	= redisTemplate;
		this.objectMapper   = objectMapper;
	}
	
	public int save(List<ChatDTO> list) throws Exception {
		return dao.insertLiveChatting(list);
	}
	
	public List<ChatDTO> getChattingListExcel(AppBroadcastListDTO dto) throws Exception{
		return dao.getChattingListExcel(dto);
	}
	
	public boolean checkChattingInRedis(String lbSeq, String reqType) throws Exception {
		String chatKey = RedisHashKeyword.BACKUP.toString() + lbSeq;
		
		int retval = 0;
		List<ChatDTO> chatDtoList =  objectMapper.convertValue(redisTemplate.opsForList().range(chatKey, 0, -1), new TypeReference<List<ChatDTO>>() {});
		
		if(chatDtoList != null && chatDtoList.size() > 0) {
			int listSize = chatDtoList.size();
			List<ChatDTO> tempList = new ArrayList<ChatDTO>();
			
			for(int i=0; i<listSize; i++) {
				tempList.add(chatDtoList.get(i));
				if(i > 0 && i % 200 == 0) {
					retval += this.save(tempList);
					tempList = new ArrayList<ChatDTO>();
				} else if (i == listSize - 1) {
					retval += this.save(tempList);
				}
			}
			boolean isDel = redisTemplate.delete(chatKey);
			logger.info("[{} Request] Chatting [{}] Insert Finished | Success cnt : {}, Deleted : {}", reqType, chatKey, retval, isDel);
		}
		return retval > 0;
	}
	
	public boolean checkChattingInDB(String lbSeq) throws Exception {
		return dao.getChattingListCountBySeq(lbSeq) > 0 ;
	}
	
}
