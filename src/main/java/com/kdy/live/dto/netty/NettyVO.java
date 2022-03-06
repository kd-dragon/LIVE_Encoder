package com.kdy.live.dto.netty;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.kdy.live.dto.ffmpeg.ProgressVO;
import com.kdy.live.dto.live.LiveBroadcastVO;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import lombok.Data;

@Data
@Component
public class NettyVO {
	private ChannelHandlerContext ffmpegListenerCtx = null;
	private ChannelHandlerContext monitorListenerCtx = null;
	private Thread ffmpegListenerThread = null;
	private Thread monitorListenerThread = null;
	
	@Value("${netty.ffmpeg.useYn}")
	private String ffmpegUseYn;
	
	@Value("${netty.ffmpeg.port}")
	private int ffmpegPort;
	
	@Value("${netty.monitor.useYn}")
	private String monitorUseYn;
	
	@Value("${netty.monitor.port}")
	private int monitorPort;
	
	//vod 인코딩 -> netty handler 공유용
	private BlockingQueue<LiveBroadcastVO> liveInfoQueue = new LinkedBlockingQueue<>();
	
	// ffmpeg 채널 ID & Live 정보 관계 Map
	private ConcurrentHashMap<ChannelId, LiveBroadcastVO> ffmpegLiveRelationMap = new ConcurrentHashMap<ChannelId, LiveBroadcastVO>();
	
	//vod 인코딩 예상 소요시간 추출용 Map
	private ConcurrentHashMap<String, ProgressVO> progressMap = new ConcurrentHashMap<String, ProgressVO>();

		
	@Autowired
	private ApplicationContext applicationContext;
	
	public boolean setThread(String type, Thread thread) {
		
		if (type.equals("ffmpeg")) {
			ffmpegListenerThread = thread;
		} else if(type.equals("monitor")) {
			monitorListenerThread = thread;
		}
		return true;
	}
	
	public Thread getThread(String type) {
		
		if (type.equals("ffmpeg")) {
			return ffmpegListenerThread;
		} else if(type.equals("monitor")) {
			return monitorListenerThread;
		}
		return null;
	}
	
	public boolean setChannelHandlerContext(String type, ChannelHandlerContext ctx) {
		if (type.equals("ffmpeg")) {
			ffmpegListenerCtx = ctx;
		} else if(type.equals("monitor")) {
			monitorListenerCtx= ctx;
		}
		return true;
	}
	
	public ChannelHandlerContext getChannelHandlerContext(String type) {
		if (type.equals("ffmpeg")) {
			return ffmpegListenerCtx;
		} else if(type.equals("monitor")) {
			return monitorListenerCtx;
		}
		return null;
	}
}
