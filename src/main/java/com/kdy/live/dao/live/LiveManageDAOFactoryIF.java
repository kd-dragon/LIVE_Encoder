package com.kdy.live.dao.live;

import java.util.List;

import com.kdy.live.dto.live.LiveBroadcastJobVO;
import com.kdy.live.dto.live.LiveBroadcastVO;
import com.kdy.live.dto.system.SystemConfigVO;

public interface LiveManageDAOFactoryIF {
	public LiveBroadcastVO selectByStatusWait(LiveBroadcastVO lbvo) throws Exception;
	
	public List<LiveBroadcastVO> selectByEndDate(String lbSerialNo) throws Exception;
	
	public List<LiveBroadcastVO> selectByStatusOnAir(LiveBroadcastVO lbvo) throws Exception;
	
	public List<LiveBroadcastVO> selectByStopStatusOnAir(LiveBroadcastVO lbvo) throws Exception;
	
	public Boolean updateLiveBroadcast(LiveBroadcastVO lbvo) throws Exception;
	
	public String selectStatusBySeq(LiveBroadcastVO lbvo) throws Exception;
	
	public String selectDelYnBySeq(LiveBroadcastVO lbvo) throws Exception;

	public Boolean updateLiveBroadcastJob(LiveBroadcastVO lbvo) throws Exception;
	
	public Boolean updateLiveSerialNo(LiveBroadcastVO lbvo) throws Exception;
	
	public Boolean jobLogMove(LiveBroadcastVO lbvo) throws Exception;
	
	public Boolean jobDataDelete(LiveBroadcastVO lbvo) throws Exception;
	
	public SystemConfigVO selectSystemConfig() throws Exception;
	
	public LiveBroadcastJobVO selectBroadcastJob(LiveBroadcastVO lbvo) throws Exception;
	
	public String selectNowDuration(String lbSeq) throws Exception;
	
	public Boolean updateLiveEndDate(String lbSeq) throws Exception;

	public List<LiveBroadcastVO> selectByInterruptedBroadcast(String lbSerialNo) throws Exception;
	
	public List<LiveBroadcastVO> selectByInterruptedOthers(String lbSerialNo) throws Exception;

	public Boolean selectBroadcastEnable(String mySerialNo) throws Exception; 
}
