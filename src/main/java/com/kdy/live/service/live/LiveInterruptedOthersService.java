package com.kdy.live.service.live;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.kdy.live.bean.status.LiveBroadcastSelectBean;
import com.kdy.live.bean.status.LiveBroadcastUpdateBean;
import com.kdy.live.bean.util.MacAddressUtil;
import com.kdy.live.bean.util.system.ProcessManageFactory;
import com.kdy.live.dto.live.LiveBroadcastVO;

@Component
public class LiveInterruptedOthersService {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private final LiveBroadcastSelectBean selectBean;
	private final LiveBroadcastUpdateBean updateBean;
	private final ProcessManageFactory processManageFactory;
	
	@Value("${live.serialNo}")
	private String serialNo;
	
	@Autowired
	public LiveInterruptedOthersService( LiveBroadcastSelectBean selectBean
									   , LiveBroadcastUpdateBean updateBean
									   , ProcessManageFactory processManageFactory) 
	{
		this.selectBean = selectBean;
		this.updateBean = updateBean;
		this.processManageFactory = processManageFactory;
	}
	
	public void service() throws Exception {
		
		List<LiveBroadcastVO> othersList = selectBean.selectByInterruptedOthers(MacAddressUtil.getMacAddress() == null ? serialNo : MacAddressUtil.getMacAddress());
	
		if(othersList != null && othersList.size() > 0) {
			for(LiveBroadcastVO lbvo : othersList) {
				
				//기존에 돌고 있던 FFMPEG 프로세스 종료
				String pid = lbvo.getLbjProcessId();
				if(pid != null && !pid.equals("0") && processManageFactory.template().checkPID(pid)) {
					processManageFactory.template().killProcess(pid);
					logger.warn("InterruptedOthers PID > 기존 ffmpeg 프로세스 PID[{}]가 강제 종료되었습니다.", pid);
				} else {
					logger.warn("InterruptedOthers PID > 기존 ffmpeg 프로세스 PID[{}]가 존재하지 않습니다.", pid);
				}
				
				//serialNo update
				updateBean.updateLiveSerialNo(lbvo);
				
				//processId 초기화
				logger.warn("$$$$$$$$$ lbj process Id 초기화 $$$$$$$$$");
				lbvo.setLbjProcessId("0");
				updateBean.updateLiveBroadcastJob(lbvo);
			}
		}
	}
}
