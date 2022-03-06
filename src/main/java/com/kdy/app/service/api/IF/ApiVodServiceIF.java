package com.kdy.app.service.api.IF;

import com.kdy.app.dto.api.request.RequestVodListDTO;
import com.kdy.app.dto.api.response.ResponseVodListDTO;
import com.kdy.app.dto.vod.VodVO;

public interface ApiVodServiceIF {
	
	public ResponseVodListDTO getVodList(RequestVodListDTO requestDTO) throws Exception;
	
	public VodVO getVodDetail(String vodSeq) throws Exception;

	public Object getWatermark() throws Exception;

}
