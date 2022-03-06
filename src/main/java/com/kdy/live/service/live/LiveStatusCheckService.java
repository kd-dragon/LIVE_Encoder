package com.kdy.live.service.live;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import com.kdy.app.bean.LiveChatSaveBean;
import com.kdy.app.bean.util.LiveFileUtils;
import com.kdy.live.bean.status.LiveBroadcastSelectBean;
import com.kdy.live.bean.status.LiveBroadcastUpdateBean;
import com.kdy.live.bean.util.MacAddressUtil;
import com.kdy.live.bean.util.code.LiveBroadcastStatus;
import com.kdy.live.bean.util.system.ProcessManageFactory;
import com.kdy.live.dto.LiveSchedMemoryVO;
import com.kdy.live.dto.LiveSchedMemoryVO.RedisHashKeyword;
import com.kdy.live.dto.live.LiveBroadcastEvent;
import com.kdy.live.dto.live.LiveBroadcastVO;

@Component
public class LiveStatusCheckService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private final LiveSchedMemoryVO				memoryVO;
	private final LiveBroadcastSelectBean 		selectBean;
	private final LiveBroadcastUpdateBean 		updateBean;
	private final LiveChatSaveBean 				chatSaveBean;
	private final ProcessManageFactory 			processManageFactory;
	private final ApplicationEventPublisher 	applicationEventPublisher;
	private final RedisTemplate<String, Object> redisTemplate;
	
	@Autowired
	public LiveStatusCheckService(	  LiveSchedMemoryVO 			memoryVO
									, LiveBroadcastSelectBean 		selectBean
									, LiveBroadcastUpdateBean 		updateBean
									, LiveChatSaveBean 				chatSaveBean
									, ProcessManageFactory 			processManageFactory
									, ApplicationEventPublisher 	applicationEventPublisher
									, @Qualifier("redisTemplateObject") 
	  								RedisTemplate<String, Object> 		redisTemplate) 
	{
		this.memoryVO 					= memoryVO;
		this.selectBean 				= selectBean;
		this.updateBean 				= updateBean;
		this.chatSaveBean				= chatSaveBean;
		this.processManageFactory 		= processManageFactory;
		this.applicationEventPublisher  = applicationEventPublisher;
		this.redisTemplate 				= redisTemplate;
	}
	
	/*
	 * 실행중인 Live 상태 체크 서비스 입니다.
	 * 
	 * 1. 싱글톤 공유 객체 LiveSchedMemoryVO 의 멤버변수 해시맵 LiveSeqToVO 에서 LB_SEQ를 키로 모든 라이브 객체를 가져옵니다.
	 * 2. 라이브 객체의 상태 값 별로 분기 처리.
	 *    - OnAir, Pause 상태일 경우에는 DB상의 상태값을 재조회하여 상태별로 처리합니다.
	 *    - Error 상태일 경우에는 DB의 재시작 여부를 확인하여 재시작 처리합니다.
	 *    - Finish (방송종료) 요청시에는 LiveFinishCheckService 서비스에서 처리합니다.
	 * 
	 */
	public void service() throws Exception {
		
		Iterator<String> keys = memoryVO.getLiveSeqToVO().keySet().iterator();
		logger.info("## Working Live Queue Size === " + memoryVO.getLiveSeqToVO().keySet().size());
		
		while(keys.hasNext()) {
			String liveBroadcastSeq = keys.next();
			LiveBroadcastVO lbvo = memoryVO.getLiveSeqToVO().get(liveBroadcastSeq);
			if(lbvo != null) {
				
				if(lbvo.getLcUrl().equals("VOD")) {//VOD
					logger.info(String.format("######## LiveBroadcastVO [seq:%s] Status [%s], VOD live (seq:[%s])###### ",lbvo.getLbSeq(), lbvo.getLbStatus(), lbvo.getLbVodSeq()));
					vodLiveStatus(lbvo);
					
				} else {//LIVE
					logger.info(String.format("######## LiveBroadcastVO [seq:%s] Status [%s], LIVE retval [%s] ###### ",lbvo.getLbSeq(), lbvo.getLbStatus(), lbvo.getFfmpegRetval()));
					
					if(lbvo.getLbStatus().equals(LiveBroadcastStatus.OnAir.getTitle())
							|| lbvo.getLbStatus().equals(LiveBroadcastStatus.Pause.getTitle())) {
						switch(selectBean.getLiveStatus(lbvo)){
						case OnAir:
							processOnAir(lbvo); break;
						case Pause:
							processOnPause(lbvo); break;
						case Restart:
							processOnRestart(lbvo); break;
						case Finished:
							processFinished(lbvo); break;
						default:
							break;
						}
					} else if(lbvo.getLbStatus().equals(LiveBroadcastStatus.Error.getTitle())) {
						switch(selectBean.getLiveStatus(lbvo)){
						case Restart:
							processOnRestart(lbvo); break;
						default:
							processError(lbvo);
							break;
						}
					}
				}
				
				
			} else {
				logger.info("###### LiveStatusCheckService LiveBroadcastVO is NULL ########");
			}
		}
		
	}
	
	/*
	 *  [On-Air(Restart) 상태 처리]
	 *  ffmpeg return value 여부 조사
	 *  1. NULL => FFMPEG 라이브 인코딩 진행 상태
	 *  2. True => FFMPEG 라이브 인코딩 정상 종료
	 *  3. False => FFMPEG 라이브 인코딩 비정상 종료 (오류 발생 및 Pid Killed)
	 */
	private void processOnAir(LiveBroadcastVO lbvo) throws Exception {
		lbvo.setLbjDuration(lbvo.getLiveDurationDate() + lbvo.getCurrentDuration()); //시간 세팅
		
		if(lbvo.getFfmpegRetval() == null) {
			logger.info("[Live STATUS] Keep On Air >> " + lbvo.getLbTitle());
			// Duration DB Update
			lbvo.setLbSerialNo(MacAddressUtil.getMacAddress() == null ? memoryVO.getSerialNo() : MacAddressUtil.getMacAddress());
			updateBean.updateLiveBroadcastJob(lbvo);
			
		} else if(lbvo.getFfmpegRetval()) {
			logger.info("[Live STATUS] Live Successfully Finished >> " + lbvo.getLbTitle());
			//라이브 정상종료 -> 종료시간 업데이트
			updateBean.updateLiveEndDate(lbvo.getLbSeq()); 
			
		} else {
			logger.info("[Live STATUS] Live Error Cause >> " + lbvo.getLbTitle());
			lbvo.setLbStatus(LiveBroadcastStatus.Error.getTitle());
			lbvo.setLbjProcessId("0");
			updateBean.statusOnError(lbvo); //status
			updateBean.updateLiveBroadcastJob(lbvo); //job 로그 업데이트
			
			//라이브 총 방송 시간 세팅
			lbvo.setLbjDuration(lbvo.getLiveDurationDate() + lbvo.getCurrentDuration()); //시간 세팅
			lbvo.setLiveDurationDate(lbvo.getLbjDuration());
			lbvo.setLbStatus(LiveBroadcastStatus.Error.getTitle());
			lbvo.setCurrentDuration(0); //초기화
			
			/*
			updateBean.updateLiveBroadcastJob(lbvo); //processId
			updateBean.jobLogMove(lbvo); //종료된 방송 JOB 데이터 JOB_LOG로 이동시키기
			updateBean.jobDataDelete(lbvo); //job 데이터 지우기
			
			//시퀀스:VO 관계 메모리 DB Remove
			releaseLiveSeqToVO(lbvo);
			memoryVO.getFinishedQueue().offer(lbvo);
			*/
		}
	}
	
	/*
	 *  [Pause 상태 처리]
	 *  관리자 패키지에서 일시정지 요청
	 *  1. Object status 상태값 변경 ( OnAir -> Pause)
	 *  2.  FFMPEG Pid Killed
	 */
	private void processOnPause(LiveBroadcastVO lbvo) throws Exception {
		
		// 처음 일시정지 처리시
		if(lbvo.getLbStatus().equals(LiveBroadcastStatus.OnAir.getTitle())) {
			/*
			 *  for문 돌면서 CheckPID 후 KillProcess 처리
			 */
			String pid = lbvo.getLbjProcessId();
			if(processManageFactory.template().checkPID(pid)) {
				processManageFactory.template().killProcess(pid);
			}
			
			//라이브 총 방송 시간 세팅
			lbvo.setLbjDuration(lbvo.getLiveDurationDate() + lbvo.getCurrentDuration()); //시간 세팅
			lbvo.setLiveDurationDate(lbvo.getLbjDuration());
			lbvo.setLbStatus(LiveBroadcastStatus.Pause.getTitle());
			
			// Redis 방송상태 (일시정지 SET)
			ValueOperations<String, Object> valueOperation = redisTemplate.opsForValue();
			String key = RedisHashKeyword.LIVESTATUS.toString() + lbvo.getLbSeq();
			valueOperation.set(key, "3");
			
		// 이미 일시정지 상태 일시 
		} else {
			logger.info("[Paused] This Channel is Waiting For Resume [" + lbvo.getLbTitle() + "]");
		}
		lbvo.setCurrentDuration(0); //초기화
	}
	
	/*
	 *  [Restart 상태 처리]
	 *  관리자 패키지에서 재시작 요청
	 *  1. FFMPEG Retval 초기화
	 *  2. restart-number 설정 (재시작후 새로 시작할 ts파일의 번호)
	 *  3. 라이브 인코딩 요청
	 */
	private void processOnRestart(LiveBroadcastVO lbvo) throws Exception {
		
		lbvo.setFfmpegRetval(null);
		lbvo.setLbStatus(LiveBroadcastStatus.Restart.getTitle());
		lbvo.setReStartNum(LiveFileUtils.getRestartNum(lbvo, memoryVO.getIsAdaptive()));
		
		// Redis 방송상태 (시작 구분값 SET)
		ValueOperations<String, Object> valueOperation = redisTemplate.opsForValue();
		String key = RedisHashKeyword.LIVESTATUS.toString() + lbvo.getLbSeq();
		valueOperation.set(key, "1");
		
		// Live 인코딩 요청
		LiveBroadcastEvent liveEvent = new LiveBroadcastEvent(applicationEventPublisher, lbvo);
		applicationEventPublisher.publishEvent(liveEvent);
	}
	
	
	/**
	 * [Finished 상태 처리]
	 *  방송 종료되었지만 메모리에 남아있는 경우 처리 
	 */
	private void processFinished(LiveBroadcastVO lbvo) throws Exception {
		
		//Zombie FFMPEG 확인
		String pid = lbvo.getLbjProcessId();
		if(processManageFactory.template().checkPID(pid)) {
			processManageFactory.template().killProcess(pid);
		} else {
			logger.error("Expired LiveBroadcast FFMPEG Kill 처리 중 해당하는 PID[{}]가 존재하지 않습니다", pid);
		}
		
		releaseLiveSeqToVO(lbvo);
	}
	
	/**
	 * [Error 상태 처리]
	 *  삭제된 방송인지 확인후 삭제되었으면 Memory에서 지우기
	 */
	private void processError(LiveBroadcastVO lbvo) throws Exception{
		String delYn = selectBean.getLiveDelYn(lbvo);
		
		if(delYn != null && delYn.equalsIgnoreCase("Y")) {
			releaseLiveSeqToVO(lbvo);
		}
	}
	
	
	private void releaseLiveSeqToVO(LiveBroadcastVO lbvo) {
		// ** In-Memory Map release() **
		try {
			memoryVO.getLiveSeqToVO().remove(lbvo.getLbSeq());
		} catch(NullPointerException e) {
			logger.warn("NullPointException() :: LiveSeqToVO [" + lbvo.getLbSeq() + "]");
		}
	}
	
	//VOD로 LIVE 방송시
	private void vodLiveStatus(LiveBroadcastVO lbvo) throws Exception {
		
		//LIVE 종료시
		if(lbvo.getLbStatus().equals(LiveBroadcastStatus.Finished.getTitle())) {
			//라이브 총 방송 시간 세팅
			if(lbvo.getVodStartDate() != 0) {
				lbvo.setCurrentDuration((System.currentTimeMillis()-lbvo.getVodStartDate())/1000); //시작부터 현재까지 시간(초단위)
			} else {
				lbvo.setCurrentDuration(0);
			}
			lbvo.setLbjDuration(lbvo.getCurrentDuration() + lbvo.getVodDuration());
			
			lbvo.setLbStatus(LiveBroadcastStatus.Finished.getTitle());
			lbvo.setLbjProcessId(null);
			updateBean.statusFinished(lbvo); //status 
			updateBean.jobLogMove(lbvo); //종료된 방송 JOB 데이터 JOB_LOG로 이동시키기
			updateBean.jobDataDelete(lbvo); //job 데이터 지우기
			
			releaseLiveSeqToVO(lbvo);
			
			if(lbvo.getLbChatYn() != null && lbvo.getLbChatYn().equalsIgnoreCase("Y")) {
				try {
					chatSaveBean.checkChattingInRedis(lbvo.getLbSeq(), "SCHED");
				} catch(Exception e) {
					logger.error("error : {}", e);
				}
			}
			
			logger.info("[VOD Live FINISH] OFF >>> " + lbvo.getLbTitle());
			
		} else {
			
			switch(selectBean.getLiveStatus(lbvo)){
			case OnAir:
				lbvo.setCurrentDuration((System.currentTimeMillis()-lbvo.getVodStartDate())/1000); //시작/재시작부터 현재까지 시간(초단위)
				lbvo.setLbjDuration(lbvo.getCurrentDuration() + lbvo.getVodDuration());
				
				// current Duration DB Update
				updateBean.updateLiveBroadcastJob(lbvo);
				logger.info("[VOD Live STATUS] Keep On Air >> " + lbvo.getLbTitle());
				break;
				
			case Pause:
				
				if(lbvo.getVodStartDate() != 0 ) {
					String duration = selectBean.selectNowDuration(lbvo.getLbSeq()); //현재 영상 시간 가져오기
					lbvo.setVodDuration(Double.parseDouble(duration));
					//vod 시작시간 초기화
					lbvo.setVodStartDate(0);
				}
				
				logger.info("[VOD Live Paused] This Channel is Waiting For Resume [" + lbvo.getLbTitle() + "]");
				
				break;
				
			case Restart:
				// 라이브 상태 변경 ( 진행중 : 1)
				updateBean.statusOnAir(lbvo);
				
				//시작시간 세팅
				lbvo.setVodStartDate(System.currentTimeMillis());
				
				logger.info("[VOD Live Restart] RE-START ON AIR >> " + lbvo.getLbTitle());
				break;
				
			default:
				break;
			}
			
		}
	}
}
