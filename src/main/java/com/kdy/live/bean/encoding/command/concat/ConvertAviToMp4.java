package com.kdy.live.bean.encoding.command.concat;

import com.kdy.live.bean.encoding.command.FfmpegCommandBuilderIF;

import net.bramp.ffmpeg.builder.FFmpegBuilder;

public class ConvertAviToMp4 implements FfmpegCommandBuilderIF {
	
	private final int port;
	private final String inputFileFullPath;
	
	public ConvertAviToMp4(int port, String inputFileFullPath) {
		this.port = port;
		this.inputFileFullPath = inputFileFullPath;
	}
	
	@Override
	public FFmpegBuilder getBuilder() {
		
		String listenerUrl = String.format("tcp://127.0.0.1:%s", port);

		FFmpegBuilder builder = new FFmpegBuilder()
				.overrideOutputFiles(true)
				.addExtraArgs("-progress", listenerUrl)
				.addExtraArgs("-stats")
				.addExtraArgs("-loglevel", "warning")
				.setInput(inputFileFullPath)
				.addOutput(inputFileFullPath.replace(".avi", ".mp4").replace("\\", "/"))
					.setVideoCodec("libx264")
					.setAudioCodec("aac")
					.done()
				;

		return builder;
	}

}
