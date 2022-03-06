package com.kdy.live.dto.system;

import lombok.Data;

@Data
public class SystemConfigVO {
	private String liveFileLocalPath;
	private String encodingVodFilePath;
	private String etcFileUploadPath;;
	private String replaceRootPath;
	private String liveStreamingUri;
	private String vodStreamingUri;
	private Integer encoderWorkerCnt;
	private Integer encoderServerCnt;
	private int thumbnailCnt; //썸네일 갯수
	private String thumbnailTime; //썸네일 간격
	private String thumbnailFormat; //썸네일 포맷
	private String vodOriginalFilePath;	//VOD 직접등록시 원본파일 저장 경로 (uploader conf 경로와 같아야함)
	private String vodTempFilePath; //VOD uploader 영상 업로드 경로 > local 경로 (original path(NAS)로 복사)
	private String rootPath;
	private double uploadFolderSize;
	private double rootFolderSize;
	private String defaultThumbnail;
	private String liveFileNasPath;
	
}
