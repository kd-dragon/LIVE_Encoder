package com.kdy.app.service.IF;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.kdy.app.dto.login.LoginVO;

public interface LoginServiceIF {
	
	public HashMap<String, Object> keyGenerate();
	
	public void logInsert(LoginVO vo, String connect, HttpServletRequest req) throws Exception;
	
	public String menuList(LoginVO vo, HttpServletRequest request) throws Exception;
    	
}