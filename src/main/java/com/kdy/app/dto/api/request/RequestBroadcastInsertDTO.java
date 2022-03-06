package com.kdy.app.dto.api.request;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="방송등록 요청")
public class RequestBroadcastInsertDTO {
	
	@ApiModelProperty(value="라이브 방송 시퀀스 (자동등록)", hidden = true)
	private String lbSeq;
	
	@ApiModelProperty(value="스케줄러 시리얼 번호 (자동등록)", hidden = true)
	private String lbSerialNo;
	
	@ApiModelProperty(value="방송 제목 (max=100)", example="테스트 라이브방송01", required=true)
	@NotNull(message="Title must not be Null")
	@Size(min=1, max=100, message="Title size must be 1~100")
	private String lbTitle;
	
	@ApiModelProperty(value="방송 설명", example="안녕하세요. 테스트 라이브방송입니다.")
	private String lbDesc;
	@ApiModelProperty(value="방송 등록자", example="tigen", required=true)
	private String lbRegUserId;
	@ApiModelProperty(value="공개 여부 [Y/N] (default: Y)", example="Y")
	private String lbOpenYn;
	
	@ApiModelProperty(value="방송 시작일시 (yyyymmddhh24mi:년월일시분)", example="202106070900", required=true)
	@NotEmpty(message="StartDate is Empty")
	private String lbStartDate;
	
	@ApiModelProperty(value="방송 종료일시 (yyyymmddhh24mi:년월일시분)", example="202106071000", required=true)
	@NotEmpty(message="EndDate is Empty")
	private String lbEndDate;
	
	@ApiModelProperty(value="VOD 저장 여부 [Y/N] (default: N)", example="N")
	private String lbVodSaveYn;
	
	@ApiModelProperty(value="VOD 저장시 다운로드 여부 (default: N)", example="N")
	private String lbVodDownYn;
	
	@ApiModelProperty(value="VOD로 LIVE시 VOD SEQ")
	private String lbVodSeq;
	
	@ApiModelProperty(value="라이브 채팅사용 여부 [Y/N] (default: N)", example="N")
	private String lbChatYn;
	
	@ApiModelProperty(value="방송 프리셋 옵션 [high:고화질, mid:중화질, low:저화질]", example="low", required=true)
	private String lbPresetCd;
	
	@ApiModelProperty(value="라이브 채널 시퀀스 (Channel 일련번호)", example="20210500000000000017", required=true)
	@NotEmpty(message="Channel ID is Empty")
	private String lcSeq;
	
	//hash tag
	@ApiModelProperty(value="해시태그  ex) ['티젠소프트', '라이브방송', 'LIVE']", example="['apple', 'banana', 'cranberry']")
	private List<String> lhTagNames;
	
	//category
	@ApiModelProperty(value="카테고리 일련번호 (VOD 저장시 해당 카테고리로 이동)", example="1")
	private String lbCategorySeq;
}
