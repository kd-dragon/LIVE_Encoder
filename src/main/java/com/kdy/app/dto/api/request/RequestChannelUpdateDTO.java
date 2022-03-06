package com.kdy.app.dto.api.request;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="방송 채널 수정")
public class RequestChannelUpdateDTO {
	
	@ApiModelProperty(value="라이브 채널 시퀀스", required=true)
	@NotBlank(message="live channel Seq is not null")
	private String lcSeq; 
	
	@ApiModelProperty(value="라이브 채널 URL", required=true)
	@NotBlank(message="live channel url is not null")
	private String lcUrl; 
	
	@ApiModelProperty(value="라이브 채널명", required=true)
	@NotBlank(message="live channel name is not null")
	private String lcName; 
	
	@ApiModelProperty(value="라이브채널 상세설명")
	private String lcDesc;

}
