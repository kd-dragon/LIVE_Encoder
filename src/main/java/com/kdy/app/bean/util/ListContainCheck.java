package com.kdy.app.bean.util;

import java.util.List;

public class ListContainCheck {

	public static String contain(List<String> list, String status) throws Exception{
		if(list.contains(status)) {
			return "true";
		}
		return "false";
	}
}
