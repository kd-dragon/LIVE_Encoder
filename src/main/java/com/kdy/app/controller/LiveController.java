package com.kdy.app.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.kdy.app.bean.util.ExcelDownBean;
import com.kdy.app.bean.util.MultiSlashChk;
import com.kdy.app.dto.channel.ChannelDTO;
import com.kdy.app.dto.live.AppBroadcastListDTO;
import com.kdy.app.dto.live.AppBroadcastVO;
import com.kdy.app.dto.live.ResultVO;
import com.kdy.app.dto.login.LoginVO;
import com.kdy.app.dto.vod.LiveContentsVO;
import com.kdy.app.dto.vod.LiveVodDTO;
import com.kdy.app.dto.watermark.WatermarkVO;
import com.kdy.app.service.IF.ChannelServiceIF;
import com.kdy.app.service.IF.LiveServiceIF;
import com.kdy.app.service.IF.VodServiceIF;
import com.kdy.live.bean.util.code.LiveBroadcastStatus;
import com.kdy.live.dto.LiveSchedMemoryVO;
import com.kdy.live.dto.live.ChatDTO;

import lombok.RequiredArgsConstructor;


@Controller
@RequestMapping("/live")
@RequiredArgsConstructor
public class LiveController {
	
	private final LiveServiceIF liveService;
	private final VodServiceIF vodService;
	private final ChannelServiceIF channelService;
	private final ExcelDownBean excelDownBean;
	private final LiveSchedMemoryVO memoryVO;
	
	@Value("${server.chat.host}")
	private String chatHost;
	
	@Value("${server.chat.port}")
	private String chatPort;
	
	
	@RequestMapping(value="/liveOnAirList.do", method= {RequestMethod.GET, RequestMethod.POST})
	public String liveOnAirList(AppBroadcastListDTO dto, Model model, Authentication auth) throws Exception{
		LoginVO vo = (LoginVO)auth.getPrincipal();
		dto.setAuthSeq(vo.getAuthSeq());
		dto.setCategorySeq(vo.getCategorySeq());
		
		AppBroadcastListDTO rtnDto = liveService.getOnAirList(dto);
		liveService.getOnAirStatusList(rtnDto);
		model.addAttribute("dto", rtnDto);
		
		return "live/live-onair-list";
	}
	
	/**?????? ?????? - list*/
	@RequestMapping(value="/liveWaitList.do", method= {RequestMethod.GET, RequestMethod.POST})
	public String liveWaitList(AppBroadcastListDTO dto, Model model, Authentication auth) throws Exception{
		LoginVO vo = (LoginVO)auth.getPrincipal();
		dto.setAuthSeq(vo.getAuthSeq());
		dto.setCategorySeq(vo.getCategorySeq());
		
		model.addAttribute("dto", liveService.getWaitList(dto));
		
		return "live/live-wait-list";
	}
	
	/**?????? ?????? - ?????? ?????? (?????? ??????)*/
	@RequestMapping(value="/copyBroadcastForm.do", method= {RequestMethod.GET, RequestMethod.POST})
	public String copyBroadcastForm(String lbSeq, Model model) throws Exception{
		model.addAttribute("dto", liveService.getBroadcast(lbSeq));
		return "live/live-copy-write";
	}
	
	/**?????? ?????? - detail*/
	@RequestMapping(value="/viewBroadcastWait.do", method= {RequestMethod.POST})
	public String viewBroadcastWait(AppBroadcastVO lbvo, Model model) throws Exception{
		String nlString = System.getProperty("line.separator").toString();
		model.addAttribute("nlString", nlString);
		model.addAttribute("vo", liveService.getBroadcastDetail(lbvo));
		
		return "live/live-wait-view";
	}
	
	/**?????? ?????? - regist Form*/
	@RequestMapping(value="/modifyBroadcastForm.do", method= {RequestMethod.POST})
	public String modifyBroadcastForm(AppBroadcastVO vo, Model model, Authentication auth) throws Exception{
		LoginVO loginVo = (LoginVO)auth.getPrincipal();
		model.addAttribute("loginVO", loginVo);
		ChannelDTO dto = new ChannelDTO();
		model.addAttribute("dto", channelService.channelList(dto));
		//model.addAttribute("categoryHtml", liveService.getCategoryTreeHtml(liveService.getCategoryList()));
		model.addAttribute("categoryList", liveService.getCategoryList());
		model.addAttribute("vo", liveService.getBroadcastDetail(vo));
		
		return "live/live-modify";
	}
	
