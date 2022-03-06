package com.kdy.app.bean.dao.api;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kdy.app.dto.api.request.RequestChannelDeleteDTO;
import com.kdy.app.dto.api.request.RequestChannelInsertDTO;
import com.kdy.app.dto.api.request.RequestChannelUpdateDTO;
import com.kdy.app.dto.channel.ChannelVO;

@Repository
public class ApiChannelDAO {
	
	@Autowired
	SqlSessionTemplate sqlSessionTemplate;
	
	//채널 리스트
	public List<ChannelVO> channelList() throws Exception{
		return sqlSessionTemplate.selectList("api.channelList");
	}

	//채널 추가 전 중복 채널명 확인
	public int beforeAddChannelNameChk(String name) throws Exception {
		return sqlSessionTemplate.selectOne("api.beforeAddChannelNameChk", name);
	}
	
	//채널 추가
	public int addChannel(RequestChannelInsertDTO dto) throws Exception{
		return sqlSessionTemplate.insert("app_channel.addChannel",dto);
	}
	
	//채널 수정 전 중복 채널명 확인
	public int beforeModChannelNameChk(RequestChannelUpdateDTO dto) throws Exception {
		return sqlSessionTemplate.selectOne("api.beforeModChannelNameChk", dto);
	}
	
	//채널 수정
	public int modChannel(RequestChannelUpdateDTO dto) throws Exception{
		return sqlSessionTemplate.update("api.modChannel",dto);
	}
	
	//채널 삭제
	public int delChannel(RequestChannelDeleteDTO dto) throws Exception{
		return sqlSessionTemplate.delete("api.delChannel",dto);
	}

}
