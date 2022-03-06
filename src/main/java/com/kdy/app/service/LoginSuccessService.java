package com.kdy.app.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import com.kdy.app.bean.LoginBean;
import com.kdy.app.dto.login.LoginVO;
import com.kdy.app.dto.system.LoginIpLogVO;
import com.kdy.app.service.IF.LoginServiceIF;

@Service
public class LoginSuccessService implements AuthenticationSuccessHandler{
	
	private final Logger logger = LoggerFactory.getLogger(LoginSuccessService.class);

	@Autowired
	LoginServiceIF loginService;
	
	@Autowired
	private LoginBean loginBean; 

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication auth) throws IOException, ServletException {
		
		LoginVO vo = (LoginVO)auth.getPrincipal();
		String userId = "";
		userId = vo.getUserId();
		
		try {
			
			//Client IP와 허용 IP 비교
			String usrIpChkType = "";
			usrIpChkType = loginBean.getuserIp(request, response);
			
			if(usrIpChkType == "ACCESS") {//접속 허용
				
				//로그 insert
				LoginIpLogVO logVo = new LoginIpLogVO();
				logVo.setLuId(userId);
				logVo.setLclCode("SUCCESS");
				logVo.setLclIp(getUserIp(request));
				logVo.setLclDevice(getUserAgent(request));
				logVo.setLclType("A");
				loginBean.logInsert(logVo);
				loginBean.loginDateUpdate(userId);
				response.sendRedirect(request.getContextPath() + "/loginSuccess.do");
				
			} else {//접속 불가
				
				//로그 insert
				LoginIpLogVO logVo = new LoginIpLogVO();
				logVo.setLuId(userId);
				logVo.setLclCode("IP_ACCESS_FAIL");
				logVo.setLclIp(getUserIp(request));
				logVo.setLclDevice(getUserAgent(request));
				logVo.setLclType("A");
				loginBean.logInsert(logVo);
				response.sendRedirect(request.getContextPath() + "/loginFail.do");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error : \n{}", e);
		}
	}
	
	private String getUserIp(HttpServletRequest request) {
		
		String userIp = request.getHeader("X-Forwarded-For");
        
		if (userIp == null || userIp.length() == 0 || "unknown".equalsIgnoreCase(userIp)) { 
			userIp = request.getHeader("Proxy-Client-IP"); 
		} 
		if (userIp == null || userIp.length() == 0 || "unknown".equalsIgnoreCase(userIp)) { 
			userIp = request.getHeader("WL-Proxy-Client-IP"); 
		} 
		if (userIp == null || userIp.length() == 0 || "unknown".equalsIgnoreCase(userIp)) { 
			userIp = request.getHeader("HTTP_CLIENT_IP"); 
		} 
		if (userIp == null || userIp.length() == 0 || "unknown".equalsIgnoreCase(userIp)) { 
			userIp = request.getHeader("HTTP_X_FORWARDED_FOR"); 
		}
		if (userIp == null || userIp.length() == 0 || "unknown".equalsIgnoreCase(userIp)) { 
			userIp = request.getHeader("X-Real-IP"); 
		}
		if (userIp == null || userIp.length() == 0 || "unknown".equalsIgnoreCase(userIp)) { 
			userIp = request.getHeader("X-RealIP"); 
		}
		if (userIp == null || userIp.length() == 0 || "unknown".equalsIgnoreCase(userIp)) { 
			userIp = request.getHeader("REMOTE_ADDR");
		}
		if (userIp == null || userIp.length() == 0 || "unknown".equalsIgnoreCase(userIp)) { 
			userIp = request.getRemoteAddr(); 
		}
		return userIp;
	}
	
	private String getUserAgent(HttpServletRequest request) {
		String userAgent = request.getHeader("User-Agent").toUpperCase();
		
		if(userAgent.indexOf("MOBI") > -1) {
			return "M";
		} else {
			return "P";
		}
	}

}
