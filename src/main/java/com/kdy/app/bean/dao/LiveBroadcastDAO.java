package com.kdy.app.bean.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kdy.app.dto.live.AppBroadcastListDTO;
import com.kdy.app.dto.live.AppBroadcastStatusVO;
import com.kdy.app.dto.live.AppBroadcastVO;
import com.kdy.app.dto.live.CategoryVO;
import com.kdy.app.dto.watermark.WatermarkVO;
import com.kdy.live.dto.live.ChatDTO;

@Repository
public class LiveBroadcastDAO {
	
	@Autowired
	SqlSessionTemplate sqlSessionTemplate;
	
	//방송중 목록
	public List<AppBroadcastVO> selectOnAirList(AppBroadcastListDTO dto) throws Exception{
		return sqlSessionTemplate.selectList("app_live.onAirList", dto); 
	}
	
	//방송중 목록 갯수
	public int selectOnAirListCount(AppBroadcastListDTO dto) throws Exception{
		return sqlSessionTemplate.selectOne("app_live.onAirListCount", dto); 
	}
	
	//방송 대기 - 목록
	public List<AppBroadcastVO> selectWaitList(AppBroadcastListDTO dto) throws Exception{
		return sqlSessionTemplate.selectList("app_live.waitList", dto); 
	}
	
	//방송 대기 - 목록 갯수 가져오기
	public int selectWaitListCount(AppBroadcastListDTO dto) throws Exception{
		return sqlSessionTemplate.selectOne("app_live.waitListCount", dto); 
	}
	
	//방송 대기 - 삭제(tglive_broadcast)
	public int updateLbDelYn(String lbSeq) throws Exception{
		return sqlSessionTemplate.delete("app_live.updateLbDelYn", lbSeq);
	}
	
	//방송 대기 - 삭제(tglive_broadcast_job)
	public int deleteLbj(String lbSeq) throws Exception{
		return sqlSessionTemplate.delete("app_live.deleteLbj", lbSeq);
	}
	
	public String selectLpStatus(String lbSeq) throws Exception{
		return sqlSessionTemplate.selectOne("app_live.getLpStatus", lbSeq);
	}
	
	//방송 완료 목록 
	public List<AppBroadcastVO> selectFinishList(AppBroadcastListDTO dto) throws Exception{
		return sqlSessionTemplate.selectList("app_live.finishList", dto); 
	}
	
	//방송 완료 목록 갯수 가져오기
	public int selectFinishListCount(AppBroadcastListDTO dto) throws Exception{
		return sqlSessionTemplate.selectOne("app_live.finishListCount", dto); 
	}
	
	//방송중 페이지 상태 리스트
	public List<AppBroadcastStatusVO> selectOnAirStatusList(AppBroadcastListDTO dto) throws Exception{
		return sqlSessionTemplate.selectList("app_live.onAirStatusList", dto); 
	}
	
	public AppBroadcastVO selectBroadcastDetail(AppBroadcastVO vo) throws Exception {
		return sqlSessionTemplate.selectOne("app_live.broadcastDetail", vo);
	}
	
	public int insertBroadcast(AppBroadcastVO lbvo) throws Exception {
		return sqlSessionTemplate.insert("app_live.insertBroadcast", lbvo);
	}
	
	public int insertBroadcastJob(AppBroadcastVO lbvo) throws Exception {
		return sqlSessionTemplate.insert("app_live.insertBroadcastJob", lbvo);
	}

	public AppBroadcastVO selectBroadcast(String lbSeq) throws Exception {
		return sqlSessionTemplate.selectOne("app_live.getBroadcast", lbSeq);
	}

	public int updateBroadcast(AppBroadcastVO lbvo) throws Exception {
		return sqlSessionTemplate.insert("app_live.updateBroadcast", lbvo);
	}

	public int updateBroadcastFile(AppBroadcastVO lbvo) {
		return sqlSessionTemplate.insert("app_live.updateBroadcastFile", lbvo);
	}

	public int updateAttachFile(AppBroadcastVO lbvo) throws Exception {
		return sqlSessionTemplate.insert("app_live.updateAttachFile", lbvo);
	}

