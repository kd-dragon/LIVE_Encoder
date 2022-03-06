package com.kdy.app.dto.util;

import lombok.Data;

@Data
public class FileVO {

	private Integer fileIdx;		  //파일_번호
	private String filePath;          //파일_경로
	private String fileName;          //파일_이름
	private String fileNameServer;    //파일_명_서버
	
	//과제 전체 다운로드시 필요 parameter
	private String finSubmitDt; //완료 날짜
	private String userName; //사용자 이름
	private String userId; //사용자 아이디
	
}
