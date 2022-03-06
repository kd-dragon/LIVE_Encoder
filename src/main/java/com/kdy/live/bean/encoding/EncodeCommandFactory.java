package com.kdy.live.bean.encoding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import net.bramp.ffmpeg.builder.FFmpegBuilder;

@Component
public class EncodeCommandFactory {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private final NettyVO nettyVO;
	
	private final LiveSchedMemoryVO memoryVO;
	
	@Autowired
	public EncodeCommandFactory(  NettyVO 			nettyVO
								, LiveSchedMemoryVO memoryVO
	) {
		this.nettyVO  = nettyVO;
		this.memoryVO = memoryVO;
	}
	
	public FFmpegBuilder getCommand(LiveBroadcastVO liveVO) {
		
		try {
			
			String liveChannelUrl = liveVO.getLcUrl();
			int port = nettyVO.getFfmpegPort();
			
			// live vod 저장시 처리
			if(liveVO.getLbVodSaveYn().equalsIgnoreCase("Y")) {
				
				//rtsp 요청
				if(liveChannelUrl.contains("rtsp://")) {
					//적응형 스트리밍 O
					if(memoryVO.getIsAdaptive()) {
						//advance : high, low + mid
						logger.info("getCommand : RecordAdaptiveRtspToHLS Command");
						return new RecordAdaptiveRtspToHLS().getCommandBuilder(port, liveVO, memoryVO);
						
					//적응형 스트리밍 X
					} else {
						logger.info("getCommand : RecordDefaultRtspToHLS Command");
						return new RecordDefaultRtspToHLS().getCommandBuilder(port, liveVO, memoryVO);
					}
					
				//rtmp 요청
				} else if(liveChannelUrl.contains("rtmp://")) {
					//적응형 스트리밍 O
					if(memoryVO.getIsAdaptive()) {
						//advance : high, low + mid
						logger.info("getCommand : RecordAdaptiveRtmpToHLS Command");
						return new RecordAdaptiveRtmpToHLS().getCommandBuilder(port, liveVO, memoryVO);
						
					//적응형 스트리밍 X
					} else {
						logger.info("getCommand : RecordDefaultRtmpToHLS Command");
						return new RecordDefaultRtmpToHLS().getCommandBuilder(port, liveVO, memoryVO);
					}
					
				} else if(liveChannelUrl.contains("http://") && liveChannelUrl.contains(".m3u8")){
					logger.info("getCommand : DefaultHttpToVodSave Command");
					return new DefaultHttpToVodSave().getCommandBuilder(port, liveVO, memoryVO);
				} else {
					logger.error("[Fail To Get Command] Non Exist Preset Type");
					return null;
				}
			
			// vod 저장 없이 live 송출시 처리
			} else {
				if(liveChannelUrl.contains("rtsp://")) {
					//적응형 스트리밍 O
					if(memoryVO.getIsAdaptive()) {
						//advance : high, low + mid
						logger.info("getCommand : AdaptiveRtspToHLS Command");
						return new AdaptiveRtspToHLS().getCommandBuilder(port, liveVO, memoryVO);
						
					//적응형 스트리밍 X
					} else {
						logger.info("getCommand : DefaultRtspToHLS Command");
						return new DefaultRtspToHLS().getCommandBuilder(port, liveVO, memoryVO);
					}
					
				//rtmp 요청
				} else if(liveChannelUrl.contains("rtmp://")) {
					//적응형 스트리밍 O
					if(memoryVO.getIsAdaptive()) {
						//advance : high, low + mid
						logger.info("getCommand : AdaptiveRtmpToHLS Command");
						return new AdaptiveRtmpToHLS().getCommandBuilder(port, liveVO, memoryVO);
						
					//적응형 스트리밍 X
					} else {
						logger.info("getCommand : DefaultRtmpToHLS Command");
						return new DefaultRtmpToHLS().getCommandBuilder(port, liveVO, memoryVO);
					}
					
				} else {
					logger.error("[Fail To Get Command] Non Exist Preset Type");
					return null;
				}
				
			}
		} catch (Exception e) {
			logger.error("ffmpeg command build error : \n{}", e);
			e.printStackTrace();
		}
		return null;
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