	/**?????? ?????? - regist*/
	@ResponseBody
	@RequestMapping(value="/modifyBroadcast.do")
	public ResultVO modifyBroadcast(AppBroadcastVO lbvo, 
			@RequestPart("attach") MultipartFile attachFile,
			@RequestPart("thumbnail") MultipartFile thumbnailFile) throws Exception{
		
			return liveService.modifyBroadcast(lbvo, attachFile, thumbnailFile);
	}
	
	/**?????? ?????? - list*/
	@RequestMapping(value="/liveFinishList.do",  method= {RequestMethod.GET, RequestMethod.POST})
	public String liveFinishList(AppBroadcastListDTO dto, Model model, Authentication auth) throws Exception{
		LoginVO vo = (LoginVO)auth.getPrincipal();
		dto.setAuthSeq(vo.getAuthSeq());
		dto.setCategorySeq(vo.getCategorySeq());
		
		model.addAttribute("dto", liveService.getFinishList(dto));
		
		return "live/live-finish-list";
	}
	
	/**?????? ?????? - detail*/
	@RequestMapping(value="/viewBroadcastFinish.do", method= {RequestMethod.POST})
	public String viewBroadcastFinish(AppBroadcastVO lbvo, Model model) throws Exception{
		String nlString = System.getProperty("line.separator").toString();
		model.addAttribute("nlString", nlString);
		model.addAttribute("vo", liveService.getBroadcastDetail(lbvo));
		model.addAttribute("chatRecYn", memoryVO.getChatRecYn());
		
		return "live/live-finish-view";
	}
	
	
	/** ?????? ?????? **/
	@RequestMapping(value="/waitExcelDown.do")
	public void waitExcelDown(AppBroadcastListDTO dto, HttpServletRequest req, HttpServletResponse res) throws Exception {
		excelDownBean.liveBroadcast(liveService.getWaitListExcel(dto), "??????_??????_??????", req, res);
	}
	
	@RequestMapping(value="/finishExcelDown.do")
	public void finishExcelDown(AppBroadcastListDTO dto, HttpServletRequest req, HttpServletResponse res) throws Exception {
		excelDownBean.liveBroadcast(liveService.getFinishListExcel(dto), "??????_??????_??????", req, res);
	}
	
	@RequestMapping(value="/onAirExcelDown.do")
	public void onAirExcelDown(AppBroadcastListDTO dto, HttpServletRequest req, HttpServletResponse res) throws Exception {
		excelDownBean.liveBroadcast(liveService.getOnAirListExcel(dto), "?????????_?????????_??????", req, res);
	}
	
	@RequestMapping(value="/chatExcelDown.do")
	public void chatExcelDown(AppBroadcastListDTO dto, HttpServletRequest req, HttpServletResponse res) throws Exception {
		excelDownBean.liveChat(liveService.getChattingListExcel(dto), "???????????????_??????", req, res);
	}
	
	/**?????? ??????, ?????? ?????? ?????? - list?????? ?????? ??????*/
	@ResponseBody
	@RequestMapping(value="/removeBroadcast.do", method = RequestMethod.POST)
	public String removeBroadcast(AppBroadcastListDTO dto) throws Exception{
		return liveService.removeBroadcast(dto);
	}
	
	/**?????? ??????, ?????? ?????? ?????? - detail?????? ?????? ??????*/
	@ResponseBody
	@RequestMapping(value="/removeBroadcastOne.do", method = RequestMethod.POST)
	public String removeBroadcastOne(AppBroadcastListDTO dto) throws Exception{
		return liveService.removeBroadcastOne(dto);
	}
	
	/** ?????? ?????? - form **/
	@RequestMapping(value="/registBroadcastForm.do", method= {RequestMethod.GET, RequestMethod.POST})
	public String registBroadcastForm(HttpServletRequest req, Authentication auth, Model model) throws Exception{
		LoginVO vo = (LoginVO)auth.getPrincipal();
		model.addAttribute("loginVO", vo);
		ChannelDTO dto = new ChannelDTO();
		model.addAttribute("dto", channelService.channelList(dto));
		//model.addAttribute("categoryHtml", liveService.getCategoryTreeHtml(liveService.getCategoryList()));
		model.addAttribute("categoryList", liveService.getCategoryList());
		model.addAttribute("previousURL",  req.getHeader("REFERER"));
		
		return "live/live-write";
	}
	
