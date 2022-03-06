package com.kdy.live.dto.ffmpeg;

import lombok.Data;

@Data
public class FfprobeVO {
	
	//Common INFO
	public int    index      = -1;
	public double duration   = 0;
	public int    bitrate    = 0;
	public String codecName  = "";
	
	//Format INFO
	public String formatName = "";
	public String codecType  = "";
	public int    streams    = 0;
	public int    size       = 0;
	
	//Video INFO
	public int    width                = 0;
	public int    height               = 0;
	public int    nbFrames             = 0;
	public int    rotate               = 0;
	public double aspect			   = 0.0;
	public Double fps                  = 0.0;
	public Double display_aspect_ratio = 0.0;
	
	//Audio INFO
	public int channels   = 0;
	public int sampleRate = 0;
	public int bitsPerSample;
	
}
