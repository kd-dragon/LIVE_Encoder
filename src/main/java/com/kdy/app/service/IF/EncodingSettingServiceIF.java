package com.kdy.app.service.IF;

import org.springframework.web.multipart.MultipartFile;

import com.kdy.app.dto.watermark.WatermarkVO;
import com.kdy.live.dto.system.SystemConfigVO;

public interface EncodingSettingServiceIF {
	//워터마크 불러오기
	public WatermarkVO getWatermark(WatermarkVO vo) throws Exception;
	
	//워터마크 등록
	public String saveWatermark(WatermarkVO vo, MultipartFile watermarkFile) throws Exception;

	//워터마크 사용여부
	public String modifyStatus(WatermarkVO vo) throws Exception;
	
	//미디어 설정 불러오기
	public SystemConfigVO getMediaConfig(SystemConfigVO vo) throws Exception;
	
	//미디어 설정 업데이트
	public String modifyMediaConfig(SystemConfigVO vo) throws Exception;
}
