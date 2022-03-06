package com.kdy.app.service.IF;

import java.util.List;

import com.kdy.app.dto.channel.ChannelDTO;
import com.kdy.app.dto.channel.ChannelVO;
import com.kdy.app.dto.live.CategoryVO;

public interface ChannelServiceIF {
	
	//채널 리스트
	public ChannelDTO channelList(ChannelDTO dto) throws Exception;
	
	//채널 추가
	public String addChannel(ChannelVO vo) throws Exception;
	
	//채널 수정
	public String modChannel(ChannelVO vo) throws Exception;
	
	//채널 삭제
	public String delChannel(String deleteSeqs) throws Exception;
	
	//카테고리 리스트 가져오기
	public List<CategoryVO> categoryList() throws Exception;

}
