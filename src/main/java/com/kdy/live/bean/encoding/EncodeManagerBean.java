package com.kdy.live.bean.encoding;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList;
import com.kdy.live.bean.status.LiveBroadcastUpdateBean;
import com.kdy.live.bean.util.RecordingFileHandler;
import com.kdy.live.bean.util.RunConsoleRunnable;
import com.kdy.live.bean.util.annotation.StopWatch;
import com.kdy.live.bean.util.code.LiveBroadcastStatus;
import com.kdy.live.dto.LiveSchedMemoryVO;
import com.kdy.live.dto.live.LiveBroadcastVO;
import com.kdy.live.dto.netty.NettyVO;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFmpegUtils;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.progress.Progress;
import net.bramp.ffmpeg.progress.ProgressListener;

@Component
public class EncodeManagerBean {
	private Logger logger = LoggerFactory.getLogger("ffmpeg");
	
	private final EncodeCommandFactory commandFactory;
	
	private final LiveBroadcastUpdateBean updateBean;
	
	private final RecordingFileHandler recordHandler;
	
	private final LiveSchedMemoryVO memoryVO;
	
	private final NettyVO nettyVO;
	
	@Autowired
	public EncodeManagerBean( EncodeCommandFactory 		commandFactory
							, LiveBroadcastUpdateBean 	updateBean
							, RecordingFileHandler 		recordHandler
							, LiveSchedMemoryVO 		memoryVO
							, NettyVO 					nettyVO
	) {
		this.commandFactory = commandFactory;
		this.updateBean 	= updateBean;
		this.recordHandler 	= recordHandler;
		this.memoryVO		= memoryVO;
		this.nettyVO 		= nettyVO;
	}
	
	@StopWatch
	public boolean executeLiveEncodeTCP(LiveBroadcastVO lbvo) {
		boolean rslt = true;
		
		FFmpegBuilder builder = null;
		List<String> cmdList = null;
		
		try {
		
			//ffmpeg command 빌드 
			builder = commandFactory.getCommand(lbvo);
			nettyVO.getLiveInfoQueue().offer(lbvo);
			
			//ffmpeg command List에 담기
			if(builder != null) {
				cmdList = ImmutableList.<String>builder().add(memoryVO.getFfmpegPath()).addAll(builder.build()).build();
				StringBuilder strbuild = new StringBuilder();
				for(String str: cmdList) {
					strbuild.append(str);
					strbuild.append(" ");
				}
				logger.info(strbuild.toString());
				
				//ffmpeg 실행
				RunConsoleRunnable runConsole = new RunConsoleRunnable(cmdList, "liveStreaming");
				
				//ffmpeg Pid 호출
				long pid = runConsole.getProcessID();
				logger.info(String.format("liveBroadcastSeq [%s] - ffmpeg Process ID [%d]", lbvo.getLbSeq(), pid));
				
				
				if(runConsole != null) {
					
					lbvo.setLbjProcessId(pid + "");
					
					// 라이브 상태 변경 ( 진행중 : 1)
					updateBean.statusOnAir(lbvo);
					
					//ffmpeg processId Update
					updateBean.updateLiveBroadcastJob(lbvo);
					
					//ffmpeg log 파일 생성
					recordHandler.recordFfmpegLog(runConsole, lbvo.getLbSeq());
					
					//blocking until ffmpeg job is finished
					int retval = runConsole.waitFor();
					
					/*
					 * # ffmpeg waitFor() return code 
					 * 0: 정상
					 * else: 비정상
					 * --
					 * ffmpeg 강제종료시에도 ffmpeg 입장에서는 오류. 
					 * 따라서 Live 상태값을 조건으로 하여 구분
					 * --
					 */
					if(retval == 0) {
						lbvo.setLbjLogMsg("WARNING");
						lbvo.setLbjLogDesc("Broadcast Suddenly has been Finished");
						logger.info(lbvo.getLbjLogDesc());
						rslt = false;
					} else if(lbvo.getLbStatus().equals(LiveBroadcastStatus.Finished.getTitle())) {
						lbvo.setLbjLogMsg("SUCCESS");
						lbvo.setLbjLogDesc("Broadcast Successfully has been Finished");
						logger.info(lbvo.getLbjLogDesc());
					} else if(lbvo.getLbStatus().equals(LiveBroadcastStatus.Pause.getTitle())) {
						lbvo.setLbjLogMsg("SUCCESS");
						lbvo.setLbjLogDesc("Broadcast Successfully has been Paused");
						logger.info(lbvo.getLbjLogDesc());
					} else {
						lbvo.setLbjLogMsg("WARNING");
						lbvo.setLbjLogDesc("Broadcast Encoder has caused an Error");
						logger.info(lbvo.getLbjLogDesc());
						rslt = false;
					}
				}
				logger.info("============================== RunConsole Finished =================================");
				
			} else {
				lbvo.setLbjLogMsg("ERROR");
				lbvo.setLbjLogDesc("ffmpeg Command Build Error");
				logger.error("[ERROR] ffmpeg Command Build Error :: " + lbvo.getLcUrl());
				rslt = false;
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			rslt = false;
			logger.error(e.getMessage());
			
			lbvo.setLbjLogMsg("ERROR");
			lbvo.setLbjLogDesc(e.getMessage());
			
			for(StackTraceElement st : e.getStackTrace()) {
				if(st.toString().startsWith("com.kdy.live")) {
					logger.error(st.toString());
				}
			}
		}
		
		logger.info("$$$$$$$$$$$$$$ FFMPEG RETURN Boolean : {} $$$$$$$$$$$$$$$$$$$$", rslt);
		
		
		return rslt;
	}
	
	
	public boolean encodeThumbnailSource(LiveBroadcastVO vo) {
		boolean rslt = true;
		
		FFmpeg ffmpeg = memoryVO.getFfmpeg();
		FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, memoryVO.getFfprobe());
		FFmpegBuilder builder = commandFactory.getCommandThumbnail(vo);
	
		FFmpegJob job = executor.createJob(builder, configProgressListener(vo));
		
		job.run();
		
		return rslt;
	}
	
	private ProgressListener configProgressListener(LiveBroadcastVO vo) {
		
		double duration_ns = vo.getLbjDuration() * TimeUnit.SECONDS.toNanos(1);
		
		ProgressListener pl = new ProgressListener() {
			
			@Override
			public void progress(Progress progress) {
				long outTimeNs = progress.out_time_ns;
				long percentage = Math.round(outTimeNs / duration_ns) * 100;
				
				//logger.debug("Current Encoding Complete Time : " + outTimeNs);
				//logger.debug("Current Encoding Complete Percent : " + percentage + "%");
				logger.info(String.format(
						"[%d%%] status:%s frame:%d time:%s ms fps:%.0f speed:%.2fx",
						percentage,
						progress.status,
						progress.frame,
						FFmpegUtils.toTimecode(progress.out_time_ns, TimeUnit.NANOSECONDS),
						progress.fps.doubleValue(),
						progress.speed
					));
				
				
			}
		};
		
		return pl;
	}
}
