package com.kdy.app.dto.api.response;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.kdy.app.dto.live.ResultVO;

import lombok.Data;

@Data
public class ResponseBroadcastDetailDTO {
	
	//broadcast
	private String lbSeq;
	
	@NotNull(message="Title must not be Null")
	@Size(min=1, max=100, message="Title size must be 1~100")
	private String lbTitle;
	
	private String lbDesc;
	private String lbRegUserId;
	private String lbRegDate;
	private String lbStatus;
	private String lbOpenYn;
	
	@NotEmpty(message="StartDate is Empty")
	private String lbStartDate;
	
	@NotEmpty(message="EndDate is Empty")
	private String lbEndDate;
	
	private String lbVodSaveYn;
	private String lbVodDownYn;
	private String lbChatYn;
	private String lbPresetCd;
	private String lbCategorySeq;
	
	//VOD
	private String lbVodSeq;
	private String lbVodRuntime;
	private String lbVodPath; //web vod path
	private String lbVodName; //encoding file name
//	private String vodStreamUrl; //VOD 스트리밍 URL
	private String vodTitle;
	
	//hash tag
	private String lhTagName;
	
	//broadcast file
	private String lbfSeq;
	private String lbfImgEncPath;
	private String lbfImgEncNm;
	
	private String lbfAttachOriginalNm;
	private String lbfAttachPath;
	private String lbfAttachServerNm;
	
	
	//broadcast channel
	@NotEmpty(message="Channel ID is Empty")
	private String lcSeq;
	private String lcName;
	private String lcUrl;
	
	//broadcast job
	private String lbjDuration; 		//누적 방송 시간
	private String lbjPauseDate;
	private String lbjCurViewerCnt;
	private String lbjVodLastPosition;
	private String lbjLogRegDate;
	private String lbjLogMsg;
	private String lbjLogDesc;
	
	private String lbjDurationFormat;
	
	//category
	private String categoryName;
	private String fullCategoryName;
	private int rootUpCategoryCode;
	
	private String totalDuration;
	private String passDuration;
	
	private ResultVO result;
	private String liveStreamUrl;
	private String vodStreamUrl;
	
	//라이브 스트리밍 종류
	private String adaptiveYn; //적응형 사용여부
	private String adaptiveType; //적응형 타입
}
