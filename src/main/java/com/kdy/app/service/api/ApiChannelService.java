package com.kdy.app.service.api;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kdy.app.bean.api.ApiChannelBean;
import com.kdy.app.dto.api.request.RequestChannelDeleteDTO;
import com.kdy.app.dto.api.request.RequestChannelInsertDTO;
import com.kdy.app.dto.api.request.RequestChannelUpdateDTO;
import com.kdy.app.dto.channel.ChannelVO;
import com.kdy.app.dto.live.ResultVO;
import com.kdy.app.service.api.IF.ApiChannelServiceIF;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApiChannelService implements ApiChannelServiceIF{
	
	private final ApiChannelBean channelBean; 

	//채널 리스트
	@Override
	public List<ChannelVO> channelList() throws Exception {
		return channelBean.channelList();
	}
	
	//채널 추가
	@Override
	public ResultVO addChannel(RequestChannelInsertDTO dto) throws Exception {
		
		ResultVO resultVO = new ResultVO();
		
		//채널명 중복확인
		if(channelBean.beforeAddChannelNameChk(dto.getLcName())) {
			resultVO.setRslt(false);
			resultVO.setRsltMsg("channel name exist");
			resultVO.setRsltDesc("이미 등록된 채널명입니다.");
			return resultVO;
		}
		
		//채널 등록
		if(channelBean.addChannel(dto)) {
			resultVO.setRslt(true);
			resultVO.setRsltMsg("SUCCESS");
			resultVO.setRsltDesc("성공적으로 등록되었습니다.");
			
		} else {
			resultVO.setRslt(false);
			resultVO.setRsltMsg("Insert Channel Not Found");
			resultVO.setRsltDesc("등록할 채널이 없습니다.");
		}
		
		return resultVO;
	}

	//채널 수정
	@Override
	public ResultVO modChannel(RequestChannelUpdateDTO dto) throws Exception {
		
		ResultVO resultVO = new ResultVO();
		
		//이름 수정시 다른  채널명과 겹치는지 확인
		if(channelBean.beforeModChannelNameChk(dto)) {
			resultVO.setRslt(false);
			resultVO.setRsltMsg("channel name exist");
			resultVO.setRsltDesc("이미 등록된 채널명입니다.");
			return resultVO;
		}
		
		//채널 수정
		if(channelBean.modChannel(dto)) {
			resultVO.setRslt(true);
			resultVO.setRsltMsg("SUCCESS");
			resultVO.setRsltDesc("성공적으로 수정되었습니다.");
			
		} else {
			resultVO.setRslt(false);
			resultVO.setRsltMsg("Update Channel Not Found");
			resultVO.setRsltDesc("수정할 채널이 없습니다.");
		}
			
		return resultVO;
	}

	//채널 삭제
	@Override
	public ResultVO delChannel(RequestChannelDeleteDTO dto) throws Exception {
		
		ResultVO resultVO = new ResultVO();
		
		if(channelBean.delChannel(dto)) {
			resultVO.setRslt(true);
			resultVO.setRsltMsg("SUCCESS");
			resultVO.setRsltDesc("성공적으로 삭제되었습니다.");
			
		} else {
			resultVO.setRslt(false);
			resultVO.setRsltMsg("Update Channel Not Found");
			resultVO.setRsltDesc("삭제할 채널이 없습니다.");
		}
		
		return resultVO;
	}

}
