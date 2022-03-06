package com.kdy.app.service.IF;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import com.kdy.app.dto.live.ResultVO;
import com.kdy.app.dto.vod.LiveContentsVO;
import com.kdy.app.dto.vod.LiveVodDTO;
import com.kdy.app.dto.vod.VodListDTO;
import com.kdy.app.dto.vod.VodModifyDTO;
import com.kdy.app.dto.vod.VodVO;
import com.kdy.app.dto.vod.VodWriteDTO;

public interface VodServiceIF {

	public LiveVodDTO liveVodList(LiveVodDTO dto) throws Exception;

	public List<LiveContentsVO> getContentsListByCateSeq(String lbCategorySeq) throws Exception;
	
	//vod 관리
	public VodListDTO vodList(VodListDTO dto) throws Exception;
	
	public VodWriteDTO vodWriteForm() throws Exception;

	//추가
	public ResultVO vodWrite(VodWriteDTO dto, MultipartFile thumbnailFile) throws Exception; 
	
	public VodVO vodDetail(String vodSeq) throws Exception;
	
	//수정
	public String vodModify(VodVO vo) throws Exception;
	
	//vod 삭제
	public String vodDelete(VodListDTO dto) throws Exception;
	
	//엑셀다운
	public void vodExcelDown(VodListDTO dto, HttpServletRequest req, HttpServletResponse res) throws Exception;
	
	//VOD 영상 다운로드
	public void vodVideoDown(String vodSeq, HttpServletRequest req, HttpServletResponse res) throws Exception;
	
	public String vodEncodingProgress(String videoSeq) throws Exception;
	
	public VodModifyDTO thumbList(VodModifyDTO dto) throws Exception;
	
	public String thumbImgSave(VodVO vo) throws Exception;
	
	public String thumbDelete(String thumbSeq) throws Exception;
	
	public ResultVO thumbAddInsert(VodVO vo, MultipartFile thumbnailFile) throws Exception;
}

