package com.kdy.app.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.kdy.app.dto.login.LoginVO;
import com.kdy.app.dto.userHome.BannerDTO;
import com.kdy.app.dto.userHome.BannerVO;
import com.kdy.app.dto.userHome.NoticeDeleteDTO;
import com.kdy.app.dto.userHome.NoticeDetailDTO;
import com.kdy.app.dto.userHome.NoticeFileVO;
import com.kdy.app.dto.userHome.NoticeListDTO;
import com.kdy.app.dto.userHome.NoticeWriteDTO;
import com.kdy.app.service.IF.BannerServiceIF;
import com.kdy.app.service.IF.NoticeServiceIF;


@Controller
@RequestMapping("/userHome")
public class UserHomeController {
	
	private final Logger logger = LoggerFactory.getLogger(UserHomeController.class);
	
	private BannerServiceIF bannerService;
	
	private NoticeServiceIF noticeService;
	
	@Autowired
	public UserHomeController(BannerServiceIF bannerService
							, NoticeServiceIF noticeService) {
		this.bannerService = bannerService;
		this.noticeService = noticeService;
	}
	
	@RequestMapping(value="/mainBanner.do")
	public String mainBanner(Model model) throws Exception {
		List<BannerVO> result = bannerService.getBannerList();
		
		BannerVO banner1 = null;
		BannerVO banner2 = null;
		BannerVO banner3 = null;
		BannerVO banner4 = null;
		BannerVO banner5 = null;
		
		for(BannerVO vo : result) {
			if(vo.getBannerOrder().equals("1")) {
				banner1 = vo;
			} else if(vo.getBannerOrder().equals("2")) {
				banner2 = vo;
			} else if(vo.getBannerOrder().equals("3")) {
				banner3 = vo;
			} else if(vo.getBannerOrder().equals("4")) {
				banner4 = vo;
			} else if(vo.getBannerOrder().equals("5")) {
				banner5 = vo;
			}
		}
		model.addAttribute("banner1", banner1);
		model.addAttribute("banner2", banner2);
		model.addAttribute("banner3", banner3);
		model.addAttribute("banner4", banner4);
		model.addAttribute("banner5", banner5);

		return "userHome/main-banner";
	}
	
	@RequestMapping("/updateBannerImgFile.do")
	@ResponseBody
	public Map<String , Object> updateBannerImgFile(BannerDTO dto) throws Exception {
		return bannerService.uploadBannerFile(dto);
	}
	
	@RequestMapping("/modifyMainBanner.do")
	@ResponseBody
	public String modifyMainBanner(@RequestBody BannerDTO dto, Authentication auth) throws Exception {
		LoginVO loginVo = (LoginVO)auth.getPrincipal();
		dto.setUserId(loginVo.getUserId());
		
		String rtnMsg = "SUCCESS";
		if(!bannerService.modifyBanner(dto)) {
			rtnMsg = "FAIL";
		}
		return rtnMsg;
	}
	
	@RequestMapping("/noticeList.do")
	public String noticeList(NoticeListDTO dto, Model model, HttpServletRequest request) throws Exception{
		String userId = (String)request.getSession().getAttribute("userId");
		dto.setUserId(userId);
		
		model.addAttribute("dto", noticeService.getNoticeList(dto));
		
		return "userHome/notice-list";
	}
	
	@RequestMapping("/noticeWriteForm.do")
	public String noticeWrite(HttpServletRequest request, Model model) throws Exception{
		String userId = (String)request.getSession().getAttribute("userId");
		model.addAttribute("userId", userId);
		
		return "userHome/notice-write-form";
	}
	
	@ResponseBody
	@RequestMapping("/noticeWrite.do")
	public String noticeWrite(NoticeWriteDTO dto) throws Exception{
		return noticeService.noticeWrite(dto);
	}
	
	@RequestMapping("/noticeDetail.do")
	public String noticeDetail(NoticeDetailDTO dto, Model model) throws Exception{
		model.addAttribute("dto", noticeService.noticeDetail(dto));
		return "userHome/notice-view";
	}
	
	@RequestMapping("/noticeModifyForm.do")
	public String noticeModifyForm(NoticeDetailDTO dto, Model model) throws Exception{
		model.addAttribute("dto", noticeService.noticeDetail(dto));
		return "userHome/notice-modify";
	}
	@ResponseBody
	@RequestMapping("/noticeModify.do")
	public String noticeModify(NoticeWriteDTO dto, HttpServletRequest request, Model model) throws Exception{
		String userId = (String)request.getSession().getAttribute("userId");
		dto.setUserId(userId);
		
		return noticeService.noticeModify(dto);
	}
	
	@RequestMapping(value="/noticeFileDown.do")
	public void noticeFileDown(NoticeFileVO fileVo, HttpServletRequest req, HttpServletResponse res) throws Exception {
		noticeService.noticeFileDown(fileVo, res);
	}
	
	@ResponseBody
	@RequestMapping(value="/noticeDelete.do")
	public boolean noticeDelete(NoticeDeleteDTO dto, HttpServletRequest request) throws Exception {
		String userId = (String)request.getSession().getAttribute("userId");
		dto.setUserId(userId);
		
		return noticeService.noticeDelete(dto);
		
	}
	
	@ResponseBody
	@RequestMapping(value = "/noticeEditImgUpload.do")
	public String noticeEditImgUpload(MultipartHttpServletRequest multiRequest) throws Exception {
		NoticeFileVO       fileDto      = null;
		List<NoticeFileVO> fileInfoList = null;
		
		fileInfoList = noticeService.noticeEditImgUpload(multiRequest);

		if (fileInfoList == null) {
			return "FAIL";
		}

		if (fileInfoList != null && fileInfoList.size() > 0) {
			fileDto = fileInfoList.get(0);
		}

		return "/upload/notice/img/" + fileDto.getFileNameServer();
	}
	
	
	@ResponseBody
	@RequestMapping(value="/noticeImp.do")
	public boolean noticeImp(String noticeSeq) throws Exception {
		return noticeService.noticeImp(noticeSeq);
		
	}
	
	@ResponseBody
	@RequestMapping(value="/noticeImpCalcel.do")
	public boolean noticeImpCalcel(String noticeSeq) throws Exception {
		return noticeService.noticeImpCalcel(noticeSeq);
		
	}
	
}
