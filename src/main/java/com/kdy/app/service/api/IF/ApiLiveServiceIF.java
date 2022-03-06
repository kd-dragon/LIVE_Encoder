package com.kdy.app.service.api.IF;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.kdy.app.dto.api.request.RequestBroadcastExcelDTO;
import com.kdy.app.dto.api.request.RequestBroadcastInsertDTO;
import com.kdy.app.dto.api.request.RequestBroadcastListDTO;
import com.kdy.app.dto.api.request.RequestBroadcastUpdateDTO;
import com.kdy.app.dto.api.request.RequestUserBroadcastListDTO;
import com.kdy.app.dto.api.response.ResponseBroadcastDetailDTO;
import com.kdy.app.dto.api.response.ResponseBroadcastListDTO;
import com.kdy.app.dto.api.response.ResponseBroadcastListExcelDTO;
import com.kdy.app.dto.api.response.ResponseUserBroadcastListDTO;
import com.kdy.app.dto.live.AppBroadcastVO;
import com.kdy.app.dto.live.ResultVO;

public interface ApiLiveServiceIF {
	public ResponseBroadcastListDTO getBroadcasts(RequestBroadcastListDTO requestDTO) throws Exception;
	
	public List<AppBroadcastVO> getBroadcastsExcel(RequestBroadcastExcelDTO requestDTO) throws Exception;
	
	public ResultVO addBroadcasts(RequestBroadcastInsertDTO requestDTO	, MultipartFile attachFile, MultipartFile thumbnailFile) throws Exception;
	
	public ResponseBroadcastDetailDTO getBroadcast(String seq) throws Exception;

	public ResultVO deleteBroadcast(List<String> seqs) throws Exception;

	public ResultVO updateBroadcast(RequestBroadcastUpdateDTO requestDTO, MultipartFile attachFile, MultipartFile thumbnailFile) throws Exception;

	public AppBroadcastVO getLiveVideoData(AppBroadcastVO vo) throws Exception;
	
	//라이브 종료
	public ResultVO liveBroadcastStatusEnd(String lbSeq) throws Exception;
	
	//라이브 채팅 다운로드
	public AppBroadcastVO getLiveInfo(String lbSeq) throws Exception;
	
	//라이브 일시중지 및 재시작
	public ResultVO livePauseAndStart(AppBroadcastVO vo) throws Exception;
	
	//User live Broadcast List
	public ResponseUserBroadcastListDTO getUserBroadcasts(RequestUserBroadcastListDTO requestDTO) throws Exception;
	
	//라이브 종료 여부 체크
	public String liveStatusCheck(String lbSeq) throws Exception;

	public ResponseBroadcastListExcelDTO getBroadcastListExcel(RequestBroadcastListDTO requestDTO) throws Exception;
		
}
