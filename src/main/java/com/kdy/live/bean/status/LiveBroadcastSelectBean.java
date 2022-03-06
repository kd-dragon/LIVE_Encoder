package com.kdy.live.bean.status;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.kdy.live.bean.util.MacAddressUtil;
import com.kdy.live.bean.util.code.LiveBroadcastStatus;
import com.kdy.live.dao.live.LiveManageDAOFactory;
import com.kdy.live.dto.live.LiveBroadcastJobVO;
import com.kdy.live.dto.live.LiveBroadcastVO;

@Component
public class LiveBroadcastSelectBean {
	
	private Logger logger = LoggerFactory.getLogger(LiveBroadcastSelectBean.class);
	
	@Value("${live.serialNo}")
	private String serialNo;
	
	private final LiveManageDAOFactory liveManageDAOFactory;
	
	@Autowired
	public LiveBroadcastSelectBean(LiveManageDAOFactory liveManageDAOFactory) {
		this.liveManageDAOFactory = liveManageDAOFactory;
	}
	
	public Boolean selectBroadcastEnable(String mySerialNo) throws Exception {
		return liveManageDAOFactory.getDAO().selectBroadcastEnable(mySerialNo);
	}
	
	public LiveBroadcastVO onWaitingChannel() throws Exception {
		
		LiveBroadcastVO lbvo = new LiveBroadcastVO();
		lbvo.setLbStatus(LiveBroadcastStatus.Wait.getTitle());
		lbvo.setRowNum(1);
		
		lbvo = liveManageDAOFactory.getDAO().selectByStatusWait(lbvo);
		return lbvo;
	}
	
	public List<LiveBroadcastVO> onAirChannel() throws Exception {
		
		LiveBroadcastVO lbvo = new LiveBroadcastVO();
		lbvo.setLbStatus(LiveBroadcastStatus.OnAir.getTitle());
		
		List<LiveBroadcastVO> list = liveManageDAOFactory.getDAO().selectByStatusOnAir(lbvo);
		return list;
	}
	
	public LiveBroadcastStatus getLiveStatus(LiveBroadcastVO lbvo) throws Exception {
		String status = liveManageDAOFactory.getDAO().selectStatusBySeq(lbvo);
		if(status.equals(LiveBroadcastStatus.OnAir.getTitle())) {
			return LiveBroadcastStatus.OnAir;
		} else if(status.equals(LiveBroadcastStatus.Wait.getTitle())) {
			return LiveBroadcastStatus.Wait;
		} else if(status.equals(LiveBroadcastStatus.Finished.getTitle())) {
			return LiveBroadcastStatus.Finished;
		} else if(status.equals(LiveBroadcastStatus.Pause.getTitle())) {
			return LiveBroadcastStatus.Pause;
		} else if(status.equals(LiveBroadcastStatus.Restart.getTitle())) {
			return LiveBroadcastStatus.Restart;
		} else if(status.equals(LiveBroadcastStatus.Error.getTitle())) {
			return LiveBroadcastStatus.Error;
		} else {
			logger.error("getLiveStatus FAIL ::: status [" + status + "]");
			return null;
		}
	}
	
	public String getLiveDelYn(LiveBroadcastVO lbvo) throws Exception{
		return liveManageDAOFactory.getDAO().selectDelYnBySeq(lbvo);
	}
	
	public List<LiveBroadcastVO> expiredChannel(String lbSerialNo) throws Exception {
		return liveManageDAOFactory.getDAO().selectByEndDate(lbSerialNo);
	}
	
	public LiveBroadcastJobVO selectBroadcastJob(LiveBroadcastVO lbvo) throws Exception {
		return liveManageDAOFactory.getDAO().selectBroadcastJob(lbvo);
	}
	
	public List<LiveBroadcastVO> onAirChannelBySerialNo() throws Exception {
		
		LiveBroadcastVO lbvo = new LiveBroadcastVO();
		lbvo.setLbStatus(LiveBroadcastStatus.OnAir.getTitle());
		
		String macAddr = MacAddressUtil.getMacAddress() == null ? serialNo : MacAddressUtil.getMacAddress();
		lbvo.setLbSerialNo(macAddr);
		List<LiveBroadcastVO> list = liveManageDAOFactory.getDAO().selectByStopStatusOnAir(lbvo);
		return list;
	}
	
	//현재 영상 시간 가져오기
	public String selectNowDuration(String lbSeq) throws Exception{
		return liveManageDAOFactory.getDAO().selectNowDuration(lbSeq);
	}
	
	public List<LiveBroadcastVO> selectByInterruptedBroadcast(String lbSerialNo) throws Exception {
		return liveManageDAOFactory.getDAO().selectByInterruptedBroadcast(lbSerialNo);
	}
	
	public List<LiveBroadcastVO> selectByInterruptedOthers(String lbSerialNo) throws Exception {
		return liveManageDAOFactory.getDAO().selectByInterruptedOthers(lbSerialNo);
	}

}
