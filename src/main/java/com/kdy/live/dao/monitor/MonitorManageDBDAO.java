package com.kdy.live.dao.monitor;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kdy.app.dto.system.StreamingVO;
import com.kdy.live.dto.monitor.LiveMonitorVO;
import com.kdy.live.dto.monitor.LiveViewsDTO;

@Repository
public class MonitorManageDBDAO implements MonitorManageDAOFactoryIF{
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	SqlSessionTemplate sqlSessionTemplate;
	
	//스트리밍 모듈 리스트 가져오기
	@Override
	public List<StreamingVO> selectStreamingList() throws Exception {
		return sqlSessionTemplate.selectList("monitor.selectStreamingList");
	}
	
	@Override
	public int insertStreamingList(List<LiveMonitorVO> insertStreamingList) throws Exception {
		return sqlSessionTemplate.insert("monitor.insertStreamingList", insertStreamingList);
	}
	
	//라이브 방송중 리스트 가져오기
	@Override
	public List<String> onAirList() throws Exception {
		return sqlSessionTemplate.selectList("monitor.onAirList");
	}

	@Override
	public int insertLiveViewsList(LiveViewsDTO dto) throws Exception {
		return sqlSessionTemplate.insert("monitor.insertLiveViewsList", dto);
	}

	@Override
	public int deleteStreamingList() throws Exception {
		return sqlSessionTemplate.delete("monitor.deleteLiveStreamingList");
	}

}
