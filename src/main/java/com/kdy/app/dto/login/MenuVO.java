package com.kdy.app.dto.login;

import lombok.Getter;
import lombok.ToString;
import lombok.Setter;

@Getter
@Setter
@ToString
public class MenuVO {
	
	private int menuSeq;
	private String menuName;
	private String menuUrl;
	private String menuImgIcon;
	private String useYn;
	private int menuStep1;
	private int menuStep2;
	private int menuStep3;

}
