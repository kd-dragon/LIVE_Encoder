package com.kdy.app.bean.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kdy.app.dto.live.AppBroadcastVO;

@Repository
public class LiveBroadcastFileDAO {
	
	@Autowired
	SqlSessionTemplate sqlSessionTemplate;
	
	//방송 완료 목록 
	public AppBroadcastVO selectFileBySeq(AppBroadcastVO vo) throws Exception{
		return sqlSessionTemplate.selectOne("app_file.selectFileBySeq", vo); 
	}
	
	public int insertBroadcastFile(AppBroadcastVO lbvo) throws Exception {
		return sqlSessionTemplate.insert("app_file.insertBroadcastFile", lbvo);
	}

	public AppBroadcastVO selectFile(String lbSeq) throws Exception {
		return sqlSessionTemplate.selectOne("app_file.selectFile", lbSeq);
	}

	public boolean updateThumbnail(AppBroadcastVO lbvo) throws Exception {
		int rslt = sqlSessionTemplate.insert("app_file.updateThumbnail", lbvo);
		return rslt > 0;
	}

	public boolean updateAttach(AppBroadcastVO lbvo) throws Exception{
		int rslt = sqlSessionTemplate.insert("app_file.updateAttach", lbvo);
		return rslt > 0;
	}

}
