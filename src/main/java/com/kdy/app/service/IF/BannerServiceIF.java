package com.kdy.app.service.IF;

import java.util.List;

import java.util.Map;
import com.kdy.app.dto.userHome.BannerDTO;
import com.kdy.app.dto.userHome.BannerVO;

public interface BannerServiceIF {

	public List<BannerVO> getBannerList() throws Exception;
	
	public Boolean modifyBanner(BannerDTO dto) throws Exception;
	
	public Map<String, Object> uploadBannerFile(BannerDTO dto) throws Exception;                                  
}
