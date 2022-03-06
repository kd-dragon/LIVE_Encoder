package com.kdy.live.dto.preset;

import lombok.Data;

@Data
public class PresetVO {
	private String contentId;
	private String type;
	private String codec;
	private String quality;
	private String width;
	private String height;
	private String keepRatio;
	private String resize;
	private String speed;
	private String audioCodec;
	private String audioBitrate;
	private String thumbFormat;
	private String thumbWidth;
	private String thumbHeight;
	private String thumbFit;
	private String thumbSeek;
	private String thumbEstimate;
}
