package com.kdy.app.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.kdy.app.bean.LoginBean;
import com.kdy.app.dto.system.LoginIpLogVO;

@Component
public class LoginFailureService implements AuthenticationFailureHandler{
	
	private final Logger logger = LoggerFactory.getLogger(LoginFailureService.class);
	
	@Autowired
	private LoginBean loginBean;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		
		try {
			String userId = decrypt(request.getParameter("encId"));
			
			LoginIpLogVO logVo = new LoginIpLogVO();
			logVo.setLuId(userId);
			logVo.setLclCode("FAIL");
			logVo.setLclIp(getUserIp(request));
			logVo.setLclDevice(getUserAgent(request));
			logVo.setLclType("A");
			
			loginBean.logInsert(logVo);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error : \n{}", e);
		}
		
		response.sendRedirect(request.getContextPath() + "/loginFail.do");
		
	}
	
	public String decrypt(String username) {
		
		HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		HttpSession session = req.getSession();
		PrivateKey privateKey = (PrivateKey) session.getAttribute("__rsaPrivateKey__");
		return decryptRsa(privateKey, username);
		
	}
	
	private String decryptRsa(PrivateKey privateKey, String securedValue)  {

		try{
			Cipher cipher = Cipher.getInstance("RSA");
			byte[] encryptedBytes = hexToByteArray(securedValue);
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
			String decryptedValue = new String(decryptedBytes, "utf-8"); // 문자 인코딩 주의.
			return decryptedValue;
		}catch(IllegalBlockSizeException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | UnsupportedEncodingException e ){
			e.printStackTrace();
			return "DECRYPT_FAIL";
		}
	}

	public static byte[] hexToByteArray(String hex) {
		if (hex == null || hex.length() % 2 != 0) {
			return new byte[] {};
		}

		byte[] bytes = new byte[hex.length() / 2];
		for (int i = 0; i < hex.length(); i += 2) {
			byte value = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
			bytes[(int) Math.floor(i / 2)] = value;
		}
		return bytes;
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
