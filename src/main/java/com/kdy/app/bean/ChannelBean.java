package com.kdy.app.bean;

import java.util.List;

import org.springframework.stereotype.Component;

import com.kdy.app.bean.dao.ChannelDAO;
import com.kdy.app.dto.channel.ChannelDTO;
import com.kdy.app.dto.channel.ChannelVO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChannelBean {
	
	private final ChannelDAO dao;
	
	//채널 리스트
	public List<ChannelVO> channelList(ChannelDTO dto) throws Exception{
		return dao.channelList(dto);
	}
	
	//채널 리스트 개수
	public int getChannelListCount(ChannelDTO dto) throws Exception {
		return dao.selectChannelListCount(dto);
	}
	
	//채널명 중복검사
	public int channelNameExistChk(ChannelVO vo) throws Exception{
		return dao.channelNameExistChk(vo);
	}
	
	//채널 추가
	public String addChannel(ChannelVO vo) throws Exception{
		
		if(dao.addChannel(vo) > 0) {
			return "SUCCESS";
		}
		return "FAIL";
	}
	
	//채널 수정
	public String modChannel(ChannelVO vo) throws Exception{
		
		if(dao.modChannel(vo) > 0) {
			return "SUCCESS";
		}
		return "FAIL";
	}
	
	//채널 삭제
	public String delChannel(String[] deleteSeqArr) throws Exception{
		
		if(dao.delChannel(deleteSeqArr)>0) {
			return "SUCCESS";
		}
		return "FAIL";
	}

}
