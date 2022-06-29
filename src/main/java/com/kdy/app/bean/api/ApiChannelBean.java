package com.kdy.app.bean.api;

import java.util.List;

import org.springframework.stereotype.Component;

import com.kdy.app.bean.dao.api.ApiChannelDAO;
import com.kdy.app.dto.api.request.RequestChannelDeleteDTO;
import com.kdy.app.dto.api.request.RequestChannelInsertDTO;
import com.kdy.app.dto.api.request.RequestChannelUpdateDTO;
import com.kdy.app.dto.channel.ChannelVO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ApiChannelBean {
	
	private final ApiChannelDAO dao;
	
	//채널 리스트
	public List<ChannelVO> channelList() throws Exception{
		return dao.channelList();
	}
	
	//채널 추가 전 중복 채널명 확인
	public boolean beforeAddChannelNameChk(String name) throws Exception{
		return dao.beforeAddChannelNameChk(name) > 0;
	}

	//채널 추가
	public boolean addChannel(RequestChannelInsertDTO dto) throws Exception {
		return dao.addChannel(dto) > 0;
	}
	
	//채널 수정 전 중복 채널명 확인
	public boolean beforeModChannelNameChk(RequestChannelUpdateDTO dto) throws Exception{
		return dao.beforeModChannelNameChk(dto) > 0;
	}
	
	//채널 수정
	public boolean modChannel(RequestChannelUpdateDTO dto) throws Exception{
		return dao.modChannel(dto) > 0;
	}
	
	//채널 삭제
	public boolean delChannel(RequestChannelDeleteDTO dto) throws Exception{
		return dao.delChannel(dto) > 0;
	}
	
}
