package com.kdy.app.dto.vod;

import lombok.Data;

@Data
public class VodWriteDTO {
	
	private String vodSeq;					//비디오 시퀀스
	private String vodTitle;                //비디오 제목                        
	private String vodDesc;                 //비디오 설명                        
	private String vodStatus;               //비디오 상태(0 : 등록중, 1 : 등록완료/사용가능, 2 : 임시, 3 : 인코딩중, 4 : 실패)
	private String vodPreset;               //VOD 프리셋(high/mid/low)         
	private String vodType;                 //VOD 종류(R:LIVE 녹화 파일/D:직접 등록)  
	private String vodDownYn;               //VOD 다운로드 여부(Y/N)              
	private String regUserId;               //등록_사용자_아이디                    
	private String regDate;                 //등록_일자                                                   
	private String categorySeq;             //카테고리_시퀀스                                                   

	private String originalFilePath;        //원본_비디오_경로                     
	private String originalFileServer;      //원본_비디오_서버파일명                
	private String originalFileName;        //원본_비디오_이름                    
	private String originalFileSize;        //원본_비디오_용량
	
	private String vodLimitSize;			//VOD 최대 사이즈
	private String uploaderPort;			//Uploader port
	
	private String tempFilePath;			//VOD uploader upload경로
	
}
