package com.kdy.app.dto.userHome;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class BannerDTO {

	private BannerVO banner1;
	private BannerVO banner2;
	private BannerVO banner3;
	private BannerVO banner4;
	private BannerVO banner5;
	
	private List<BannerVO> bannerList;
	private List<MultipartFile> bannerImgFile;
	
	private String userId;
}
