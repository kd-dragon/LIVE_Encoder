package com.kdy.app.dto.userHome;

import lombok.Data;

@Data
public class BannerVO {
	
	private String bannerSeq;
	private String bannerImgFilePath;
	private String bannerImgFileWebPath;
	private String bannerImgFileName;
	private String bannerImgFileNameServer;
	private String bannerShortcutUrl;
	private String bannerOrder;
	private String regUserId;
	private String regDate;
	private String delYn;
	private String delUserId;
	private String delDate;
	private String modUserId;
	private String modDate;
}
