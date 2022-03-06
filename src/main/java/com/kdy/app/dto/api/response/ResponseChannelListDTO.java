package com.kdy.app.dto.api.response;

import java.util.List;

import com.kdy.app.dto.channel.ChannelVO;
import com.kdy.app.dto.live.ResultVO;

import lombok.Data;

@Data
public class ResponseChannelListDTO {
	
	private List<ChannelVO> channelList;
	
	private ResultVO result;

}
