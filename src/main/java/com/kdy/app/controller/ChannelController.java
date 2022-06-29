package com.kdy.app.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kdy.app.dto.channel.ChannelDTO;
import com.kdy.app.dto.channel.ChannelVO;
import com.kdy.app.dto.login.LoginVO;
import com.kdy.app.service.IF.ChannelServiceIF;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/channel")
@RequiredArgsConstructor
public class ChannelController extends ExceptionController{
	
	private final ChannelServiceIF channelService;
	
	//채널 리스트
	@RequestMapping("/channelList.do")
	public String channelList(ChannelDTO dto, Model model, Authentication auth) throws Exception{
		LoginVO vo = (LoginVO)auth.getPrincipal();
		model.addAttribute("loginVO", vo);
		model.addAttribute("dto", channelService.channelList(dto));
		return "channel/channel-setting-list";
	}
	
	//채널 추가
	@ResponseBody
	@RequestMapping("/addChannel.do")
	public String addChannel(ChannelVO vo) throws Exception{
		return channelService.addChannel(vo);
	}
	
	
	//채널 수정
	@ResponseBody
	@RequestMapping("/modChannel.do")
	public String modChannel(ChannelVO vo) throws Exception{
		return channelService.modChannel(vo);
	}
	
	
	//채널 삭제
	@ResponseBody
	@RequestMapping("/delChannel.do")
	public String delChannel(String deleteSeqs) throws Exception{
		return channelService.delChannel(deleteSeqs);
	}
	
	
	
}
