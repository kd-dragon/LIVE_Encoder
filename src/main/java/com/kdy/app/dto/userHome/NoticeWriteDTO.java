package com.kdy.app.dto.userHome;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class NoticeWriteDTO {
	
	private String noticeSeq;
	private String noticeTitle;
	private String noticeCtx;
	private String noticeCtxSrch;
	private String mainNoticeYn;
	private String userId;
	
	private List<MultipartFile> uploadFile;
	private List<MultipartFile> uploadImg;
	private List<String> delFileSeqs;
	
	private int mainNoticeMax;

	
	
	
}
