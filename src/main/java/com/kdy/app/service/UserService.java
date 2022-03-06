package com.kdy.app.service;

import java.security.PrivateKey;

import javax.crypto.Cipher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.kdy.app.bean.LoginBean;
import com.kdy.app.dto.login.LoginVO;

@Service
public class UserService implements UserDetailsService{
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private LoginBean loginBean;
	
	public UserService(LoginBean loginBean) {
		this.loginBean = loginBean;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		try {
			
			String userId = decrypt(username);
			
			LoginVO loginVo = loginBean.loginChk(userId);
			
			if(loginVo == null) {
	            throw new UsernameNotFoundException(username);
	        }
			
			//권한 설정
			loginVo.setAuth("ROLE_ADMIN");
			
			return loginVo;
			
		} catch (Exception e) {
			logger.error("cause : " + e.getClass() + ", line : " + e.getStackTrace()[0].getLineNumber());
			logger.error(e.getMessage());
			return null;
		}
		
	}
	
	public String decrypt(String username) throws Exception {
		
		HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		HttpSession session = req.getSession();
		
		PrivateKey privateKey = (PrivateKey) session.getAttribute("__rsaPrivateKey__");
		
		return decryptRsa(privateKey, username);
		
	}
	
	private String decryptRsa(PrivateKey privateKey, String securedValue) throws Exception {

		Cipher cipher = Cipher.getInstance("RSA");

		byte[] encryptedBytes = hexToByteArray(securedValue);

		cipher.init(Cipher.DECRYPT_MODE, privateKey);

		byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
		String decryptedValue = new String(decryptedBytes, "utf-8"); // 문자 인코딩 주의.
		return decryptedValue;
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
	

}
