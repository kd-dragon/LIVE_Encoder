package com.kdy.app.dto.channel;

import java.util.List;

import lombok.Data;

@Data
public class ChannelDTO {

	List<ChannelVO> channelList;
	private int totalCount;
	private int blockCount;
	private int currentPage=1;
	private int startNum;
	private int totalPage;
	private String pagingHtml;
	
}
