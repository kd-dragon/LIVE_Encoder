package com.kdy.app.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.kdy.app.bean.dao.LoginDAO;
import com.kdy.app.bean.util.IpTree;
import com.kdy.app.dto.login.LoginVO;
import com.kdy.app.dto.login.MenuVO;
import com.kdy.app.dto.system.ConnectIpVO;
import com.kdy.app.dto.system.LoginIpLogVO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LoginBean {

	private final LoginDAO loginDao;

	public LoginVO loginChk(String userId) throws Exception {
		return loginDao.loginChk(userId);
	}

	public void logInsert(LoginIpLogVO vo) throws Exception {
		loginDao.logInsert(vo);
	}
	
	public void loginDateUpdate(String userId) throws Exception{
		loginDao.loginDateUpdate(userId);
	}

	public String getuserIp(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		//Client IP 가져오기
		String userIp = req.getHeader("X-Forwarded-For");

		if (userIp == null || userIp.length() == 0 || "unknown".equalsIgnoreCase(userIp)) {
			userIp = req.getHeader("Proxy-Client-IP");
		}
		if (userIp == null || userIp.length() == 0 || "unknown".equalsIgnoreCase(userIp)) {
			userIp = req.getHeader("WL-Proxy-Client-IP"); 
		}
		if (userIp == null || userIp.length() == 0 || "unknown".equalsIgnoreCase(userIp)) {
			userIp = req.getHeader("HTTP_CLIENT_IP");
		}
		if (userIp == null || userIp.length() == 0 || "unknown".equalsIgnoreCase(userIp)) {
			userIp = req.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (userIp == null || userIp.length() == 0 || "unknown".equalsIgnoreCase(userIp)) {
			userIp = req.getRemoteAddr();
		}

		String type = "";
		
		//접속 허용 IP List 가져오기
		List<ConnectIpVO> onlyIpList = new ArrayList<>();
		IpTree iptree = new IpTree();
		
		onlyIpList = loginDao.onlyIpList();
		int ipChkCnt = loginDao.connectIpCnt();

		//접속 허용 IP List에 아무것도 없으면 모든 IP 접속 허용
		if (ipChkCnt == 0) {
			type = "ACCESS";
		} else { //접속 허용 List의 IP와 Client IP 비교
			if(onlyIpList != null && onlyIpList.size() > 0) {
				for(int i=0; i<onlyIpList.size(); i++) {
					
					Map<String, Object> ipMap = new HashMap<>();
					ipMap = (Map<String, Object>) onlyIpList.get(i);
					
					iptree.add(ipMap.get("LSC_IP").toString());
				}
				
				if(iptree.containsIp(userIp)) {
					type = "ACCESS";
				
				}else  {
					type = "FAIL";
				}
			}
			
		}

		return type;
	}
	
	public List<MenuVO> menuList(LoginVO vo) throws Exception {
		return loginDao.menuList(vo);
	}

}
