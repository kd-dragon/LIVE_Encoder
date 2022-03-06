package com.kdy.app.bean.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kdy.app.dto.watermark.WatermarkVO;
import com.kdy.live.dto.system.SystemConfigVO;

@Repository
public class EncodingSettingDAO {

	@Autowired
	SqlSessionTemplate sqlSessionTemplate;

	//워터마크 불러오기
	public WatermarkVO viewWatermark(WatermarkVO vo) throws Exception {
		return sqlSessionTemplate.selectOne("app_encodingSetting.watermarkView", vo);
	}
	
	//워터마크 등록
	public int insertWatermark(WatermarkVO vo) throws Exception {
		return sqlSessionTemplate.insert("app_encodingSetting.insertWatermark", vo);
	}
	
	//워터마크 수정
	public int updateWatermark(WatermarkVO vo) throws Exception {
		return sqlSessionTemplate.update("app_encodingSetting.updateWatermark", vo);
	}
	
	//워터마크 수정 > 기존 파일 삭제
	public WatermarkVO selectWatermark() throws Exception {
		return sqlSessionTemplate.selectOne("app_encodingSetting.selectWatermark");
	}
	
	//워터마크 수정 > 사용여부
	public int updateStatus(WatermarkVO vo) throws Exception {
		return sqlSessionTemplate.update("app_encodingSetting.updateStatus", vo);
	}
	
	//미디어 설정 불러오기
	public SystemConfigVO selectMediaConfig(SystemConfigVO vo) throws Exception {
		return sqlSessionTemplate.selectOne("app_encodingSetting.selectMediaConfig", vo);
	}
	
	//미디어 설정 수정
	public int updateMediaConfig(SystemConfigVO vo) throws Exception {
		return sqlSessionTemplate.update("app_encodingSetting.updateMediaConfig", vo);
	}
}
