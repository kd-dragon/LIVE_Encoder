package com.kdy.app.dto.userHome;

import java.util.List;
import lombok.Data;

@Data
public class NoticeDetailDTO {
	
	private NoticeVO noticeVo;
	private List<NoticeFileVO> fileList;
	
	//권한 체크
	private String roleSeq;
	private String noticeSeq;
	private String userId;
}