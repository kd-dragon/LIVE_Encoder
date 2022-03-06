package com.kdy.app.controller.client;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.kdy.app.dto.api.request.RequestBroadcastInsertDTO;
import com.kdy.app.dto.api.request.RequestBroadcastListDTO;
import com.kdy.app.dto.api.request.RequestBroadcastUpdateDTO;
import com.kdy.app.dto.live.ResultVO;

import org.springframework.stereotype.Controller;

@Controller
@RequestMapping("/webclient")
public class WebClientController {
	

	private   WebClientServiceIF webClientService;

	@Autowired
	public WebClientController(WebClientServiceIF webClientService) {
		this.webClientService = webClientService;
	}
	
	
	
	@RequestMapping(value="/delete.do")
	public ResultVO delete(String[] seq, Model model) throws Exception {
		 return webClientService.delete(seq);
	}
	
	@RequestMapping(value="/detail.do")
	public void detail(String seq, Model model) throws Exception {
		model.addAttribute(webClientService.detail(seq));
	}
	
	@RequestMapping(value="/list.do")
	public void list(RequestBroadcastListDTO dto, Model model) throws Exception {
		model.addAttribute(webClientService.list(dto));
	}
	
	@RequestMapping(value="/insert.do")
	public ResultVO insert(  RequestBroadcastInsertDTO dto
				           , @RequestPart(value="attach", required =false) MultipartFile attachFile
				           , @RequestPart("thumbnail") MultipartFile thumbnailFile
				           , Model model) throws Exception {
		
		return webClientService.insert(dto, attachFile, thumbnailFile);
	}

	@RequestMapping(value="/update.do")
	public ResultVO update(  RequestBroadcastUpdateDTO dto
				           , @RequestPart(value="attach", required =false) MultipartFile attachFile
				           , @RequestPart(value = "thumbnail", required =false) MultipartFile thumbnailFile
				           , Model model) throws Exception {
		
		return webClientService.update(dto, attachFile, thumbnailFile);
	}
	
	@RequestMapping(value="/excel.do")
	public void excel(RequestBroadcastListDTO dto, HttpServletRequest req, HttpServletResponse res) throws Exception {
		webClientService.excel(dto, req, res);
	}

}
