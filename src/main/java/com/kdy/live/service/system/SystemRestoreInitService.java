package com.kdy.live.service.system;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kdy.live.bean.status.LiveBroadcastSelectBean;
import com.kdy.live.bean.status.LiveBroadcastUpdateBean;
import com.kdy.live.bean.util.MacAddressUtil;
import com.kdy.live.bean.util.code.LiveBroadcastStatus;
import com.kdy.live.bean.util.system.ProcessManageFactory;
import com.kdy.live.dto.LiveSchedMemoryVO;
import com.kdy.live.dto.live.LiveBroadcastJobVO;
import com.kdy.live.dto.live.LiveBroadcastVO;

@Component
public class SystemRestoreInitService {
private Logger logger = LoggerFactory.getLogger(SystemRestoreInitService.class);
	
	private final LiveBroadcastSelectBean selectBean;
	private final LiveBroadcastUpdateBean updateBean;
	private final LiveSchedMemoryVO memoryVO;
	private final ProcessManageFactory processManageFactory;
	
	@Autowired
	public SystemRestoreInitService(LiveBroadcastSelectBean selectBean
								, LiveBroadcastUpdateBean updateBean
								, LiveSchedMemoryVO memoryVO
								, ProcessManageFactory processManageFactory)
	{
		this.selectBean = selectBean;
		this.updateBean = updateBean;
		this.memoryVO = memoryVO;
		this.processManageFactory = processManageFactory;
	}
	
	/*
	 *  라이브 비정상 종료 후 재기동시 라이브 오류 처리
	 */
	
	@PostConstruct
	public void init() throws Exception {
		
		logger.info("##### System Restore Initialize #####");
		
		List<LiveBroadcastVO> onAirList = selectBean.onAirChannelBySerialNo();

		if(onAirList != null && onAirList.size() > 0) {
			for(LiveBroadcastVO lbvo: onAirList) {
				
				// 기존에 돌고 있던 FFMPEG 프로세스 종료
				String pid = lbvo.getLbjProcessId();
				if(pid != null && !pid.equals("0") && processManageFactory.template().checkPID(pid)) {
					processManageFactory.template().killProcess(pid);
					logger.warn("AliveCheck > 기존 ffmpeg 프로세스 PID[{}]가 강제 종료되었습니다.", pid);
				} else {
					logger.warn("AliveCheck > 기존 ffmpeg 프로세스 PID[{}]가 존재하지 않습니다.", pid);
				}
				
				if(memoryVO.getLiveSeqToVO().get(lbvo.getLbSeq()) == null) {
					logger.warn("[LIVE Module ERROR] Stopped Module By Force");
					
					LiveBroadcastJobVO lbjvo = selectBean.selectBroadcastJob(lbvo);
					if(lbjvo != null) {
						lbvo.setLbjSeq(lbjvo.getLbjSeq());
						lbvo.setLbjProcessId("0");
//						lbvo.setLbjProcessId(lbjvo.getLbjProcessId());
						lbvo.setLbjDuration(lbjvo.getLbjDuration());
						lbvo.setLbStatus(LiveBroadcastStatus.OnAir.getTitle());
//						lbvo.setLbStatus(LiveBroadcastStatus.Error.getTitle());
//						lbvo.setLbjLogMsg("ERROR");
//						lbvo.setLbjLogDesc("라이브 모듈이 강제 종료되어 방송이 비정상 종료되었습니다.");
//						
//						// 최종 완료 상태 업데이트
//						updateBean.statusOnError(lbvo); //status
//						updateBean.jobLogMove(lbvo); //종료된 방송 JOB 데이터 JOB_LOG로 이동시키기
//						updateBean.jobDataDelete(lbvo); //job 데이터 지우기
//						
//						memoryVO.getFinishedQueue().offer(lbvo);
					
						//MAC 주소
						//lbvo.setLbSerialNo(MacAddressUtil.getLocalMacAddress());
						String macAddr = MacAddressUtil.getMacAddress() == null ? memoryVO.getSerialNo() : MacAddressUtil.getMacAddress();
						logger.info("######### Restore LiveBroadcast MACADDR => {} ########", macAddr);
						lbvo.setLbSerialNo(macAddr);
						
						updateBean.updateLiveBroadcastJob(lbvo);
					}
				}
			}
		}
	}
}
