package com.kdy.live.bean.encoding.command.rtmp;

import com.kdy.live.bean.encoding.command.EncodeCommandFactoryIF;
import com.kdy.live.dto.LiveSchedMemoryVO;
import com.kdy.live.dto.live.LiveBroadcastVO;

import net.bramp.ffmpeg.builder.FFmpegBuilder;

public class DefaultRtmpToHLS implements EncodeCommandFactoryIF {
	
	@Override
	public FFmpegBuilder getCommandBuilder(int port, LiveBroadcastVO liveVO, LiveSchedMemoryVO memoryVO) 
			throws Exception {
		
		FFmpegBuilder builder = null;
		String listenerUrl = String.format("tcp://127.0.0.1:%s", port);
		String videoCodec = "copy";
		String audioCodec = "copy";
		if(memoryVO.getCodecEnabled()) {
			videoCodec = "libx264";
			audioCodec = "aac";
		}

		builder = new FFmpegBuilder()
								.overrideOutputFiles(true)
								.addExtraArgs("-rw_timeout", "5000000") // rtmp stream wait time (5s)
								.addExtraArgs("-progress", listenerUrl)
								.addExtraArgs("-loglevel", "warning")
								.setInput(liveVO.getLcUrl())
								.addOutput(liveVO.getHlsFilePath() + "/index.m3u8")
								.addExtraArgs("-timeout", "2000")
									.setPreset("veryfast")
									.setVideoCodec(videoCodec)
									.setAudioCodec(audioCodec)
									.addExtraArgs("-f", "hls")
									.addExtraArgs("-hls_time", "3")
									.addExtraArgs("-hls_list_size", "15")
									.addExtraArgs("-hls_flags", "delete_segments+append_list")
									.addExtraArgs("-hls_segment_filename", liveVO.getHlsFilePath() + "/%5d.ts")
									.done()			
									;
		
		return builder;
	}

}
