package com.kdy.app.service.IF;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import com.kdy.app.dto.live.AppBroadcastListDTO;
import com.kdy.app.dto.live.AppBroadcastVO;
import com.kdy.app.dto.live.CategoryVO;
import com.kdy.app.dto.live.ResultVO;
import com.kdy.app.dto.watermark.WatermarkVO;
import com.kdy.live.dto.live.ChatDTO;

public interface LiveServiceIF {
	public AppBroadcastListDTO getOnAirList(AppBroadcastListDTO dto) throws Exception;
	
	public AppBroadcastListDTO getWaitList(AppBroadcastListDTO dto) throws Exception;
	
	public AppBroadcastListDTO getFinishList(AppBroadcastListDTO dto) throws Exception;
	
	public AppBroadcastListDTO getOnAirStatusList(AppBroadcastListDTO dto) throws Exception;
	
	public AppBroadcastVO getBroadcastDetail(AppBroadcastVO vo) throws Exception;
	
	public ResultVO registBroadcast(AppBroadcastVO lbvo, MultipartFile attachFile, MultipartFile thumbnailFile) throws Exception;
	
	public void attachFileDownload(AppBroadcastVO vo, HttpServletResponse res) throws Exception;

	public String removeBroadcast(AppBroadcastListDTO dto) throws Exception;

	public String removeBroadcastOne(AppBroadcastListDTO dto) throws Exception;
	
	public AppBroadcastVO getBroadcast(String lbSeq) throws Exception;

	public ResultVO modifyBroadcast(AppBroadcastVO lbvo, MultipartFile attachFile, MultipartFile thumbnailFile) throws Exception;

	public List<CategoryVO> getCategoryList() throws Exception;
	
	public String getCategoryTreeHtml(List<CategoryVO> categoryList) throws Exception;
	
	public AppBroadcastVO getLiveVideoData(AppBroadcastVO vo) throws Exception;
	
	//라이브 종료
	public String liveBroadcastStatusEnd(String lbSeq) throws Exception;
	
	//워터마크 설정
	public WatermarkVO getWatermark(WatermarkVO watermarkVo) throws Exception;
	
	//라이브 채팅 다운로드
	public AppBroadcastVO getLiveInfo(String lbSeq) throws Exception;
	
	//라이브 일시중지 및 재시작
	public String livePauseAndStart(AppBroadcastVO vo) throws Exception;
	
	//라이브 상태 확인
	public String liveStatusCheck(String lbSeq) throws Exception;

	public List<AppBroadcastVO> getFinishListExcel(AppBroadcastListDTO dto) throws Exception;

	public List<AppBroadcastVO> getWaitListExcel(AppBroadcastListDTO dto) throws Exception;

	public List<AppBroadcastVO> getOnAirListExcel(AppBroadcastListDTO dto) throws Exception;
	
	public List<ChatDTO> getChattingListExcel(AppBroadcastListDTO dto) throws Exception;
	
	//라이브 종료 (스트리밍 오류)
	public String liveErrorStatusEnd(String lbSeq) throws Exception;
	//라이브 재시작요청 (스트리밍 오류)
	public String liveErrorStatusRestart(String lbSeq) throws Exception;
	
	//동적 녹화 상태 조회
	public String getDynamicRecordEnable(String lbSeq) throws Exception;
	//동적 녹화 상태 변경
	public String updateDynamicRecordEnable(String lbSeq) throws Exception;
	
	public String checkChatFile(String lbSeq) throws Exception;

	public List<ChatDTO> getChatHistory(ChatDTO dto) throws Exception;

}
