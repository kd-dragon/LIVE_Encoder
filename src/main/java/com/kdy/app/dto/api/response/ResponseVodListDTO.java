package com.kdy.app.dto.api.response;

import java.util.List;

import com.kdy.app.dto.live.ResultVO;
import com.kdy.app.dto.vod.VodVO;

import lombok.Data;

@Data
public class ResponseVodListDTO {
	
	private List<VodVO> vodList;
	private int tatalCount;
	
	private ResultVO result;

}
