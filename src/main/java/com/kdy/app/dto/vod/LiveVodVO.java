package com.kdy.app.dto.vod;

import lombok.Data;

@Data
public class LiveVodVO {
	
	private String mediaId; //비디오 ID
	private String categoryName; //카테고리 명
	private String contentsTitle; //콘텐츠 명
	private String videoName; //비디오 파일 명
	private String duration; //영상 재생시간 (hh:mi:ss)
	private String regUserId; //등록ID
	private String regDate; //등록일
	private String runTime; //영상 재생 시간(초)

}
