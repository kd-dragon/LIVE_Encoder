package com.kdy.app.service;

import java.security.PrivateKey;

import javax.crypto.Cipher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class PasswordEncoderService implements PasswordEncoder {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	@Lazy
	BCryptPasswordEncoder bcryptEncoder;
	
	@Override
	public String encode(CharSequence rawPassword) {
		return bcryptEncoder.encode(rawPassword);
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
//		rawPassword: 암호화 되지않은 값 (화면에서 입력한 값)
//		encodedPassword : 암호화된 값 (DB 값)
		
		boolean rtn = false;
		
		try {
			
			rawPassword = decrypt(rawPassword.toString());
			
			rtn = bcryptEncoder.matches(rawPassword, encodedPassword);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("cause : " + e.getClass() + ", line : " + e.getStackTrace()[0].getLineNumber());
		}
		
		return rtn;
	}
	
	
	public String decrypt(String password) throws Exception {
		
		HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		HttpSession session = req.getSession();
		
		PrivateKey privateKey = (PrivateKey) session.getAttribute("__rsaPrivateKey__");
		
		return decryptRsa(privateKey, password);
		
	}
	
	public String decryptRsa(PrivateKey privateKey, String securedValue) throws Exception {

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
