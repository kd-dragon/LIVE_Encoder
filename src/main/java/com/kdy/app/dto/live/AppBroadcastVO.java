package com.kdy.app.dto.live;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.kdy.live.dto.live.ChatDTO;

import lombok.Data;

@Data
public class AppBroadcastVO {
	
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
	private String regUserName;
	
	@NotEmpty(message="StartDate is Empty")
	private String lbStartDate;
	
	@NotEmpty(message="EndDate is Empty")
	private String lbEndDate;
	
	private String lbVodSaveYn;
	private String lbVodDownYn;
	private String lbChatYn;
	private String lbChatAnnym;
	private String lbPresetCd;
	private String lbCategorySeq;
	private String lbDelYn;
	
	//Mac Address
	private String lbSerialNo;
	
	//hash tag
	private String lhTagName;
	private List<String> lhTagNames;
	
	//broadcast file
	private String lbfSeq;
	private String lbfImgPath;
	private String lbfImgOriginalNm;
	private String lbfImgServerNm;
	private String lbfImgEncPath;
	private String lbfImgEncNm;
	private String lbfAttachPath;
	private String lbfAttachOriginalNm;
	private String lbfAttachServerNm;
	
	//broadcast channel
	@NotEmpty(message="Channel ID is Empty")
	private String lcSeq;
	private String lcName;
	private String lcUrl;
	
	//broadcast job
	private String lbjSeq;
	private String lbjProcessId;
	private String lbjDuration; 		
	private String lbjPauseDate;
	private String lbjCurViewerCnt;
	private String lbjVodLastPosition;
	private String lbjLogRegDate;
	private String lbjLogMsg;
	private String lbjLogDesc;
	
	//category
	private String categorySeq;
	private String categoryName;
	private String fullCategoryName;
	private int rootUpCategoryCode;
	
	private String totalDuration;  //시작시간, 종료시간으로 계산 (예상 방송 시간)
	private String passDuration;   //진행 방송 시간 (실제로 방송한 시간)
	
	//삭제여부
	private String removeThumbnailFlag;
	private String removeFileFlag;
	
	private String replaceRootPath;
	private String liveStreamUrl;
	
	private String lbjDurationFormat;
	private String totalDurationFormat;

	//라이브 채팅 다운로드 관련
	private String startYear;
	private String startMonth;
	private String startDay;
	private String endYear;
	private String endMonth;
	private String endDay;
	private String uploadRootPath;
	
	
	//VOD
	private String lbVodSeq;
	private String lbVodRuntime;
	private String lbVodPath; //web vod path
	private String lbVodName; //encoding file name
	private String vodStreamUrl; //VOD 스트리밍 URL
	private String vodTitle;
	
	//라이브 스트리밍 종류
	private String adaptiveYn; //적응형 사용여부
	private String adaptiveType; //적응형 타입
	private String lbSurveyLink;	//설문조사 링크
	private String lbSurveyWidth;	//설문조사 너비
	private String lbSurveyHeight;	//설문조사 높이
	
	private String defaultThumbnail; //Live 방송준비이미지파일
	
}
