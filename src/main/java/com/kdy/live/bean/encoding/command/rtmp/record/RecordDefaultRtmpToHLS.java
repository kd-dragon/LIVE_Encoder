package com.kdy.live.bean.encoding.command.rtmp.record;

import com.kdy.live.bean.encoding.command.EncodeCommandFactoryIF;
import com.kdy.live.bean.util.code.LiveBroadcastStatus;
import com.kdy.live.dto.LiveSchedMemoryVO;
import com.kdy.live.dto.live.LiveBroadcastVO;

import net.bramp.ffmpeg.builder.FFmpegBuilder;

public class RecordDefaultRtmpToHLS implements EncodeCommandFactoryIF {

	
	@Override
	public FFmpegBuilder getCommandBuilder(int port, LiveBroadcastVO liveVO, LiveSchedMemoryVO memoryVO) 
			throws Exception {
		
		String listenerUrl = String.format("tcp://127.0.0.1:%s", port);
		String videoCodec = "copy";
		String audioCodec = "copy";
		if(memoryVO.getCodecEnabled()) {
			videoCodec = "libx264";
			audioCodec = "aac";
		}
		
		int startNum = 0;
		if(liveVO.getLbStatus().equals(LiveBroadcastStatus.Restart.getTitle())) {
			startNum = liveVO.getReStartNum();
		}

		FFmpegBuilder builder = new FFmpegBuilder()
								.overrideOutputFiles(true)
								.addExtraArgs("-rw_timeout", "5000000") // rtmp stream wait time (5s)
								.addExtraArgs("-progress", listenerUrl)
								.addExtraArgs("-loglevel", "warning")
								.setInput(liveVO.getLcUrl())
								.addOutput(liveVO.getHlsFilePath() + "/index.m3u8")
								.addExtraArgs("-timeout", "2000")
									.setPreset("veryfast")
									//.setVideoFrameRate(30)
									//.addExtraArgs("-g", "30")
									.setVideoCodec(videoCodec)
									.setAudioCodec(audioCodec)
									.addExtraArgs("-f", "hls")
									.addExtraArgs("-hls_time", "5")
									.addExtraArgs("-hls_list_size", "0")
									.addExtraArgs("-start_number", startNum+"")
									.addExtraArgs("-hls_segment_filename", liveVO.getHlsFilePath() + "/%5d.ts")
									.done();
		
		return builder;
	}

}
