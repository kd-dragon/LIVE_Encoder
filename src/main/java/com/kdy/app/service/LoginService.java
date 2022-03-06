package com.kdy.app.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kdy.app.bean.LoginBean;
import com.kdy.app.bean.RSAArrBean;
import com.kdy.app.dto.login.LoginVO;
import com.kdy.app.dto.login.MenuVO;
import com.kdy.app.dto.system.LoginIpLogVO;
import com.kdy.app.service.IF.LoginServiceIF;

@Service
public class LoginService implements LoginServiceIF{
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private LoginBean loginBean;  
	
	@Autowired
	private RSAArrBean rsaArrBean;
	
	@Override
	public HashMap<String, Object> keyGenerate() {
		
		HashMap<String,Object> rsaMap = new HashMap<String,Object>();
		
		try {
			rsaMap.put("rsaPublicKeyModules",rsaArrBean.getPublicKeyModules());
			rsaMap.put("rsaPublicKeyExponent", rsaArrBean.getPublicKeyExponent());
			rsaMap.put("rsaPrivateKey", rsaArrBean.getPrivateKey());
		}catch (Exception e) {
			logger.error("cause : "+ e.getClass()+", line : "+e.getStackTrace()[0].getLineNumber());
		}
		return rsaMap;
	}


	@Override
	public void logInsert(LoginVO vo, String connect, HttpServletRequest req) throws Exception {
		LoginIpLogVO logVo = new LoginIpLogVO();
		
		logVo.setLclCode(connect);
		logVo.setLclIp(req.getRemoteAddr());
		logVo.setLuId(vo.getUserId());
		
		loginBean.logInsert(logVo);
		
	}


	@Override
	public String menuList(LoginVO vo, HttpServletRequest request) throws Exception {
		
		List<MenuVO> list = loginBean.menuList(vo);
		MenuVO menuVo = new MenuVO();
		
		String end = "";
		int upMenuDepth = 1; //이전 depth
		
		StringBuilder sb = new StringBuilder();
		
		for(int i=0; i<list.size(); i++){
			menuVo = list.get(i);
			int depth1 = menuVo.getMenuStep1()/100; 
			int depth2 = menuVo.getMenuStep2()/10;
			int depth3 = menuVo.getMenuStep3();
			
			//depth 1 -------------------------------------------------
			if(depth2 == 0){
				if(i != 0){
					
					if(upMenuDepth == 1) {
						sb.append("</li>");
					} else if(upMenuDepth == 2){
						sb.append("</li></ul></li>");
					} else if(upMenuDepth == 3){
						sb.append("</ul></li></ul></li>");
						
					}
				}
				
				upMenuDepth = 1;
				
				sb.append("<li class='nav-item lnb_"+depth1+"'>");
				sb.append("<a href='"+ request.getContextPath() + menuVo.getMenuUrl()+"?step1="+depth1+"'><i class='"+menuVo.getMenuImgIcon()+"'></i><span class='menu-title' data-i18n='nav.page_layouts.main'>"+menuVo.getMenuName()+"</span></a>");
				
					
			} else{
				
				//depth 2 ----------------------------------------------------
				if(depth3 == 0){
			  		
			  		if(upMenuDepth == 1){
			  			sb.append("<ul class='menu-content'>");
			  		} else if(upMenuDepth == 2){
			  			sb.append("</li>");
			  		} else {
			  			sb.append("</ul></li>");					  			
			  		}
			  		
			  		upMenuDepth = 2;
			  		
			  		sb.append("<li class='nav-item lnb_"+depth1+"_"+depth2+"'><a href='"+request.getContextPath() + menuVo.getMenuUrl()+"?step1="+depth1+"&step2="+depth2+"' data-i18n='nav.navbars.nav_light'>"+menuVo.getMenuName()+"</a>");
					

				//depth 3 ----------------------------------------------------
				} else {
					
					if(upMenuDepth == 2){
						sb.append("<ul class='menu-content'>");
					}
					upMenuDepth = 3;
					sb.append("<li class='lnb_"+depth1+"_"+depth2+"_"+depth3+"'><a class='menu-item' href='"+request.getContextPath() + menuVo.getMenuUrl()+"?step1="+depth1+"&step2="+depth2+"&step3="+depth3+"' data-i18n='nav.navbars.nav_light'>"+menuVo.getMenuName()+"</a></li>");
		  		}
			}
			
			if(i == list.size() -1){
				sb.append(end);
			}
			
		}
		
		return sb.toString();
	}
	
	/*
	 * @Override public String getuserIp (HttpServletRequest req,
	 * HttpServletResponse res) throws Exception {
	 * 
	 * String userIp = req.getHeader("X-Forwarded-For");
	 * 
	 * logger.info(">>>> X-FORWARDED-FOR : " + userIp);
	 * 
	 * if (userIp == null || userIp.length() == 0 ||
	 * "unknown".equalsIgnoreCase(userIp)) { userIp =
	 * req.getHeader("Proxy-Client-IP"); logger.info(">>>> Proxy-Client-IP : " +
	 * userIp); } if (userIp == null || userIp.length() == 0 ||
	 * "unknown".equalsIgnoreCase(userIp)) { userIp =
	 * req.getHeader("WL-Proxy-Client-IP"); // 웹로직
	 * logger.info(">>>> WL-Proxy-Client-IP : " + userIp); } if (userIp == null ||
	 * userIp.length() == 0 || "unknown".equalsIgnoreCase(userIp)) { userIp =
	 * req.getHeader("HTTP_CLIENT_IP"); logger.info(">>>> HTTP_CLIENT_IP : " +
	 * userIp); } if (userIp == null || userIp.length() == 0 ||
	 * "unknown".equalsIgnoreCase(userIp)) { userIp =
	 * req.getHeader("HTTP_X_FORWARDED_FOR");
	 * logger.info(">>>> HTTP_X_FORWARDED_FOR : " + userIp); } if (userIp == null ||
	 * userIp.length() == 0 || "unknown".equalsIgnoreCase(userIp)) { userIp =
	 * req.getRemoteAddr(); }
	 * 
	 * logger.info(">>>> Result : IP Address : " + userIp);
	 * 
	 * String userIpChk = loginBean.userIpChk(userIp);
	 * 
	 * return userIpChk; }
	 */
	
}