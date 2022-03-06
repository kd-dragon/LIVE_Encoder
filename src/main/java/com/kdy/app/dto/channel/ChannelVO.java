package com.kdy.app.dto.channel;

import lombok.Data;

@Data
public class ChannelVO {
	
	private String lcSeq; //라이브 채널 시퀀스
	private String lcUrl; //라이브채널URL
	private String lcDesc; //라이브채널 상세설명
	private String lcName; //라이브 채널명
	private String categorySeq;	//카테고리(부서) 시퀀스
	private String categoryName;

}
