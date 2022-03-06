package com.kdy.app.bean.dao.api;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kdy.app.dto.api.request.RequestVodListDTO;
import com.kdy.app.dto.vod.VodModifyDTO;
import com.kdy.app.dto.vod.VodVO;
import com.kdy.app.dto.watermark.WatermarkVO;

@Repository
public class ApiVodDAO {

	@Autowired
	SqlSessionTemplate sqlSessionTemplate;
	
	public List<VodVO> getVodList(RequestVodListDTO requestDTO) throws Exception{
		return sqlSessionTemplate.selectList("api_vod.vodList", requestDTO);
	}
	
	public int getVodListCount(RequestVodListDTO requestDTO) throws Exception{
		return sqlSessionTemplate.selectOne("api_vod.vodListCount", requestDTO);
	}
	
	public VodVO getVodDetail(VodModifyDTO dto) {
		return sqlSessionTemplate.selectOne("app_vod.vodDetail", dto);
	}

	public WatermarkVO selectWatermark(WatermarkVO watermarkVo) throws Exception{
		return sqlSessionTemplate.selectOne("app_vod.selectWatermark", watermarkVo);
	}
	
}
