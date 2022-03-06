package com.kdy.live.dao.live;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kdy.live.dto.live.LiveBroadcastJobVO;
import com.kdy.live.dto.live.LiveBroadcastVO;
import com.kdy.live.dto.system.SystemConfigVO;

@Repository
public class LiveManageDBDAO implements LiveManageDAOFactoryIF {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	SqlSessionTemplate sqlSessionTemplate;
	
	@Override
	public LiveBroadcastVO selectByStatusWait(LiveBroadcastVO lbvo) throws Exception {
		logger.debug("LiveManageDBDAO selectByStatusWait()");
		return sqlSessionTemplate.selectOne("live.selectByStatusWait", lbvo);
	}
	
	@Override
	public List<LiveBroadcastVO> selectByStatusOnAir(LiveBroadcastVO lbvo) throws Exception {
		logger.debug("LiveManageDBDAO selectByStatusWait()");
		return sqlSessionTemplate.selectList("live.selectByStatusOnAir", lbvo);
	}

	@Override
	public Boolean updateLiveBroadcast(LiveBroadcastVO lbvo) throws Exception {
		logger.debug("LiveManageDBDAO updateLiveBroadcastStatus()");
		int result = sqlSessionTemplate.update("live.updateLiveBroadcast", lbvo);
		return result > 0; 
	}

	@Override
	public String selectStatusBySeq(LiveBroadcastVO lbvo) throws Exception {
		logger.debug("LiveManageDBDAO selectStatusBySeq()");
		return sqlSessionTemplate.selectOne("live.selectStatusBySeq", lbvo);
	}

	@Override
	public String selectDelYnBySeq(LiveBroadcastVO lbvo) throws Exception {
		logger.debug("LiveManageDBDAO selectDelYnBySeq()");
		return sqlSessionTemplate.selectOne("live.selectDelYnBySeq", lbvo);
	}
	
	@Override
	public List<LiveBroadcastVO> selectByEndDate(String lbSerialNo) throws Exception {
		logger.debug("LiveManageDBDAO selectByEndDate()");
		
		return sqlSessionTemplate.selectList("live.selectByEndDate", lbSerialNo);
	}

	//ProcessId update
	@Override
	public Boolean updateLiveBroadcastJob(LiveBroadcastVO lbvo) {
		logger.debug("LiveManageDBDAO broadcastJobUpdate()");
		int result = sqlSessionTemplate.update("live.updateLiveBroadcastJob", lbvo);
		return result > 0;
	}

	//job -> job_log move
	@Override
	public Boolean jobLogMove(LiveBroadcastVO lbvo) throws Exception {
		logger.debug("LiveManageDBDAO jobLogMove()");
		int result = sqlSessionTemplate.insert("live.jobLogMove", lbvo);
		return result > 0;
	}

	//job delete
	@Override
	public Boolean jobDataDelete(LiveBroadcastVO lbvo) throws Exception {
		logger.debug("LiveManageDBDAO jobDataDelete()");
		int result = sqlSessionTemplate.delete("live.jobDataDelete", lbvo);
		return result > 0;
	}

	@Override
	public SystemConfigVO selectSystemConfig() throws Exception {
		logger.debug("LiveManageDBDAO selectSystemConfig()");
		return sqlSessionTemplate.selectOne("cmn.selectSystemConfig");
	}

	@Override
	public LiveBroadcastJobVO selectBroadcastJob(LiveBroadcastVO lbvo) throws Exception {
		logger.debug("LiveManageDBDAO selectBroadcastJob()");
		return sqlSessionTemplate.selectOne("live.selectBroadcastJobBySeq", lbvo);
	}

	@Override
	public Boolean updateLiveSerialNo(LiveBroadcastVO lbvo) throws Exception {
		logger.debug("LiveManageDBDAO updateLiveSerialNo()");
		int result = sqlSessionTemplate.update("live.updateLiveSerialNo", lbvo);
		return result > 0;
	}

	@Override
	public String selectNowDuration(String lbSeq) throws Exception {
		logger.debug("LiveManageDBDAO selectNowDuration()");
		return sqlSessionTemplate.selectOne("live.selectNowDuration", lbSeq);
	}

	@Override
	public Boolean updateLiveEndDate(String lbSeq) throws Exception {
		logger.debug("LiveManageDBDAO updateLiveSerialNo()");
		int result = sqlSessionTemplate.update("live.updateLiveEndDate", lbSeq);
		return result > 0;
		
	}

	@Override
	public List<LiveBroadcastVO> selectByInterruptedBroadcast(String lbSerialNo) throws Exception {
		logger.debug("LiveManageDBDAO selectByInterruptedBroadcast()");
		return sqlSessionTemplate.selectList("live.selectByInterruptedBroadcast", lbSerialNo);
	}

	@Override
	public List<LiveBroadcastVO> selectByInterruptedOthers(String lbSerialNo) throws Exception {
		logger.debug("LiveManageDBDAO selectByInterruptedOthers()");
		return sqlSessionTemplate.selectList("live.selectByInterruptedOthers", lbSerialNo);
	}

	//중단 데이터 가져오기
	@Override
	public List<LiveBroadcastVO> selectByStopStatusOnAir(LiveBroadcastVO lbvo) throws Exception {
		logger.debug("LiveManageDBDAO selectByStopStatusOnAir()");
		return sqlSessionTemplate.selectList("live.selectByStopStatusOnAir", lbvo);
	}

	@Override
	public Boolean selectBroadcastEnable(String mySerialNo) throws Exception {
		logger.debug("LiveManageDBDAO selectBroadcastEnable()");
		return (Integer) sqlSessionTemplate.selectOne("live.selectBroadcastEnable", mySerialNo) > 0;
	}

}
