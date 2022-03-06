package com.kdy.live.bean.encoding.command.rtmp.record;

import com.kdy.live.bean.encoding.command.EncodeCommandFactoryIF;
import com.kdy.live.bean.util.code.LiveBroadcastStatus;
import com.kdy.live.dto.LiveSchedMemoryVO;
import com.kdy.live.dto.live.LiveBroadcastVO;

import net.bramp.ffmpeg.builder.FFmpegBuilder;

public class RecordAdaptiveRtmpToHLS implements EncodeCommandFactoryIF {
	
	@Override
	public FFmpegBuilder getCommandBuilder(int port, LiveBroadcastVO liveVO, LiveSchedMemoryVO memoryVO) 
			throws Exception {
		
		FFmpegBuilder builder = null;
		String streamMap = null;
		
		try {
			String listenerUrl = String.format("tcp://127.0.0.1:%s", port);
			int startNum = 0;
			
			if(liveVO.getLbStatus().equals(LiveBroadcastStatus.Restart.getTitle())) {
				startNum = liveVO.getReStartNum();
			}
			
			if(memoryVO.getEncodingType().equalsIgnoreCase("advance")) {
				if(memoryVO.getOsType().equalsIgnoreCase("linux")) {
					streamMap = "v:0,a:0 v:1,a:1 v:2,a:2";
				} else {
					streamMap = "\"v:0,a:0 v:1,a:1 v:2,a:2\"";
				}
				builder = new FFmpegBuilder()
							.overrideOutputFiles(true)
							.addExtraArgs("-rw_timeout", "5000000") // rtmp stream wait time (5s)
							.addExtraArgs("-progress", listenerUrl)
							.addExtraArgs("-loglevel", "warning")
							.setInput(liveVO.getLcUrl())
							.addOutput(liveVO.getHlsFilePath() + "/%v/index.m3u8")
							.addExtraArgs("-timeout", "2000")
								.setPreset("veryfast")
								.setVideoFrameRate(30)
								.addExtraArgs("-g", "30")
								.addExtraArgs("-sc_threshold", "0")
								.addExtraArgs("-map", "0")
								.addExtraArgs("-map", "0")
								.addExtraArgs("-map", "0")
								// high
								//.addExtraArgs("-s:v:0", "1280x720")
								.addExtraArgs("-b:v:0", "6000k")
								.addExtraArgs("-maxrate:v:0", "6600k")
								.addExtraArgs("-bufsize:v:0", "8000k")
								// low
								.addExtraArgs("-s:v:1", "640x360")
								.addExtraArgs("-b:v:1", "2000k")
								.addExtraArgs("-maxrate:v:1", "2200k")
								.addExtraArgs("-bufsize:v:1", "3000k")
								// mid
								.addExtraArgs("-s:v:2", "852x480")
								.addExtraArgs("-b:v:2", "3000k")
								.addExtraArgs("-maxrate:v:2", "3300k")
								.addExtraArgs("-bufsize:v:2", "4000k")
								// common
								.addExtraArgs("-c:v", "h264")
								.addExtraArgs("-c:a", "aac")
								.addExtraArgs("-var_stream_map", streamMap)
								// hls configuration
								.addExtraArgs("-f", "hls")
								.addExtraArgs("-hls_time", "5")
								.addExtraArgs("-hls_list_size", "0")
								.addExtraArgs("-start_number", startNum+"")
								.addExtraArgs("-hls_segment_filename", liveVO.getHlsFilePath() + "/%v/%5d.ts")
								.done();
			} else {
				if(memoryVO.getOsType().equalsIgnoreCase("linux")) {
					streamMap = "v:0,a:0 v:1,a:1";
				} else {
					streamMap = "\"v:0,a:0 v:1,a:1\"";
				}
				builder = new FFmpegBuilder()
						.overrideOutputFiles(true)
						.addExtraArgs("-progress", listenerUrl)
						.addExtraArgs("-loglevel", "warning")
						.setInput(liveVO.getLcUrl())
						.addOutput(liveVO.getHlsFilePath() + "/%v/index.m3u8")
							.setPreset("veryfast")
							.setVideoFrameRate(30)
							.addExtraArgs("-g", "30")
							.addExtraArgs("-sc_threshold", "0")
							//.addExtraArgs("-s:v:0", "1280x720")
							.addExtraArgs("-map", "0")
							.addExtraArgs("-map", "0")
							// high
							//.addExtraArgs("-s:v:0", "1280x720")
							.addExtraArgs("-b:v:0", "6000k")
							.addExtraArgs("-maxrate:v:0", "6600k")
							.addExtraArgs("-bufsize:v:0", "8000k")
							// low
							.addExtraArgs("-s:v:1", "640x360")
							.addExtraArgs("-b:v:1", "2000k")
							.addExtraArgs("-maxrate:v:1", "2200k")
							.addExtraArgs("-bufsize:v:1", "3000k")
							// common
							.addExtraArgs("-c:v", "h264")
							.addExtraArgs("-c:a", "aac")
							.addExtraArgs("-var_stream_map", streamMap)
							// hls configuration
							.addExtraArgs("-f", "hls")
							.addExtraArgs("-hls_time", "5")
							.addExtraArgs("-hls_list_size", "0")
							.addExtraArgs("-start_number", startNum+"")
							.addExtraArgs("-hls_segment_filename", liveVO.getHlsFilePath() + "/%v/%5d.ts")
							.done();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return builder;
	}

}
