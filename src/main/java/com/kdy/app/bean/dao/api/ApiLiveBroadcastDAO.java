package com.kdy.app.bean.dao.api;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kdy.app.dto.api.request.RequestBroadcastExcelDTO;
import com.kdy.app.dto.api.request.RequestBroadcastInsertDTO;
import com.kdy.app.dto.api.request.RequestBroadcastListDTO;
import com.kdy.app.dto.api.request.RequestBroadcastUpdateDTO;
import com.kdy.app.dto.api.request.RequestUserBroadcastListDTO;
import com.kdy.app.dto.api.response.ResponseBroadcastDetailDTO;
import com.kdy.app.dto.live.AppBroadcastStatusVO;
import com.kdy.app.dto.live.AppBroadcastVO;

@Repository
public class ApiLiveBroadcastDAO {
	
	@Autowired
	SqlSessionTemplate sqlSessionTemplate;
	
	//API 방송중 목록
	public List<AppBroadcastVO> selectBroadcastList(RequestBroadcastListDTO dto) throws Exception{
		return sqlSessionTemplate.selectList("api.broadcastList", dto); 
	}
	
	//API 방송중 목록 갯수
	public int selectBroadcastListCount(RequestBroadcastListDTO dto) throws Exception{
		return sqlSessionTemplate.selectOne("api.broadcastListCount", dto); 
	}
	
	//API 방송 상세 
	public ResponseBroadcastDetailDTO selectBroadcastDetail(AppBroadcastVO vo) throws Exception {
		return sqlSessionTemplate.selectOne("api.broadcastDetailAPI", vo);
	}
	
	//API 방송 목록 엑셀다운로드
	public List<AppBroadcastVO> selectBroadcastListExcel(RequestBroadcastExcelDTO requestDTO) throws Exception{
		return sqlSessionTemplate.selectList("api.broadcastExcelList", requestDTO); 
	}
	
	//API 방송 등록
	public int insertBroadcast(RequestBroadcastInsertDTO requestDTO) throws Exception {
		return sqlSessionTemplate.insert("api.insertBroadcastAPI", requestDTO);
	}
	
	//API 방송 JOB 등록
	public int insertBroadcastJob(RequestBroadcastInsertDTO lbvo) throws Exception {
		return sqlSessionTemplate.insert("api.insertBroadcastJobAPI", lbvo);
	}

	public String selectLbStatus(String id) throws Exception {
		return sqlSessionTemplate.selectOne("api.selectLbStatus", id);
	}

	public int updateLbDelYn(String id) throws Exception {
		return sqlSessionTemplate.update("api.updateLbDelYn", id);
	}

	public int deleteLbj(String id) throws Exception {
		return sqlSessionTemplate.delete("api.deleteLbj", id);
	}

	public int updateLb(RequestBroadcastUpdateDTO requestDTO) throws Exception {
		return sqlSessionTemplate.update("api.updateLb", requestDTO);
	}

	public boolean checkEncoderWorkerCnt(Object dto) throws Exception {
		int rslt = sqlSessionTemplate.selectOne("api.checkEncoderWorkerCnt", dto);
		return rslt == 1;
	}
	
	//리스트 상태값
	public List<AppBroadcastStatusVO> selectOnAirStatusList(RequestBroadcastListDTO dto) throws Exception {
		return sqlSessionTemplate.selectList("api.onAirStatusList", dto);
	}
	
	//라이브 종료
	public int liveBroadcastStatusEnd(String lbSeq) throws Exception {
		return sqlSessionTemplate.update("api.liveBroadcastStatusEnd", lbSeq);
	}
	
	//라이브 채팅 다운로드
	public AppBroadcastVO getLiveInfo(String lbSeq) throws Exception {
		return sqlSessionTemplate.selectOne("api.getLiveInfo", lbSeq);
	}
	
	//라이브 일시중지 및 재시작
	public int livePauseAndStart(AppBroadcastVO vo) throws Exception {
		return sqlSessionTemplate.update("api.livePauseAndStart", vo);
	}
	
	//라이브, 재시작 >> 일시중지 시 Date Update
	public int pauseDateUpdate(String lbSeq) throws Exception {
		return sqlSessionTemplate.update("api.pauseDateUpdate", lbSeq);
	}
	
	//사용자 라이브 리스트 가져오기
	public List<AppBroadcastVO> getLiveStatusList(RequestUserBroadcastListDTO dto) throws Exception{
		return sqlSessionTemplate.selectList("api.getLiveStatusList", dto);
	}
	
	//LIVE 상태 확인 (1:진행중/2:종료/3:일시정지)
	public String liveStatusCheck(String lbSeq) throws Exception{
		return sqlSessionTemplate.selectOne("api.liveStatusCheck", lbSeq);
	}

	//방송 목록 (excel 용)
	public List<AppBroadcastVO> getBroadcastListExcel(RequestBroadcastListDTO requestDTO) throws Exception {
		return sqlSessionTemplate.selectList("api.broadcastListExcel", requestDTO); 
	}
	
}
