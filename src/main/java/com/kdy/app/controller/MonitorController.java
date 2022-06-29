package com.kdy.app.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kdy.app.dto.login.LoginVO;
import com.kdy.app.service.IF.MonitorServiceIF;
import com.kdy.live.dto.monitor.LiveMonitorDTO;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/monitor")
@RequiredArgsConstructor
public class MonitorController extends ExceptionController{
	
	private final MonitorServiceIF monitorService;
	
	@RequestMapping("/monitorMain.do")
	public String monitorMain(Model model) throws Exception{
		model.addAttribute("streamingList", monitorService.getStreamingList());
		return "monitor/streaming-monitor";
	}
	
	@ResponseBody
	@RequestMapping("/getMonitorData.do")
	public LiveMonitorDTO getMonitorData(LiveMonitorDTO dto, Authentication auth) throws Exception{
		LoginVO vo = (LoginVO)auth.getPrincipal();
		dto.setAuthSeq(vo.getAuthSeq());
		dto.setCategorySeq(vo.getCategorySeq());
		
		return monitorService.getMonitorData(dto);
	}
}
