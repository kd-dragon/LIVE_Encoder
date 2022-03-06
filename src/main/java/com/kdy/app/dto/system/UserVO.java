package com.kdy.app.dto.system;


import java.security.PrivateKey;

import lombok.Data;

@Data
public class UserVO {
	private String luId;		//아이디
	private String luName;		//이름
	private String luPwd;		//비밀번호
	private String regDate;		//등록일자
	private String delYn;		//삭제여뷰
	private String delDate;		//삭제일자
	private PrivateKey privateKey;
	
	private String lastLoginDate;	//최종 로그인 일시
	private String categorySeq;	//카테고리 시퀀스(부서)
	private String categoryName;	//카테고리(부서)명
	private String authSeq; //권한코드
	private String authName; //권한명
}