	/** ?????? ??????  - regist **/
	@ResponseBody
	@RequestMapping(value="/registBroadcast.do")
	public ResultVO registBroadcast(  Authentication auth
									, AppBroadcastVO lbvo
									, @RequestPart("attach") MultipartFile attachFile
									, @RequestPart("thumbnail") MultipartFile thumbnailFile) throws Exception{
		
		LoginVO loginVo = (LoginVO)auth.getPrincipal();
		lbvo.setLbRegUserId(loginVo.getUserId());
		
		return liveService.registBroadcast(lbvo, attachFile, thumbnailFile);
	}
	
	/**  ?????? ?????? - VOD ???????????? POPUP  **/
	@RequestMapping("/callVodListPopup.do")
	public String callVodListPopup(LiveVodDTO dto, Model model) throws Exception{
		model.addAttribute("categoryList", liveService.getCategoryList());
		
		return "popup/callVodList-popup";
	}
	
	/**
	 * ?????? ?????? - VOD ???????????? POPUP - ????????? ????????? ???????????? (selectBox)
	 */
	@ResponseBody
	@RequestMapping("/getContentsListByCateSeq.do")
	public List<LiveContentsVO> getContentsListByCateSeq(String lbCategorySeq) throws Exception {
		return vodService.getContentsListByCateSeq(lbCategorySeq);
	}
	
	
	/** ?????? ?????? - VOD ???????????? POPUP - VOD (video) ????????? **/
	@ResponseBody
	@RequestMapping("/getLiveVodList.do")
	public LiveVodDTO getLiveVodList(LiveVodDTO dto) throws Exception {
		return vodService.liveVodList(dto);
	}
	
	/** ?????? ??? - detail **/
	@RequestMapping(value="/viewBroadcast.do")
	public String viewBroadcast(AppBroadcastVO lbvo, Model model, Authentication auth, WatermarkVO watermarkVo) throws Exception {
		LoginVO loginVo = (LoginVO)auth.getPrincipal();
		String nlString = System.getProperty("line.separator").toString();
		
		AppBroadcastVO rtnVO = liveService.getBroadcastDetail(lbvo);
		
		model.addAttribute("nlString", nlString);
		model.addAttribute("userId", loginVo.getUserId());
		model.addAttribute("userName", loginVo.getUsername());
		model.addAttribute("chatUrl", chatHost + ":" + chatPort);
		model.addAttribute("vo", rtnVO);
		
		if(rtnVO.getLbStatus().equals("1") || rtnVO.getLbStatus().equals("4") || rtnVO.getLbStatus().equals("8")) {
			//???????????? ??????
			model.addAttribute("watermarkVo", liveService.getWatermark(watermarkVo));
			return "live/live-onair-view";
		} else if(rtnVO.getLbStatus().equals("9")) {
			return "live/live-error-view";
		} else if(rtnVO.getLbStatus().equals("3")) {
			return "live/live-pause-view";
		} else {
			return "live/live-finish-view";
		}
		
	}
	
	/** ???????????? ???????????? **/
	@RequestMapping(value="/downloadAttachFile.do")
	public void downloadAttachFile(AppBroadcastVO lbvo, HttpServletRequest req, HttpServletResponse res) throws Exception {
		liveService.attachFileDownload(lbvo, res);
	}
	
	//Live player
	/*
	 * @RequestMapping("/livePlay.do") public String livePlay(AppBroadcastVO lbvo,
	 * Model model, HttpServletRequest request, WatermarkVO watermarkVo) throws
	 * Exception{ //???????????? ?????? model.addAttribute("lbvo",
	 * liveService.getLiveVideoData(lbvo)); //???????????? ??????
	 * model.addAttribute("watermarkVo", liveService.getWatermark(watermarkVo));
	 * 
	 * return "live/player_live"; }
	 */
	
	//live ??????
	@ResponseBody
	@RequestMapping("/liveBroadcastStatusEnd.do")
	public String liveBroadcastStatusEnd(String lbSeq) throws Exception {
		return liveService.liveBroadcastStatusEnd(lbSeq);
	}
	
