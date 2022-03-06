package com.kdy.app.service.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.kdy.app.bean.api.ApiVodBean;
import com.kdy.app.dto.api.request.RequestVodListDTO;
import com.kdy.app.dto.api.response.ResponseVodListDTO;
import com.kdy.app.dto.vod.VodModifyDTO;
import com.kdy.app.dto.vod.VodVO;
import com.kdy.app.dto.watermark.WatermarkVO;
import com.kdy.app.service.api.IF.ApiVodServiceIF;
import com.kdy.live.dto.LiveSchedMemoryVO;

@Service
public class ApiVodService implements ApiVodServiceIF {
	
	private final ApiVodBean vodBean;
	private final LiveSchedMemoryVO memoryVO;
	
	@Autowired
	public ApiVodService(ApiVodBean vodBean
					, LiveSchedMemoryVO memoryVO) {
		this.vodBean = vodBean;
		this.memoryVO = memoryVO;
	}

	@Value("${encoding.isAdaptive}")
	private boolean isAdaptive;
	
	@Value("${encoding.type}")
	private String adaptiveType;
	
	@Override
	public ResponseVodListDTO getVodList(RequestVodListDTO requestDTO) throws Exception {
		ResponseVodListDTO dto = new ResponseVodListDTO();
		
		requestDTO.setStartNo(requestDTO.getBlockCount() * (requestDTO.getCurrentPage() -1));
		
		dto.setVodList(vodBean.getVodList(requestDTO));
		dto.setTatalCount(vodBean.getVodListCount(requestDTO));
		
		return dto;
	}

	@Override
	public VodVO getVodDetail(String vodSeq) throws Exception {
		
		VodModifyDTO dto = new VodModifyDTO();
		dto.setVodSeq(vodSeq);
		dto.setReplaceRootPath(memoryVO.getReplaceRootPath());
		
		VodVO vo = vodBean.getVodDetail(dto);
		vo.setVodStreamUrl(memoryVO.getVodStreamingUri());
		
		//적응형 여부
		vo.setAdaptiveYn(isAdaptive?"Y":"N");
		vo.setAdaptiveType(adaptiveType);
		
		return vo;
	}

	@Override
	public WatermarkVO getWatermark() throws Exception {
		WatermarkVO watermarkVo = new WatermarkVO();
		watermarkVo.setReplaceRootPath(memoryVO.getReplaceRootPath());
		return vodBean.getWatermark(watermarkVo);
	}

}
