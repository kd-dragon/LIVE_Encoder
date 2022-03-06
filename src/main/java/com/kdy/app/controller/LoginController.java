package com.kdy.app.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kdy.app.dto.login.LoginVO;
import com.kdy.app.service.IF.LoginServiceIF;

@Controller
public class LoginController extends ExceptionController{
	
	@Autowired
	LoginServiceIF loginService;
	
	@RequestMapping("/")
	public String home() throws Exception{
		return "redirect:/loginForm.do";
	}
	
//	@RequestMapping("/loginForm.do")
//	public String loginForm(HttpSession session) throws Exception {
//		
//		if(session.getAttribute("userId") != null) {
//			return "redirect:/live/liveOnAirList.do";
//		}
//		
//		HashMap<String, Object> rsaMap = new HashMap<String, Object>();
//		rsaMap = loginService.keyGenerate();
//		
//		session.setAttribute("rsaPublicKeyModules",	rsaMap.get("rsaPublicKeyModules"));
//		session.setAttribute("rsaPublicKeyExponent", rsaMap.get("rsaPublicKeyExponent"));
//		session.setAttribute("__rsaPrivateKey__", rsaMap.get("rsaPrivateKey"));
//		
//		return "login/loginForm"; 
//	}
	
	@RequestMapping("/loginForm.do")
	public String loginForm(HttpSession session) throws Exception {

		return "redirect:/swagger-ui.html"; 
	}
	
	@RequestMapping("/loginSuccess.do")
	public String loginSuccess(Authentication auth, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		LoginVO loginVo = (LoginVO)auth.getPrincipal();
		
		//Session에 메뉴/로그인ID/권한 정보 넣기 
		HttpSession session = request.getSession();
		response.setStatus(HttpServletResponse.SC_OK);
		session.setAttribute("userId", loginVo.getUserId());
		session.setAttribute("userName", loginVo.getUsername());
		session.setAttribute("authSeq", loginVo.getAuthSeq());
		
		session.setAttribute("menuList", loginService.menuList(loginVo, request));
		
		return "redirect:/live/liveOnAirList.do";
	}
    
    @RequestMapping("/loginFail.do")
    public void loginFail(HttpServletRequest req, HttpServletResponse response, Authentication auth) throws Exception{
    	
    	response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.println("<script>alert(\"로그인 실패. 아이디 또는 비밀번호를 다시 확인해주세요.\");\n" + 
				"		window.location.href = \""+req.getContextPath()+"/loginForm.do\";"
				+ "</script>");
		out.flush();
		
    }
    
    //잘못된 접근
    @RequestMapping("/loginAccessError.do")
    public void loginAccessError(HttpServletRequest req, HttpServletResponse response) throws IOException {
    	response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.println("<script>alert(\"잘못된 접근입니다.\");\n" + 
				"		window.location.href = \""+req.getContextPath()+"/loginForm.do\";"
				+ "</script>");
		out.flush();
    }
    
    //접근 권한 없음
    @RequestMapping("/loginAccessDenied.do")
    public void loginAccessDenied(HttpServletRequest req, HttpServletResponse response, Authentication auth) throws Exception {
    	
    	if(auth!=null) {
    		LoginVO vo = (LoginVO)auth.getPrincipal();
    		loginService.logInsert(vo, "ACCESS_DENIED", req );
    	}
    	
    	response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.println("<script>alert(\"접근이 불가능한 아이디입니다.\");\n" + 
				"		window.location.href = \""+req.getContextPath()+"/loginForm.do\";"
				+ "</script>");
		out.flush();
    }
    
}