package com.kdy.app.dto.vod;

import lombok.Data;

@Data
public class VodVO {
	
	private String vodSeq;					//비디오 시퀀스
	private String vodTitle;                //비디오 제목                        
	private String vodDesc;                 //비디오 설명                        
	private String vodStatus;               //비디오 상태 (0 : 대기, 1 : 등록완료/사용가능, 2 : 임시, 3 : 인코딩중, 4 : 실패)
	private String vodPreset;               //VOD 프리셋(high/mid/low)         
	private String vodType;                 //VOD 종류(R:LIVE 녹화 파일/D:직접 등록)  
	private String vodDownYn;               //VOD 다운로드 여부(Y/N)              
	private String originalFilePath;        //원본_비디오_경로                     
	private String originalFileName;        //원본_비디오_이름                     
	private String originalFileSize;        //원본_비디오_용량                     
	private String regUserId;               //등록_사용자_아이디                    
	private String regDate;                 //등록_일자                                                   
	private String modUserId;               //수정_사용자_아이디                    
	private String modDate;                 //수정_일자                                                      
	private String delUserId;               //삭제_사용자_아이디                    
	private String delDate;                 //삭제_일자                                                      
	private String delYn;                   //삭제_여부                         
	private String categorySeq;             //카테고리_시퀀스                                                   
	private String lbSeq;                   //라이브방송_시퀀스
	private String regUserName;
	private String categoryName;
	
	private String encodingFilePath;		//인코딩_파일_경로
	private String encodingFileName;		//인코딩_파일_명
	private String encodingFileSize;		//인코딩_파일_사이즈
	private String encDate;					//인코딩_일자
	
	private String fullCategory;			//카테고리 명 (상위 Depth포함)
	
	private String thumbFilePath;			//썸네일 경로
	private String thumbFileName;			//썸네일 파일명
	
	private String vodStreamUrl;			//VOD 스트리밍 URL
	private String adaptiveYn;				//적응형 사용여부
	private String adaptiveType;			//적응형 타입
	
	private String thumbSeq;
	private String thumbType;
	private String delId;
	private String reprimageYn;
	private String thumbTime;
	private String thumbTimeToSec;
	private String vttFilePath;
	
}
