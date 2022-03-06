package com.kdy.app.controller;

import java.security.PrivateKey;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kdy.app.dto.system.ConnectIpDTO;
import com.kdy.app.dto.system.ConnectIpVO;
import com.kdy.app.dto.system.LoginIpLogDTO;
import com.kdy.app.dto.system.StreamingDTO;
import com.kdy.app.dto.system.StreamingVO;
import com.kdy.app.dto.system.UserListDTO;
import com.kdy.app.dto.system.UserVO;
import com.kdy.app.service.IF.SystemServiceIF;
import com.kdy.live.dto.system.SystemConfigVO;

@Controller
@RequestMapping("/system")
public class SystemController extends ExceptionController{
	
	
	private final SystemServiceIF systemService;
	
	@Autowired
	public SystemController(SystemServiceIF systemService) {
		this.systemService = systemService;
	}
	
	//시스템 관리 > 권한 관리 
	@RequestMapping("/authList.do")
	public String authList() throws Exception{
		return "system/authorize";
	}
	
	//시스템 관리 > 관리자 계정
	@RequestMapping(value="/adminManage.do", method= {RequestMethod.GET, RequestMethod.POST})
	public String adminManage(UserListDTO dto, Model model) throws Exception {
		model.addAttribute("dto", systemService.getUserList(dto));
		return "system/adminManage";
	}
	
	//시스템 관리 > 관리자 삭제
	@ResponseBody
	@RequestMapping(value="/delAdmin.do")
	public String delAdmin(String luIds) throws Exception {
		return systemService.removeAdmin(luIds);
	}
	
	//시스템 관리 > 관리자 등록
	@ResponseBody
	@RequestMapping(value="/insertAdmin.do")
	public String insertAdmin(UserVO vo, HttpServletRequest request) throws Exception {
		PrivateKey privateKey = (PrivateKey)request.getSession().getAttribute("__rsaPrivateKey__");
		vo.setPrivateKey(privateKey);
		return systemService.registerAdmin(vo);
	}
	
	//시스템 관리 > 괸리자 등록 > 아이디 중복 체크
	@ResponseBody
	@RequestMapping(value="/idDupCheck.do")
	public String idDupCheck(String luId) throws Exception {
		String type = "ERROR";
		type = systemService.idDupCheck(luId);
		return type;
	}
	
	//시스템 관리 > 관리자 수정
	@ResponseBody
	@RequestMapping(value="/modifyAdmin.do")
	public String modifyAdmin(UserVO vo, HttpServletRequest request) throws Exception {
		PrivateKey privateKey = (PrivateKey)request.getSession().getAttribute("__rsaPrivateKey__");
		vo.setPrivateKey(privateKey);
		return systemService.modifyAdmin(vo);
	}
	
	//시스템 관리 > 모니터링
	@RequestMapping(value="/systemMonitor.do", method = {RequestMethod.GET, RequestMethod.POST})
	public String systemMonitor(SystemConfigVO vo, Model model) throws Exception {
		model.addAttribute("vo", systemService.getUploadRootPath());
		return "system/present-systemuse";
	}
	
	//시스템 관리 > 데이터 비동기식 전달
	@ResponseBody
	@RequestMapping(value ="/systemStatus.do")
	public Map<String, Object> systemStatus() throws Exception {
		return systemService.getSystemStatus();
	}
	
	/**
	 * 접속 허용 IP
	 */
	@RequestMapping(value="/setConnectIpList.do", method= {RequestMethod.GET, RequestMethod.POST})
	public String setConnectIpList(ConnectIpDTO dto, Model model) throws Exception {
		model.addAttribute("dto", systemService.getConnectIpList(dto));
		return "system/ip-control";
	}
	
	
	//접속 허용 IP 추가
	@ResponseBody
	@RequestMapping(value="/insertConnectIp.do")
	public String insertConnectIp(ConnectIpVO vo) throws Exception {
		return systemService.insertConnectIp(vo);
	}
	
	//접속 허용 IP 수정
	@ResponseBody
	@RequestMapping(value="/modifyConnectIp.do")
	public String modifyConnectIp(ConnectIpVO vo) throws Exception {
		return systemService.modifyConnectIp(vo);
	}
	
	//접속 허용 IP 삭제
	@ResponseBody
	@RequestMapping(value="/delConnectIp.do")
	public String delConnectIp(String lscIps) throws Exception {
		return systemService.delConnectIp(lscIps);
	}
	
	/**
	 * 로그인 IP 로그 (접속기록)
	 */
	@RequestMapping("/loginIpLogList.do")
	public String loginIpLogList(LoginIpLogDTO dto, Model model) throws Exception{
		model.addAttribute("dto", systemService.getLoginIpLogList(dto));
		return "system/loginip-log";
	}
	
	/**
	 * 스트리밍 서버 목록
	 */
	@RequestMapping(value="/setStreamingList.do", method= {RequestMethod.GET, RequestMethod.POST})
	public String setStreamingList(StreamingDTO dto, Model model) throws Exception {
		model.addAttribute("dto", systemService.getStreamingList(dto));
		return "system/streaming-control";
	}
	
	
	//스트리밍 서버 추가
	@ResponseBody
	@RequestMapping(value="/insertStreaming.do")
	public String insertStreaming(StreamingVO vo) throws Exception {
		return systemService.insertStreaming(vo);
	}
	
	//스트리밍 서버 수정
	@ResponseBody
	@RequestMapping(value="/modifyStreaming.do")
	public String modifyStreaming(StreamingVO vo) throws Exception {
		return systemService.modifyStreaming(vo);
	}
	
	//스트리밍 서버 삭제
	@ResponseBody
	@RequestMapping(value="/delStreaming.do")
	public String delStreaming(String streamingIps) throws Exception {
		return systemService.delStreaming(streamingIps);
	}

}
