package com.kdy.app.bean;

import org.springframework.stereotype.Component;

import com.kdy.app.bean.dao.EncodingSettingDAO;
import com.kdy.app.dto.watermark.WatermarkVO;
import com.kdy.live.dto.system.SystemConfigVO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EncodingSettingBean {

	private final EncodingSettingDAO dao;
	
	//워터마크 불러오기
	public WatermarkVO getWatermark(WatermarkVO vo) throws Exception {
		return dao.viewWatermark(vo);
	}
	
	//워터마크 등록
	public String registerWatermark(WatermarkVO vo) throws Exception {
		if(dao.insertWatermark(vo) > 0) {
			return "SUCCESS";
		} else {
			return "FAIL";
		}
	}
	
	//워터마크 수정
	public String modifyWatermark(WatermarkVO vo) throws Exception {
		if(dao.updateWatermark(vo) > 0) {
			return "SUCCESS";
		} else {
			return "FAIL";
		}
	}
	
	//워터마크 수정 > 기존 파일 삭제
	public WatermarkVO getWatermarkFile() throws Exception {
		return dao.selectWatermark();
	}
	
	//워터마크 수정 > 사용여부
	public String modifyStatus(WatermarkVO vo) throws Exception {
		if(dao.updateStatus(vo) > 0) {
			return "SUCCESS";
		} else {
			return "FAIL";
		}
	}
	
	// 미디어 설정 불러오기
	public SystemConfigVO getSystemConfig(SystemConfigVO vo) throws Exception {
		return dao.selectMediaConfig(vo);
	}
	
	//워터마크 수정
	public String modifyMediaConfig(SystemConfigVO vo) throws Exception {
		if (dao.updateMediaConfig(vo) > 0) {
			return "SUCCESS";
		} else {
			return "FAIL";
		}
	}
}
