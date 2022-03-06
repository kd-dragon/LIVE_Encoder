package com.kdy.app.bean.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kdy.app.dto.channel.ChannelDTO;
import com.kdy.app.dto.channel.ChannelVO;

@Repository
public class ChannelDAO {
	
	@Autowired
	SqlSessionTemplate sqlSessionTemplate;
	
	//채널 리스트 가져오기
	public List<ChannelVO> channelList(ChannelDTO dto) throws Exception{
		return sqlSessionTemplate.selectList("app_channel.channelList", dto);
	}
	
	//채널 리스트 개수
	public int selectChannelListCount(ChannelDTO dto) throws Exception {
		return sqlSessionTemplate.selectOne("app_channel.channelListCount", dto);
	}
	
	//채널명 중복 검사
	public int channelNameExistChk(ChannelVO vo) throws Exception{
		return sqlSessionTemplate.selectOne("app_channel.channelNameExistChk", vo);
	}
	
	//채널 추가
	public int addChannel(ChannelVO vo) throws Exception{
		return sqlSessionTemplate.insert("app_channel.addChannel",vo);
	}
	
	//채널 수정
	public int modChannel(ChannelVO vo) throws Exception{
		return sqlSessionTemplate.update("app_channel.modChannel",vo);
	}
	
	//채널 삭제
	public int delChannel(String[] deleteSeqArr) throws Exception{
		return sqlSessionTemplate.delete("app_channel.delChannel",deleteSeqArr);
	}

}
