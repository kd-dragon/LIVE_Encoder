package com.kdy.live.dao.vod;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.kdy.live.bean.util.OkHttpClientPool;
import com.kdy.live.dto.live.LiveBroadcastVO;
import com.kdy.live.dto.vod.VodMetaVO;

@Repository
public class VodManageWEBDAO implements VodManageDAOFactoryIF {
	
private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Value("${spring.webUrl}")
	private String url;
	
	@Autowired
	private OkHttpClientPool pool;

	@Override
	public void insertMcmsLiveVodData(LiveBroadcastVO lbvo) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insertVodThumnail(Map<String, Object> map) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insertVodMeta(VodMetaVO mvo) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insertTgLiveVod(LiveBroadcastVO lbvo) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
