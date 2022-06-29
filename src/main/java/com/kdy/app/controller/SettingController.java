package com.kdy.app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.kdy.app.dto.live.CategoryDTO;
import com.kdy.app.dto.login.LoginVO;
import com.kdy.app.dto.watermark.WatermarkVO;
import com.kdy.app.service.IF.CategoryServiceIF;
import com.kdy.app.service.IF.EncodingSettingServiceIF;
import com.kdy.live.dto.system.SystemConfigVO;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/setting")
@RequiredArgsConstructor
public class SettingController extends ExceptionController{
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private final EncodingSettingServiceIF encodingSettingService;
	private final CategoryServiceIF categoryService;
	
	@RequestMapping("/transcordingSet.do")
	public String transcodingSet() throws Exception{
		
		return "setting/transcordingSet";
	}
	
	//워터마크 불러오기
	@RequestMapping(value="/watermarkSet.do")
	public String watermarkSet(WatermarkVO vo, Model model) throws Exception{
		model.addAttribute("vo", encodingSettingService.getWatermark(vo));
		return "setting/watermarkSet";
	}
	
	//워터마크 저장
	@ResponseBody
	@RequestMapping(value="/watermarkSave.do")
	public String watermarkSave(WatermarkVO vo, @RequestPart("imageFile") MultipartFile watermarkFile, Authentication auth) throws Exception {
		LoginVO loginVo = (LoginVO)auth.getPrincipal();
		vo.setLuId(loginVo.getUserId());
		return encodingSettingService.saveWatermark(vo, watermarkFile);
	}
	
	//워터마크 수정 > 사용여부
	@ResponseBody
	@RequestMapping(value="/statusUpdate.do")
	public String statusUpdate(WatermarkVO vo, Authentication auth) throws Exception {
		LoginVO loginVo = (LoginVO)auth.getPrincipal();
		vo.setLuId(loginVo.getUserId());
		return encodingSettingService.modifyStatus(vo);
	}
	
	//미디어 설정
	@RequestMapping(value = "/mediaSet.do")
	public String mediaSet(SystemConfigVO vo, Model model) throws Exception {
		model.addAttribute("vo", encodingSettingService.getMediaConfig(vo));
		return "setting/mediaSet";
	}
	
	//미디어 설정 업데이트
	@ResponseBody
	@RequestMapping(value = "/mediaConfigUpdate.do")
	public String mediaConfigUpdate(SystemConfigVO vo) throws Exception {
		return encodingSettingService.modifyMediaConfig(vo);
	}
	
	//카테고리 관리
	@RequestMapping(value = "/categorySet.do")
	public String categorySet(Model model) throws Exception {
		model.addAttribute("categoryHtml", categoryService.getCategoryTreeHtml(categoryService.getCategoryList()));
		model.addAttribute("detailCategoryHtml", categoryService.getDetailCategoryTreeHtml(categoryService.getCategoryList()));
		return "setting/categorySet";
	}
	
	//카테고리 등록
	@ResponseBody
	@RequestMapping(value = "/insertCategory.do")
	public String insertCategory(CategoryDTO dto) throws Exception {
		return categoryService.insertCategory(dto);
	}
	
	//카테고리 정보 수정
	@ResponseBody
	@RequestMapping(value = "/updateCategory.do")
	public String updateCategory(CategoryDTO dto) throws Exception {
		return categoryService.updateCategory(dto);
	}
	
	//카테고리 정보 수정
	@ResponseBody
	@RequestMapping(value = "/deleteCategory.do")
	public String deleteCategory(CategoryDTO dto) throws Exception {
		return categoryService.deleteCategory(dto);
	}

}
