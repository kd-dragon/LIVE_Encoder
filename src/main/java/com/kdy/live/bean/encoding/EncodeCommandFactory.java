package com.kdy.live.bean.encoding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.kdy.live.bean.encoding.command.http.DefaultHttpToVodSave;
import com.kdy.live.bean.encoding.command.rtmp.AdaptiveRtmpToHLS;
import com.kdy.live.bean.encoding.command.rtmp.DefaultRtmpToHLS;
import com.kdy.live.bean.encoding.command.rtmp.record.RecordAdaptiveRtmpToHLS;
import com.kdy.live.bean.encoding.command.rtmp.record.RecordDefaultRtmpToHLS;
import com.kdy.live.bean.encoding.command.rtsp.AdaptiveRtspToHLS;
import com.kdy.live.bean.encoding.command.rtsp.DefaultRtspToHLS;
import com.kdy.live.bean.encoding.command.rtsp.record.RecordAdaptiveRtspToHLS;
import com.kdy.live.bean.encoding.command.rtsp.record.RecordDefaultRtspToHLS;
import com.kdy.live.bean.encoding.command.thumbnail.DefaultThumbnail;
import com.kdy.live.dto.LiveSchedMemoryVO;
import com.kdy.live.dto.live.LiveBroadcastVO;
import com.kdy.live.dto.netty.NettyVO;

import lombok.RequiredArgsConstructor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

@Component
@RequiredArgsConstructor
public class EncodeCommandFactory {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private final NettyVO nettyVO;
	private final LiveSchedMemoryVO memoryVO;
	
	public FFmpegBuilder getSimpleCommand(LiveBroadcastVO liveVO) {
		
		FFmpegBuilder ffmpegBuilder = null;
		
		try {
			// Live Stream URL 정보 가져오기
			String liveChannelUrl = liveVO.getLcUrl();
			// ffmpeg 과 tcp 통신할 내부netty Server 포트 가져오기
			int port = nettyVO.getFfmpegPort();
			
			//rtsp 요청
			if(liveChannelUrl.contains("rtsp://")) {
				//적응형 스트리밍 사용 유무 검사
				if(memoryVO.getIsAdaptive()) {
					logger.debug("getCommand : AdaptiveRtspToHLS Command");
					ffmpegBuilder = new AdaptiveRtspToHLS().getCommandBuilder(port, liveVO, memoryVO);
				} 
				
				logger.debug("getCommand : DefaultRtspToHLS Command");
				ffmpegBuilder = new DefaultRtspToHLS().getCommandBuilder(port, liveVO, memoryVO); 
				
			//rtmp 요청
			} else if(liveChannelUrl.contains("rtmp://")) {
				//적응형 스트리밍 사용 유무 검사
				if(memoryVO.getIsAdaptive()) {
					logger.debug("getCommand : AdaptiveRtmpToHLS Command");
					ffmpegBuilder = new AdaptiveRtmpToHLS().getCommandBuilder(port, liveVO, memoryVO);
				} 
				
				logger.debug("getCommand : DefaultRtmpToHLS Command");
				ffmpegBuilder = new DefaultRtmpToHLS().getCommandBuilder(port, liveVO, memoryVO);
			} 
			
			logger.error("[Fail To Get Command] Non Exists Preset Type");
			
		} catch (Exception e) {
			logger.error("ffmpeg command build error : \n{}", e);
			e.printStackTrace();
		}
		return ffmpegBuilder;
	}
	
	public FFmpegBuilder getCommand(LiveBroadcastVO liveVO) {
		
		FFmpegBuilder ffmpegBuilder = null;
		
		try {
			
			String liveChannelUrl = liveVO.getLcUrl();
			int port = nettyVO.getFfmpegPort();
			
			// live vod 저장시 처리
			if(liveVO.getLbVodSaveYn().equalsIgnoreCase("Y")) {
				
				//rtsp 요청
				if(liveChannelUrl.contains("rtsp://")) {
					//적응형 스트리밍
					if(memoryVO.getIsAdaptive()) {
						//advance : high, low + mid
						logger.info("getCommand : RecordAdaptiveRtspToHLS Command");
						return new RecordAdaptiveRtspToHLS().getCommandBuilder(port, liveVO, memoryVO);
						
					} 
					
					logger.info("getCommand : RecordDefaultRtspToHLS Command");
					return new RecordDefaultRtspToHLS().getCommandBuilder(port, liveVO, memoryVO);
					
				//rtmp 요청
				} else if(liveChannelUrl.contains("rtmp://")) {
					//적응형 스트리밍
					if(memoryVO.getIsAdaptive()) {
						//advance : high, low + mid
						logger.info("getCommand : RecordAdaptiveRtmpToHLS Command");
						return new RecordAdaptiveRtmpToHLS().getCommandBuilder(port, liveVO, memoryVO);
					}
					
					logger.info("getCommand : RecordDefaultRtmpToHLS Command");
					return new RecordDefaultRtmpToHLS().getCommandBuilder(port, liveVO, memoryVO);
					
				} else if(liveChannelUrl.contains("http://") && liveChannelUrl.contains(".m3u8")){
					logger.info("getCommand : DefaultHttpToVodSave Command");
					return new DefaultHttpToVodSave().getCommandBuilder(port, liveVO, memoryVO);
				} 
				
				logger.error("[Fail To Get Command] Non Exist Preset Type");
				return ffmpegBuilder;
			
			} 
			
			// vod 저장 없이 live 송출시 처리
			if(liveChannelUrl.contains("rtsp://")) {
				//적응형 스트리밍 O
				if(memoryVO.getIsAdaptive()) {
					//advance : high, low + mid
					logger.info("getCommand : AdaptiveRtspToHLS Command");
					return new AdaptiveRtspToHLS().getCommandBuilder(port, liveVO, memoryVO);
				} 
				
				logger.info("getCommand : DefaultRtspToHLS Command");
				return new DefaultRtspToHLS().getCommandBuilder(port, liveVO, memoryVO);
				
			//rtmp 요청
			} else if(liveChannelUrl.contains("rtmp://")) {
				//적응형 스트리밍 O
				if(memoryVO.getIsAdaptive()) {
					//advance : high, low + mid
					logger.info("getCommand : AdaptiveRtmpToHLS Command");
					return new AdaptiveRtmpToHLS().getCommandBuilder(port, liveVO, memoryVO);
				}
				
				logger.info("getCommand : DefaultRtmpToHLS Command");
				return new DefaultRtmpToHLS().getCommandBuilder(port, liveVO, memoryVO);
				
			}
			
			logger.error("[Fail To Get Command] Non Exist Preset Type");
				
		} catch (Exception e) {
			logger.error("ffmpeg command build error : \n{}", e);
			e.printStackTrace();
		}
		
		return ffmpegBuilder;
	}
	
	public FFmpegBuilder getCommandThumbnail(LiveBroadcastVO liveVO) {
		logger.info("getCommandThumbnail : DefaultThumbnail Command");
		try {
			return new DefaultThumbnail().getCommandBuilder(nettyVO.getFfmpegPort(), liveVO, memoryVO);
		} catch (Exception e) {
			logger.error("ffmpeg thumbnail build error : \n{}", e);
			e.printStackTrace();
		}
		return null;
	}
	
}
