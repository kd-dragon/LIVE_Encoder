package com.kdy.live.bean.status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.kdy.live.bean.util.MacAddressUtil;
import com.kdy.live.bean.util.code.LiveBroadcastStatus;
import com.kdy.live.dao.live.LiveManageDAOFactory;
import com.kdy.live.dto.live.LiveBroadcastVO;

@Component
public class LiveBroadcastUpdateBean {
	
	private Logger logger = LoggerFactory.getLogger(LiveBroadcastUpdateBean.class);
	
	private final LiveManageDAOFactory liveManageDAOFactory;
	
	@Value("${live.serialNo}")
	private String serialNo;
	
	@Autowired
	public LiveBroadcastUpdateBean(LiveManageDAOFactory liveManageDAOFactory) {
		this.liveManageDAOFactory = liveManageDAOFactory;
	}
	
	public void statusOnAir(LiveBroadcastVO lbvo) throws Exception {
		lbvo.setLbStatus(LiveBroadcastStatus.OnAir.getTitle());
		liveManageDAOFactory.getDAO().updateLiveBroadcast(lbvo);
	}
	
	public void statusStart(LiveBroadcastVO lbvo) throws Exception {
		lbvo.setLbStatus(LiveBroadcastStatus.Start.getTitle());
		liveManageDAOFactory.getDAO().updateLiveBroadcast(lbvo);
	}
	
	public void statusFinished(LiveBroadcastVO lbvo) throws Exception {
		lbvo.setLbStatus(LiveBroadcastStatus.Finished.getTitle());
		liveManageDAOFactory.getDAO().updateLiveBroadcast(lbvo);
	}
	
	public void statusOnPause(LiveBroadcastVO lbvo) throws Exception {
		lbvo.setLbStatus(LiveBroadcastStatus.Pause.getTitle());
		liveManageDAOFactory.getDAO().updateLiveBroadcast(lbvo);
	}
	
	public void statusOnRestartWait(LiveBroadcastVO lbvo) throws Exception {
		lbvo.setLbStatus(LiveBroadcastStatus.Restart.getTitle());
		liveManageDAOFactory.getDAO().updateLiveBroadcast(lbvo);
	}
	
	public void statusOnError(LiveBroadcastVO lbvo) throws Exception {
		lbvo.setLbStatus(LiveBroadcastStatus.Error.getTitle());
		liveManageDAOFactory.getDAO().updateLiveBroadcast(lbvo);
	}
	
	public void statusRecording(LiveBroadcastVO lbvo) throws Exception {
		lbvo.setLbStatus(LiveBroadcastStatus.Recording.getTitle());
		liveManageDAOFactory.getDAO().updateLiveBroadcast(lbvo);
	}
	
	public void updateLiveSerialNo(LiveBroadcastVO lbvo) throws Exception {
		lbvo.setLbSerialNo(MacAddressUtil.getMacAddress() == null ? serialNo : MacAddressUtil.getMacAddress());
		liveManageDAOFactory.getDAO().updateLiveSerialNo(lbvo);
	}
	
	//ProcessId update
	public void updateLiveBroadcastJob(LiveBroadcastVO lbvo) throws Exception{
		liveManageDAOFactory.getDAO().updateLiveBroadcastJob(lbvo);
	}
	
	//job -> job_log move
	public void jobLogMove(LiveBroadcastVO lbvo) throws Exception{
		liveManageDAOFactory.getDAO().jobLogMove(lbvo);
	}
	
	//job delete
	public void jobDataDelete(LiveBroadcastVO lbvo) throws Exception{
		liveManageDAOFactory.getDAO().jobDataDelete(lbvo);
	}
	
	//LIVE 종료시간 업데이트
	public void updateLiveEndDate(String lbSeq) throws Exception {
		liveManageDAOFactory.getDAO().updateLiveEndDate(lbSeq);
	}
	
}
