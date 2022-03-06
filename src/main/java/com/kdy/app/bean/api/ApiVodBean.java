package com.kdy.app.bean.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kdy.app.bean.dao.api.ApiVodDAO;
import com.kdy.app.dto.api.request.RequestVodListDTO;
import com.kdy.app.dto.vod.VodModifyDTO;
import com.kdy.app.dto.vod.VodVO;
import com.kdy.app.dto.watermark.WatermarkVO;

@Component
public class ApiVodBean {

	@Autowired
	private ApiVodDAO dao;
	
	public List<VodVO> getVodList(RequestVodListDTO requestDTO) throws Exception {
		return dao.getVodList(requestDTO);
	}
	
	public int getVodListCount(RequestVodListDTO requestDTO) throws Exception {
		return dao.getVodListCount(requestDTO);
	}
	
	public VodVO getVodDetail(VodModifyDTO dto) throws Exception{
		return dao.getVodDetail(dto);
	}

	public WatermarkVO getWatermark(WatermarkVO watermarkVo) throws Exception {
		return dao.selectWatermark(watermarkVo);
	}
	
}
