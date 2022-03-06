package com.kdy.app.dto.api.request;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="방송 채널 등록")
public class RequestChannelInsertDTO {
	
	@ApiModelProperty(value="라이브 채널 시퀀스 (자동등록)")
	private String lcSeq; 
	
	@ApiModelProperty(value="라이브 채널 URL", example="rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov", required=true)
	@NotBlank(message="live channel url is not null")
	private String lcUrl; 
	
	@ApiModelProperty(value="라이브 채널명", example="testChannel", required=true)
	@NotBlank(message="live channel name is not null")
	private String lcName; 
	
	@ApiModelProperty(value="라이브채널 상세설명")
	private String lcDesc;
	

}
