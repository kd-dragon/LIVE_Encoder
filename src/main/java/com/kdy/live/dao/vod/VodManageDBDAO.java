package com.kdy.live.dao.vod;

import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kdy.live.dto.live.LiveBroadcastVO;
import com.kdy.live.dto.vod.VodMetaVO;

@Repository
public class VodManageDBDAO implements VodManageDAOFactoryIF{
	
private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	SqlSessionTemplate sqlSessionTemplate;
	
	@Override
	public void insertMcmsLiveVodData(LiveBroadcastVO lbvo) throws Exception {
		logger.debug("VodManageDAO insertMcmsLiveVodData()");
		//contents insert
		sqlSessionTemplate.insert("live.insertMcmsContents",lbvo);
		//video insert
		sqlSessionTemplate.insert("live.insertMcmsVideo", lbvo);
	}
	
	@Override
	public void insertVodThumnail(Map<String, Object> map) throws Exception {
		logger.debug("VodManageDAO vodThumbInsert()");
		sqlSessionTemplate.insert("live.insertVodThumnail", map);
	}
	
	@Override
	public void insertVodMeta(VodMetaVO mvo) throws Exception {
		logger.debug("VodManageDAO insertVodMeta()");
		sqlSessionTemplate.insert("live.insertVodMeta", mvo);
	}

	@Override
	public void insertTgLiveVod(LiveBroadcastVO lbvo) throws Exception {
		logger.debug("VodManageDAO insertTgLiveVod()");
		sqlSessionTemplate.insert("live.insertTgLiveVod",lbvo);
	}

}
