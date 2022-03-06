package com.kdy.live.dto.vod;

import lombok.Data;

@Data
public class ThumbnailVO {
	private String thumbSeq;
	private String lbSeq; 
	private String thumbFilePath;
	private String thumbFileName;
	private String thumbWidth;
	private String thumbHeight;
	private String repreimageYn;
	private String thumbTime;
	private String thumbType;
	private String vodSeq; //mediaId
}
