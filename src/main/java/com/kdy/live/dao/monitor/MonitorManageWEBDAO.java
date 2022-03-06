package com.kdy.live.dao.monitor;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.kdy.app.dto.system.StreamingVO;
import com.kdy.live.bean.util.OkHttpClientPool;
import com.kdy.live.dto.monitor.LiveMonitorVO;
import com.kdy.live.dto.monitor.LiveViewsDTO;

@Repository
public class MonitorManageWEBDAO implements MonitorManageDAOFactoryIF {
	
private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Value("${spring.webUrl}")
	private String url;
	
	@Autowired
	private OkHttpClientPool pool;

	@Override
	public List<StreamingVO> selectStreamingList() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<String> onAirList() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int insertStreamingList(List<LiveMonitorVO> insertStreamingList) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertLiveViewsList(LiveViewsDTO dto) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int deleteStreamingList() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

}
