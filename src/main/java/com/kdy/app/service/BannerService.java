package com.kdy.app.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.kdy.app.bean.BannerBean;
import com.kdy.app.bean.util.MultiFileUploadBean;
import com.kdy.app.dto.userHome.BannerDTO;
import com.kdy.app.dto.userHome.BannerVO;
import com.kdy.app.dto.util.FileVO;
import com.kdy.app.service.IF.BannerServiceIF;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BannerService implements BannerServiceIF {

	Logger logger = LoggerFactory.getLogger(BannerService.class);
	
	private final BannerBean bannerBean;

	private final MultiFileUploadBean multiFileUploadBean;
	
	@Override
	public List<BannerVO> getBannerList() throws Exception {
		return bannerBean.getBannerList();
	}

	@Override
	public Boolean modifyBanner(BannerDTO dto) throws Exception {
		return bannerBean.modifyBanner(dto);
	}

	@Override
	public Map<String, Object> uploadBannerFile(BannerDTO dto) throws Exception {
		
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		Boolean rtnVal = true;
		
		//날짜
		String year = new SimpleDateFormat("yyyy").format(new Date());
		String month = new SimpleDateFormat("MM").format(new Date());
		
		List<FileVO> fileList = null;
		
		try {
			if(dto.getBannerImgFile() != null) {
				fileList = multiFileUploadBean.service(dto.getBannerImgFile(), "banner/" + year + "/" + month);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			rtnVal = false;
		}
		rtnMap.put("result", rtnVal);
		rtnMap.put("fileList", fileList);
		
		return rtnMap;
	}

}
