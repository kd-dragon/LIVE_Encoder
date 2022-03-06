package com.kdy.app.bean.util;

public class MultiSlashChk {

	//uri 이중 슬래시 제거
	public static String uri(String url) throws Exception {
		
		String protocal = "";
		String path     = "";
		String param    = "";
		
		protocal = url.split("://")[0];
		
		//파라미터 있을 경우
		if(path.contains("\\?")) {
			path = url.split("://")[1].split("\\?")[0];
			param = url.split("://")[1].split("\\?")[1];
			return protocal + "://" + path.replaceAll("/{2,}", "/") + "?" + param;
		
		//파라미터 없을 경우
		}else {
			path = url.split("://")[1];
			return protocal + "://" + path.replaceAll("/{2,}", "/");
			
		}
		
	}
	
	//경로 이중 슬래시 제거
	public static String path(String path) throws Exception{
		return path.replaceAll("/{2,}", "/");
	}

}
