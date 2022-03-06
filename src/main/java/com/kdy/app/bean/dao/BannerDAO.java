package com.kdy.app.bean.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kdy.app.dto.userHome.BannerVO;

@Repository
public class BannerDAO {

	@Autowired
	SqlSessionTemplate sqlSessionTemplate;
	
	public List<BannerVO> getBannerList() throws Exception {
		return sqlSessionTemplate.selectList("app_banner.bannerList");
	}
	
	public int deleteBanner(BannerVO vo) throws Exception {
		return sqlSessionTemplate.update("app_banner.bannerDelete", vo);
	}
	
	public int insertBanner(BannerVO vo) throws Exception {
		return sqlSessionTemplate.insert("app_banner.bannerInsert", vo);
	}
	
	public int updateBanner(BannerVO vo) throws Exception {
		return sqlSessionTemplate.update("app_banner.bannerUpdate", vo);
	}
}