	public int updateThumbnail(AppBroadcastVO lbvo) {
		return sqlSessionTemplate.insert("app_live.updateThumbnail", lbvo);
	}

	//썸네일 정보
	public AppBroadcastVO selectFile(String lbSeq) throws Exception{
		return sqlSessionTemplate.selectOne("app_live.selectFile", lbSeq);
	}

	public List<CategoryVO> selectCategoryList(int rootUpCategoryCode) throws Exception {
		return sqlSessionTemplate.selectList("app_live.selectCategoryList", rootUpCategoryCode);
	}
	
	//라이브 종료
	public int liveBroadcastStatusEnd(String lbSeq) throws Exception{
		return sqlSessionTemplate.update("app_live.liveBroadcastStatusEnd", lbSeq);
	}

	//워터마크 설정
	public WatermarkVO selectWatermark(WatermarkVO watermarkVo) throws Exception {
		return sqlSessionTemplate.selectOne("app_live.selectWatermark", watermarkVo);
	}

	//라이브 채팅 다운로드 관련
	public AppBroadcastVO getLiveInfo(String lbSeq) throws Exception {
		return sqlSessionTemplate.selectOne("app_live.getLiveInfo", lbSeq);
	}

	public boolean checkEncoderWorkerCnt(AppBroadcastVO lbvo) throws Exception{
		int rslt = sqlSessionTemplate.selectOne("app_live.checkEncoderWorkerCnt", lbvo);
		return rslt == 1;
	}

	//라이브 일시중지 및 재시작
	public int livePauseAndStart(AppBroadcastVO vo) throws Exception {
		return sqlSessionTemplate.update("app_live.livePauseAndStart", vo);
	}
	
	//라이브, 재시작 >> 일시중지 시 Date Update
	public int pauseDateUpdate(AppBroadcastVO vo) throws Exception {
		return sqlSessionTemplate.update("app_live.pauseDateUpdate", vo);
	}
	
	//LIVE 상태 확인 (1:진행중/2:종료/3:일시정지)
	public String liveStatusCheck(String lbSeq) throws Exception{
		return sqlSessionTemplate.selectOne("app_live.liveStatusCheck", lbSeq);
	}

	public List<AppBroadcastVO> getFinishListExcel(AppBroadcastListDTO dto) throws Exception {
		return sqlSessionTemplate.selectList("app_live.finishListExcel", dto);
	}

	public List<AppBroadcastVO> getWaitListExcel(AppBroadcastListDTO dto) throws Exception{
		return sqlSessionTemplate.selectList("app_live.waitListExcel", dto);
	}

	public List<AppBroadcastVO> getOnAirListExcel(AppBroadcastListDTO dto) throws Exception {
		return sqlSessionTemplate.selectList("app_live.onAirListExcel", dto);
	}
	
	//라이브 종료 (스트리밍 오류)
	public int liveErrorStatusEnd(String lbSeq) throws Exception{
		return sqlSessionTemplate.update("app_live.liveErrorStatusEnd", lbSeq);
	}
	
	//라이브 재시작 (스트리밍 오류)
	public int liveErrorStatusRestart(String lbSeq) throws Exception{
		return sqlSessionTemplate.update("app_live.liveErrorStatusRestart", lbSeq);
	}
	
	public int insertLiveChatting(List<ChatDTO> list) throws Exception {
		int rslt = sqlSessionTemplate.insert("app_live.insertLiveChatting", list);
		return rslt;
	}
	
	public List<ChatDTO> getChattingListExcel(AppBroadcastListDTO dto) throws Exception {
		return sqlSessionTemplate.selectList("app_live.chattingListExcel", dto);
	}
	
	public int getChattingListCountBySeq(String lbSeq) throws Exception {
		return sqlSessionTemplate.selectOne("app_live.chattingListCountBySeq", lbSeq);
	}

	public List<ChatDTO> getChatHistory(ChatDTO dto) {
		return sqlSessionTemplate.selectList("app_live.getChatHistory", dto);
	}
}
