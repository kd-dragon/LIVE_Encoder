package com.kdy.app.service.api.IF;

import java.util.List;

import com.kdy.app.dto.api.request.RequestChannelDeleteDTO;
import com.kdy.app.dto.api.request.RequestChannelInsertDTO;
import com.kdy.app.dto.api.request.RequestChannelUpdateDTO;
import com.kdy.app.dto.channel.ChannelVO;
import com.kdy.app.dto.live.ResultVO;

public interface ApiChannelServiceIF {

	public List<ChannelVO> channelList() throws Exception;
	
	public ResultVO addChannel(RequestChannelInsertDTO dto) throws Exception;
	
	public ResultVO modChannel(RequestChannelUpdateDTO dto) throws Exception;
	
	public ResultVO delChannel(RequestChannelDeleteDTO dto) throws Exception;
	
}
