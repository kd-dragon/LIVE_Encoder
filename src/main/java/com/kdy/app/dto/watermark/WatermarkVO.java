package com.kdy.app.dto.watermark;

import lombok.Data;

@Data
public class WatermarkVO {

	private String wmSeq;				//워터마크 시퀀스
	private String position;			//워터마크 위치
	private String regDate;				//등록일자
	private String luId;				//사용자 아이디
	private String imgFilePath;			//워터마크 이미지 원본 파일 경로
	private String imgFileName;			//워터마크 이미지 원본 파일명
	private String imgServerFileName;	//워터마크 이미지 서버 파일명
	private String imgWebPath;
	private String replaceRootPath;
	private String useYn;
}
