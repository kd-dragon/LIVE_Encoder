package com.kdy.app.controller.api;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.kdy.app.dto.api.response.ResponseBroadcastDetailDTO;
import com.kdy.app.dto.vod.VodVO;
import com.kdy.app.dto.watermark.WatermarkVO;
import com.kdy.app.service.api.IF.ApiLiveServiceIF;
import com.kdy.app.service.api.IF.ApiVodServiceIF;

@Controller
public class ApiPlayerController {
	
	private final Logger logger = LoggerFactory.getLogger(ApiPlayerController.class);
	
	private final ApiLiveServiceIF liveService;
	private final ApiVodServiceIF vodService;
	
	public ApiPlayerController(ApiLiveServiceIF liveService
							, ApiVodServiceIF vodService) {
		this.liveService = liveService;
		this.vodService = vodService;
	}
	
	/**
	 * 라이브 플레이어 호출
	 */
	@GetMapping("/api/v2.0/live-player/{seq}")
	public String goLivePlayer(@PathVariable("seq") String lbSeq, Model model, HttpSession session) throws Exception {
		
		if(lbSeq == null || lbSeq.equals("null")) {
			return "live/live-not-found";
		}
		
		try {
			
			ResponseBroadcastDetailDTO response = liveService.getBroadcast(lbSeq);
			
			if(response == null) {
				logger.error("[API ERROR] call live player ::: live sequence is not valid");
				return "live/live-not-found";
			}
			
			if(response.getLbStatus().equals("2")) {
				return "live/live-end";
			}
			
			model.addAttribute("res", response);
			model.addAttribute("watermarkVo", vodService.getWatermark());
			
			return "live/player-live";
		} catch (Exception e) {
			e.printStackTrace();
			return "live/live-not-found";
		}
		
	}
	
	
	
	/**
	 * VOD 플레이어 호출
	 */
	@GetMapping("/api/v2.0/vod-player/{seq}")
	public String goVodPlayer(@PathVariable("seq") String vodSeq, Model model) throws Exception{
		
		if(vodSeq == null || vodSeq.equals("null")) {
			return "live/live-not-found";
		}
		
		try {
			
			VodVO vo = vodService.getVodDetail(vodSeq);
			
			if(vo == null) {
				logger.error("[API ERROR] call VOD player ::: VOD sequence is not valid");
				return "live/live-not-found";
			}
			
			model.addAttribute("vo", vo);
			model.addAttribute("watermarkVo", vodService.getWatermark());
			
			return "vod/player-vod";
		} catch (Exception e) {
			return "live/live-not-found";
		}
		
	}
	

}
