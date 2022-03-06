package com.kdy.live.bean.encoding.command.concat;

import com.kdy.live.bean.encoding.command.FfmpegCommandBuilderIF;

import net.bramp.ffmpeg.builder.FFmpegBuilder;

public class ConcatVod implements FfmpegCommandBuilderIF {
	
	private	final int port;
	private final String inputFileList;
	private final String outputFileFullPath;
	
	public ConcatVod(int port, String inputFileList, String outputFileFullPath) {
		this.port = port;
		this.inputFileList = inputFileList;
		this.outputFileFullPath = outputFileFullPath;
	}
	
	@Override
	public FFmpegBuilder getBuilder() {
		
		String listenerUrl = String.format("tcp://127.0.0.1:%s", port);

		FFmpegBuilder builder = new FFmpegBuilder()
				.addExtraArgs("-progress", listenerUrl)
				.addExtraArgs("-stats")
				.addExtraArgs("-loglevel", "warning")
				.setInput(inputFileList)
				.setFormat("concat")
				.addExtraArgs("-safe", "0")
				.overrideOutputFiles(true)
					.addOutput(outputFileFullPath)
					.done();
		
		return builder;
	}

}