	@RequestMapping(value="/viewBroadcastError.do", method= {RequestMethod.POST})
	public String viewBroadcastError(AppBroadcastVO lbvo, Model model, Authentication auth) throws Exception{
		String nlString = System.getProperty("line.separator").toString();
		model.addAttribute("nlString", nlString);
		model.addAttribute("vo", liveService.getBroadcastDetail(lbvo));
		
		LoginVO loginVo = (LoginVO)auth.getPrincipal();
		model.addAttribute("userId", loginVo.getUserId());
		model.addAttribute("userName", loginVo.getUsername());
		model.addAttribute("chatUrl", chatHost + ":" + chatPort);
		
		return "live/live-error-view";
	}


	@RequestMapping(value="/chatFileDownload.do", method = {RequestMethod.GET, RequestMethod.POST})
	public void chatFileDownload(AppBroadcastVO vo, 
									HttpServletRequest request, 
									HttpServletResponse response) throws Exception {
		PrintWriter writer = null;
		String lbSeq = vo.getLbSeq().toString();
		//?????? ?????? ?????? ?????? return
		if(lbSeq.equals("") || lbSeq == null) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
			
			//JSONObject json = new JSONObject();
			//json.put("errorMsg", "????????? ????????? ?????? ??? ????????????. ????????? ?????? ????????? ??????????????? ?????? ????????????.");
			
			writer = response.getWriter();
			//writer.print(json.toString());
			writer.flush();
			writer.close();
			return;
		} else {
			AppBroadcastVO liveInfo = liveService.getLiveInfo(lbSeq);
			if(liveInfo == null) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
				
				//JSONObject json = new JSONObject();
				//json.put("errorMsg", "????????? ????????? ?????? ??? ????????????.");
				
				writer = response.getWriter();
				//writer.print(json.toString());
				writer.flush();
				writer.close();
				return;
			} else {
				String chatFileName = liveInfo.getEndYear().toString() + "-" + liveInfo.getEndMonth().toString()+ "-" + liveInfo.getEndDay() + "_" + liveInfo.getLbSeq() + "_chat.txt";
				String chatFilePath = liveInfo.getUploadRootPath() + "chat/" + liveInfo.getStartYear().toString() + "/" + liveInfo.getStartMonth().toString()+ "/";
				String backupChatFileName = liveInfo.getStartYear().toString() + "-" + liveInfo.getStartMonth().toString() + "-" + liveInfo.getStartDay().toString() + "_" + liveInfo.getLbSeq() + "_chat.txt";
				String backupChatFilePath = chatFilePath + "backup/";
				
				File chatFile = new File(MultiSlashChk.path(chatFilePath + chatFileName));
				File backupChatFile = new File(MultiSlashChk.path(backupChatFilePath + backupChatFileName) );
				try {
					long contentLength = 0;
					String contentDisposition = "";
					
					contentDisposition = "attachment;filename=" + URLEncoder.encode(liveInfo.getLbTitle().toString()+".txt" , "UTF-8");
					
					BufferedInputStream in = null;
					BufferedOutputStream out = null;
					FileInputStream fis = null;
					 
					if(chatFile.exists()) {
						contentLength = chatFile.length();
						fis = new FileInputStream(chatFile);
					} else {
						contentLength = backupChatFile.length();
						fis = new FileInputStream(backupChatFile);
					}
					
					response.setContentLength((int)contentLength);
					response.setHeader("Content-Disposition", contentDisposition);
					response.setContentType("binary/octet-stream");
					
					response.setBufferSize(4096);
					
					in = new BufferedInputStream(fis);
					out = new BufferedOutputStream(response.getOutputStream());
					
					FileCopyUtils.copy(in, out);
					out.close();
					in.close();
					fis.close();
					
				} catch (FileNotFoundException fne) {
					response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
					response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
					
					//JSONObject json = new JSONObject();
					//json.put("errorMsg", "????????? ????????? ????????? ?????? ????????? ????????????. ");
					
					writer = response.getWriter();
					//writer.print(json.toString());
					writer.flush();
					writer.close();
					return;
				} catch (IOException ioe) {
					response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
					response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
					
					//JSONObject json = new JSONObject();
					//json.put("errorMsg", "server error. please contact system manager");
					
					writer = response.getWriter();
					//writer.print(json.toString());
					writer.flush();
					writer.close();
					return;
				} catch (Exception e) {
					response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
					response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
					
					//JSONObject json = new JSONObject();
					//json.put("errorMsg", "server error. please contact system manager");
					
					writer = response.getWriter();
					//writer.print(json.toString());
					writer.flush();
					writer.close();
					return;
				}
			}
		}
		
	}
	
	//????????? ???????????? ?????????
	@RequestMapping(value="/viewBroadcastPause.do")
	public String viewBroadcastPause(AppBroadcastVO lbvo, Model model, Authentication auth) throws Exception {
		LoginVO loginVo = (LoginVO)auth.getPrincipal();
		String nlString = System.getProperty("line.separator").toString();
		model.addAttribute("nlString", nlString);
		model.addAttribute("vo", liveService.getBroadcastDetail(lbvo));
		model.addAttribute("userId", loginVo.getUserId());
		model.addAttribute("userName", loginVo.getUsername());
		model.addAttribute("chatUrl", chatHost + ":" + chatPort);
		return "live/live-pause-view";
	}
	
	//????????? ???????????? ??? ?????????
	@ResponseBody
	@RequestMapping(value = "/livePauseAndStart.do")
	public String livePauseAndStart(@RequestBody AppBroadcastVO vo) throws Exception {
		return liveService.livePauseAndStart(vo);
	}
	
	
	@ResponseBody //Live ?????? ?????? (1:?????????/2:??????/3:????????????)
	@RequestMapping(value="/liveStatusCheck.do")
	public String liveFinishCheck(String lbSeq) throws Exception {
		return liveService.liveStatusCheck(lbSeq);
	}
	
	public boolean waitListStatusCheck(String status) throws Exception{
		if(status.equals(LiveBroadcastStatus.Wait.getTitle())) {
			return true;
		}
		return false;
	}
	
	public boolean onAirListStatusCheck(String status) throws Exception{
		if(status.equals(LiveBroadcastStatus.OnAir.getTitle())) {
			return true;
		}else if(status.equals(LiveBroadcastStatus.Restart.getTitle())) {
			return true;
		}else if(status.equals(LiveBroadcastStatus.Pause.getTitle())) {
			return true;
		}
		return false;
	}
	
	public boolean finishListStatusCheck(String status) throws Exception{
		if(status.equals(LiveBroadcastStatus.Finished.getTitle())) {
			return true;
		}else if(status.equals(LiveBroadcastStatus.Recording.getTitle())) {
			return true;
		}else if(status.equals(LiveBroadcastStatus.Error.getTitle())) {
			return true;
		}
		return false;
	}
	
	@ResponseBody
	@RequestMapping("/liveErrorStatusEnd.do")
	public String liveErrorStatusEnd(String lbSeq) throws Exception {
		return liveService.liveErrorStatusEnd(lbSeq);
	}
	
	@ResponseBody
	@RequestMapping("/liveErrorStatusRestart.do")
	public String liveErrorStatusRestart(String lbSeq) throws Exception {
		return liveService.liveErrorStatusRestart(lbSeq);
	}
	
	@ResponseBody
	@RequestMapping("/getDynamicRecordEnable.do")
	public String getDynamicRecordEnable(String lbSeq) throws Exception {
		return liveService.getDynamicRecordEnable(lbSeq);
	}
	
	@ResponseBody
	@RequestMapping("/updateDynamicRecordEnable.do")
	public String updateDynamicRecordEnable(String lbSeq, String enableYn) throws Exception {
		String retval = liveService.getDynamicRecordEnable(lbSeq);
		if(retval.equalsIgnoreCase(enableYn)) {
			return "ALREADY";
		} else {
			return liveService.updateDynamicRecordEnable(lbSeq);
		}
	}
	
	@ResponseBody
	@RequestMapping("/checkChatFile.do")
	public String checkChatFile(String lbSeq) throws Exception {
		return liveService.checkChatFile(lbSeq);
	}
	
	@ResponseBody
	@RequestMapping("/chatHistory.do")
	public List<ChatDTO> chatHistory(ChatDTO dto) throws Exception {
		return liveService.getChatHistory(dto);
	}
}
