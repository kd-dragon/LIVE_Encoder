package com.kdy.live.bean.netty.ffmpeg;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kdy.live.bean.util.ProgressParseBean;
import com.kdy.live.bean.util.TimeExpressUtil;
import com.kdy.live.bean.util.code.LiveBroadcastStatus;
import com.kdy.live.dto.LiveSchedMemoryVO;
import com.kdy.live.dto.ffmpeg.ProgressVO;
import com.kdy.live.dto.live.LiveBroadcastVO;
import com.kdy.live.dto.netty.NettyVO;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import net.bramp.ffmpeg.FFmpegUtils;
import net.bramp.ffmpeg.progress.Progress;

@Sharable // 여러 client의 경우 사용
@Component
public class NettyFfmpegHandler extends SimpleChannelInboundHandler<String> {
	
	private Logger logger = LoggerFactory.getLogger("ffmpeg");
	
	private final NettyVO nettyVO;
	
	private final LiveSchedMemoryVO memoryVO;
	
	private final ProgressParseBean parserBean;
	
	@Autowired
	public NettyFfmpegHandler(NettyVO nettyVO, LiveSchedMemoryVO memoryVO, ProgressParseBean parserBean) {
		this.nettyVO = nettyVO;
		this.memoryVO = memoryVO;
		this.parserBean = parserBean;
	}
	
	//연결 되었을때
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info("Client joined - " + ctx);
		ChannelId channelId = ctx.channel().id();
		
		Map<ChannelId, LiveBroadcastVO> relationMap = nettyVO.getFfmpegLiveRelationMap();
		// 큐에서 Vod 정보 poll
		LiveBroadcastVO vo = nettyVO.getLiveInfoQueue().poll();
		// FFmpeg 채널 ID 와 VO 관계매핑
		relationMap.put(channelId, vo);
		
