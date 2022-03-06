package com.kdy.app.dto.userHome;

import lombok.Data;

@Data
public class NoticeFileVO {

	private String noticeFileSeq;     //공지사항_파일_일련번호 :  yyyyMMdd || LPAD(NOTICE_FILE_SEQ_12.NEXTVAL, 12, '0')
	private String noticeSeq;         //공지사항_일련번호
	private String filePath;          //파일_경로
	private String fileName;          //파일_이름
	private String fileNameServer;    //파일_명_서버
	
}
