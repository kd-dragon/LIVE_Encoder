package com.kdy.live.bean.encoding.command.http;

import com.kdy.live.bean.encoding.command.EncodeCommandFactoryIF;
import com.kdy.live.dto.LiveSchedMemoryVO;
import com.kdy.live.dto.live.LiveBroadcastVO;

import net.bramp.ffmpeg.builder.FFmpegBuilder;

public class DefaultHttpToVodSave implements EncodeCommandFactoryIF {
	
	@Override
	public FFmpegBuilder getCommandBuilder(int port, LiveBroadcastVO liveVO, LiveSchedMemoryVO memoryVO) 
			throws Exception {
		
		FFmpegBuilder builder = new FFmpegBuilder()
				.overrideOutputFiles(true)
				.addExtraArgs("-loglevel", "warning")
				.setInput(liveVO.getLcUrl())
				.addOutput(liveVO.getHlsFilePath() + "/index.m3u8")
				//.setVideoFilter("scale="+liveVO.getPresetData())
				.setVideoCodec("libx264")
				.addExtraArgs("-vsync", "1")
				.setVideoFrameRate(30)
				.addExtraArgs("-g", "60")
				.setAudioCodec("aac")
				.addExtraArgs("-async", "1")
				.setStrict(FFmpegBuilder.Strict.EXPERIMENTAL)
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