		if(vo == null) {
			logger.error("ChannelId: " + channelId + " ########## Polled Queue :: LiveBroadcastVO IS NULL ########### ");
		} else {
			if(vo.getLbStatus().equals(LiveBroadcastStatus.Recording.getTitle())) {
				nettyVO.getProgressMap().put(channelId.toString(), new ProgressVO());
			}
		}
	}

	// 응답 받을 때
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		
		Map<ChannelId, LiveBroadcastVO> relationMap = nettyVO.getFfmpegLiveRelationMap();
		LiveBroadcastVO vo = relationMap.get(ctx.channel().id());
		
		if(vo != null) {
			String[] progressLine = msg.split("\\n");
			Progress progress = parserBean.service(progressLine);
			
			// 라이브 종료 후 ts concat mp4 요청일 경우 
			if(vo.getLbStatus().equals(LiveBroadcastStatus.Recording.getTitle())) {
				
				ProgressVO pvo = nettyVO.getProgressMap().get(ctx.channel().id().toString());
				
				recordingProcess(vo, pvo, progress);
				
			} else {
				// 일반 라이브 실시간 요청 로그
				logger.debug(String.format("status:%s frame:%d time:%s ms fps:%.0f speed:%.2fx", progress.status, progress.frame, progress.out_time_ns, progress.fps.doubleValue(), progress.speed));
				vo.setCurrentDuration(((double) progress.out_time_ns) / 1000000000);
			}
		}
	}
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		
		// 일정시간 동안 통신이 없을 시 수행
		if (evt instanceof IdleStateEvent) {
			 IdleStateEvent e = (IdleStateEvent) evt;
			 if (e.state() == IdleState.ALL_IDLE) {
				 logger.info("ffmpeg IDLE Process - VO GC Clear");
				 // live Map 비워주기
				 memoryVO.getLiveSeqToVO().clear();
			}
		}
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		ChannelId channelId = ctx.channel().id();
		logger.info("[Channel Id] : " + channelId + " >>> Encoding Job is Finished");
		nettyVO.getFfmpegLiveRelationMap().remove(channelId);
		nettyVO.getProgressMap().remove(channelId.toString());
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error(cause.getMessage());
		for(StackTraceElement st : cause.getStackTrace()) { 
			if(st.toString().startsWith("com.kdy.live")) {
				logger.error(st.toString()); 
			} 
		}
	}
	
	/** 
	 * recording 처리
	 * @param vo
	 * @param pvo
	 * @param progress
	 */
	private void recordingProcess(LiveBroadcastVO vo, ProgressVO pvo, Progress progress) {
		
		double duration_ns = 0.0;
		double accumulated_duration_ns = 0.0;
		
		// 전체파일 처리 (TS -> MP4) 
		if(vo.getIsTotalProcess()) {
			duration_ns = vo.getTotalDuration();
		// 분할 파일 처리 (AVI -> MP4) 사용안함
		} else {
			/*
			duration_ns = vo.getRecordDurations().get(vo.getVodFileNum());
			//누적 duration 계산
			for(int i=0; i<vo.getVodFileNum(); i++) {
				accumulated_duration_ns += vo.getRecordDurations().get(i);
			}
			*/
		}
		// 예상 남은 시간 계산
		progressUpdate(pvo, progress, vo, accumulated_duration_ns);
		// 개별 진행 퍼센트 계산 (현재 진행된 시간/ 전체 시간 * 100)
		long percentage = Math.round((progress.out_time_ns / duration_ns) * 100);
		// 전체 진행 퍼센트 계산
		long totalPercentage = Math.round(((accumulated_duration_ns + (double)progress.out_time_ns) / vo.getTotalDuration()) * 100);
		
		// 진행 과정 로그 찍기
		String logMsg = progressLogMsg(vo, progress, percentage, totalPercentage, TimeExpressUtil.secToHhmmss(((Long)pvo.getEstimatedEndTime()).intValue()));
		
		// 진행 퍼센트 vo에 담기
		vo.setRecordPercent(String.valueOf(totalPercentage));
		
		// VOD HLS Convert MP4 로그
		logger.info(logMsg);
	}
	
	/**
	 * 인코딩 예상 소요시간 계산 함수
	 * @param pvo
	 * @param progress
	 * @param duration_ns
	 */
	private void progressUpdate(ProgressVO pvo, Progress progress, LiveBroadcastVO lbvo, double accumulated_duration_ns) {
		
		int progressCount = pvo.getProgressCount();
		long curSystemTimeMs = System.currentTimeMillis();
		
		// 첫 시작시 초기화
		if(pvo.getCurProgressTimeNs() == 0) {
			if(lbvo.getEstimatedEndTime() != null) {
				try {
					pvo.setEstimatedEndTime(Long.parseLong(lbvo.getEstimatedEndTime()));
				} catch(Exception e) {
					pvo.setEstimatedEndTime(0);
				}
			} else {
				pvo.setEstimatedEndTime(0);
			}
		
		// 2번째 처리부터 이전 데이터와 비교하여 처리
		} else {
			long sumProgressTimeNsGap = pvo.getSumProgressTimeNsGap() + (progress.out_time_ns - pvo.getCurProgressTimeNs());
			double averageProgressTimeNsGap = sumProgressTimeNsGap / (progressCount + 1);
			
			long systemTimeMsGap = curSystemTimeMs - pvo.getCurSystemTimeMs();
			
			long estimatedEndCount = Math.round((lbvo.getTotalDuration() - (pvo.getCurProgressTimeNs() + accumulated_duration_ns)) / averageProgressTimeNsGap);
			long estimatedEndTime = Math.round(estimatedEndCount * ((double) systemTimeMsGap) / 1000);
			
			pvo.setSumProgressTimeNsGap(sumProgressTimeNsGap);
			pvo.setAverageProgressTimeNsGap(averageProgressTimeNsGap);
			pvo.setPrevProgressTimeNs(pvo.getCurProgressTimeNs());
			pvo.setPrevSystemTimeMs(pvo.getCurSystemTimeMs());
			pvo.setSystemTimeMsGap(systemTimeMsGap);
			pvo.setProgressCount(progressCount + 1);
			
			pvo.setEstimatedEndCount(estimatedEndCount);
			pvo.setEstimatedEndTime(estimatedEndTime);
			lbvo.setEstimatedEndTime(estimatedEndTime+"");
		}
		pvo.setCurProgressTimeNs(progress.out_time_ns);
		pvo.setCurSystemTimeMs(curSystemTimeMs);
	}
	
	/**
	 * 인코딩 진행 로그 문자열 생성 함수
	 * @param progress
	 * @param percentage
	 * @param estimatedEndTime
	 * @return
	 */
	private String progressLogMsg(LiveBroadcastVO vo, Progress progress, long percentage, long totalPercentage, String estimatedEndTime) {
		
		String logMsg = String.format(
				"{Recording}: [Total: %d%%] | status:%s | time:%s ms | fps:%.0f | speed:%.2fx | estimated_end_time [%s] |",
				//vo.getVodFileNum()+1,
				//vo.getRecordDurations().size(),
				//percentage,
				totalPercentage,
				progress.status,
				FFmpegUtils.toTimecode(progress.out_time_ns, TimeUnit.NANOSECONDS),
				progress.fps.doubleValue(),
				progress.speed,
				estimatedEndTime
			);
		
		return logMsg;
	}
}
