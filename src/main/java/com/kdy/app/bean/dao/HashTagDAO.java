package com.kdy.app.bean.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kdy.app.dto.live.AppBroadcastVO;

@Repository
public class HashTagDAO {
	
	@Autowired
	SqlSessionTemplate sqlSessionTemplate;
	
	public int insertHashTag(AppBroadcastVO vo) throws Exception {
		return sqlSessionTemplate.insert("app_tag.insertHashTag", vo);
	}

	public int deleteHashTag(String lbSeq) throws Exception {
		return sqlSessionTemplate.insert("app_tag.deleteHashTag", lbSeq);
	}
	
}
