package com.kdy.app.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.kdy.app.dto.live.ResultVO;
import com.kdy.app.dto.login.LoginVO;
import com.kdy.app.dto.vod.VodListDTO;
import com.kdy.app.dto.vod.VodModifyDTO;
import com.kdy.app.dto.vod.VodVO;
import com.kdy.app.dto.vod.VodWriteDTO;
import com.kdy.app.dto.watermark.WatermarkVO;
import com.kdy.app.service.IF.LiveServiceIF;
import com.kdy.app.service.IF.VodServiceIF;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/vod")
@RequiredArgsConstructor
public class VodController extends ExceptionController{
	
	private final VodServiceIF vodService;
	private final LiveServiceIF liveService;
	
	@RequestMapping("/vodList.do")
	public String vodList(VodListDTO dto, Model model, Authentication auth) throws Exception{
		LoginVO vo = (LoginVO)auth.getPrincipal();
		dto.setAuthSeq(vo.getAuthSeq());
		dto.setCategorySeq(vo.getCategorySeq());
		model.addAttribute("dto", vodService.vodList(dto)); 
		
		return "vod/encoding-list";
	}
	
	//등록 화면
	@RequestMapping("/vodWriteForm.do")
	public String vodWriteForm(Model model, Authentication auth) throws Exception{
		LoginVO vo = (LoginVO)auth.getPrincipal();
		model.addAttribute("loginVO", vo);
		model.addAttribute("dto", vodService.vodWriteForm());
//		model.addAttribute("categoryHtml", liveService.getCategoryTreeHtml(liveService.getCategoryList()));
		model.addAttribute("categoryList", liveService.getCategoryList());
		return "vod/encoding-write";
	}
	
	//등록하기
	@ResponseBody
	@RequestMapping("/vodWrite.do")
	public ResultVO vodWrite(VodWriteDTO dto, Authentication auth, @RequestPart("thumbnail") MultipartFile thumbnailFile) throws Exception{
		LoginVO loginVo = (LoginVO)auth.getPrincipal();
		dto.setRegUserId(loginVo.getUserId());
		return vodService.vodWrite(dto, thumbnailFile);
	}
	
	//수정 화면
	@RequestMapping("/vodModifyForm.do")
	public String vodModifyForm(String vodSeq, Model model, Authentication auth, WatermarkVO watermarkVo) throws Exception{
		LoginVO vo = (LoginVO)auth.getPrincipal();
		model.addAttribute("loginVO", vo);
		model.addAttribute("vo", vodService.vodDetail(vodSeq));
		//model.addAttribute("categoryHtml", liveService.getCategoryTreeHtml(liveService.getCategoryList()));
		model.addAttribute("categoryList", liveService.getCategoryList());
		VodModifyDTO dto = new VodModifyDTO();
		dto.setVodSeq(vodSeq);
		model.addAttribute("dto", vodService.thumbList(dto));
//		model.addAttribute("thumbList", vodService.vodThumbList(dto));
		//워터마크 설정
		model.addAttribute("watermarkVo", liveService.getWatermark(watermarkVo));
		return "vod/encoding-modify";
	}
	
	//수정하기
	@ResponseBody
	@RequestMapping("/vodModify.do")
	public String vodModify(VodVO vo, Authentication auth) throws Exception{
		LoginVO loginVo = (LoginVO)auth.getPrincipal();
		vo.setModUserId(loginVo.getUserId());
		return vodService.vodModify(vo);
	}
	
	//삭제하기
	@ResponseBody
	@RequestMapping("/vodDelete.do")
	public String vodDelete(VodListDTO dto, Authentication auth) throws Exception{
		LoginVO loginVo = (LoginVO)auth.getPrincipal();
		dto.setUserId(loginVo.getUserId());
		return vodService.vodDelete(dto);
	}
	
	//VOD 목록 리스트 엑셀 다운로드
	@RequestMapping("/vodExcelDown.do")
	public void vodExcelDown(VodListDTO dto, HttpServletRequest req, HttpServletResponse res) throws Exception{
		vodService.vodExcelDown(dto, req, res);
	}
	
	//VOD 파일 다운로드
	@RequestMapping("/vodVideoDown.do")
	public void vodVideoDown(String vodSeq, HttpServletRequest req, HttpServletResponse res) throws Exception{
		vodService.vodVideoDown(vodSeq, req, res);
	}
	
	@ResponseBody
	@RequestMapping("/encodingProgressUpdate.do")
	public String lectureVideoEncodingProgressUpdate(String videoSeq) throws Exception{
		return vodService.vodEncodingProgress(videoSeq);
	}
	
	@ResponseBody
	@RequestMapping(value="/thumbImgModify.do")
	public String thumbImageModify(VodVO vo, Authentication auth) throws Exception {
		LoginVO loginVo = (LoginVO)auth.getPrincipal();
		vo.setModUserId(loginVo.getUserId());
		return vodService.thumbImgSave(vo);
	}
	
	@ResponseBody
	@RequestMapping(value="/thumbImgDelete.do")
	public String thumbImageDelete(String thumbSeq) throws Exception {
		return vodService.thumbDelete(thumbSeq);
	}
	
	@ResponseBody
	@RequestMapping("/thumbAddInsert.do")
	public ResultVO thumbAddInsert(VodVO vo, Authentication auth, @RequestPart("imageFile") MultipartFile thumbnailFile) throws Exception{
		LoginVO loginVo = (LoginVO)auth.getPrincipal();
		vo.setRegUserId(loginVo.getUserId());
		return vodService.thumbAddInsert(vo, thumbnailFile);
	}
}
