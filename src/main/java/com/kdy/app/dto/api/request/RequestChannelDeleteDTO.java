package com.kdy.app.dto.api.request;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="라이브 방송 채널 삭제")
public class RequestChannelDeleteDTO {
	
	@ApiModelProperty(value="라이브 채널 삭제 리스트")
	@NotNull(message="deleteSeqs is not null")
	@Size(min=1, message="deleteSeqs must be at least 1")
	private List<String> deleteSeqs;
	

}
