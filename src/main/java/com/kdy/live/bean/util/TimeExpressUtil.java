package com.kdy.live.bean.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeExpressUtil {
	private static Logger logger = LoggerFactory.getLogger(TimeExpressUtil.class);
	
	/**
	 * 초를 시/분/초 표현으로 리턴
	 * @param sec
	 * @return
	 */
	public static String secToHhmmss(int sec) {
		String rtnStr = "";
		
		int hour;
		int min;
		
		min = sec / 60;
		hour = min / 60;
		sec = sec % 60;
		min = min % 60;
		
		if(hour != 0) { 
			rtnStr += hour + ":";
		} 
		
		if(min != 0) {
			rtnStr += min + ":";
		}
		
		rtnStr += sec;
		
		return rtnStr;
	}
	
	/**
	 * 초를 시/분/초 표현으로 리턴
	 * @param sec
	 * @return
	 */
	public static String secToHhmmss(int sec, String split) {
		String rtnStr = "";
		
		int hour;
		int min;
		
		min = sec / 60;
		hour = min / 60;
		sec = sec % 60;
		min = min % 60;
		
		if(hour != 0) { 
			rtnStr += hour + split;
		} 
		
		if(min != 0) {
			rtnStr += min + split;
		}
		
		rtnStr += sec;
		
		return rtnStr;
	}
	
	public static String secToHhmmss(int sec, String hourStr, String minStr, String secStr) {
		String rtnStr = "";
		
		int hour;
		int min;
		
		min = sec / 60;
		hour = min / 60;
		sec = sec % 60;
		min = min % 60;
		
		if(hour != 0) { 
			rtnStr += hour + hourStr;
		} 
		
		if(min != 0) {
			rtnStr += min + minStr;
		}
		
		rtnStr += sec + secStr;
		
		return rtnStr;
	}
	
	/**
	 * 초를 분/초 표현으로 리턴
	 * @param sec
	 * @return
	 */
	public static String secToMmss(int sec) {
		String rtnStr = "";
		
		int min;
		
		min = sec / 60;
		sec = sec % 60;
		
		if(min != 0) {
			rtnStr += min + ":";
		}
		
		rtnStr += sec;
		
		return rtnStr;
	}
	
	public static String secToMmss(int sec, String minStr, String secStr) {
		String rtnStr = "";
		
		int min;
		
		min = sec / 60;
		sec = sec % 60;
		
		if(min != 0) {
			rtnStr += min + minStr;
		}
		
		rtnStr += sec + secStr;
		
		return rtnStr;
	}

}
