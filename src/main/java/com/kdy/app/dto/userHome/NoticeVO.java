package com.kdy.app.dto.userHome;

import lombok.Data;

@Data
public class NoticeVO {

	private String noticeSeq;
	private String noticeTitle;
	private String noticeCtx;
	private String noticeCtxSrch;
	private String viewCnt;
	private String mainNoticeYn;
	private String regUserId;
	private String regDate;
	private String modUserId;
	private String modDate;
	private String delYn;
	private String delUserId;
	private String delDate;
	private String regUserName;
}
