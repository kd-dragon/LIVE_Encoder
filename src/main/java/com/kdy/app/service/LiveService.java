package com.kdy.app.service;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.multipart.MultipartFile;

import com.kdy.app.bean.LiveBroadcastBean;
import com.kdy.app.bean.LiveChatSaveBean;
import com.kdy.app.bean.util.FileDownBean;
import com.kdy.app.bean.util.PagingHtmlBean;
import com.kdy.app.dto.live.AppBroadcastListDTO;
import com.kdy.app.dto.live.AppBroadcastStatusVO;
import com.kdy.app.dto.live.AppBroadcastVO;
import com.kdy.app.dto.live.CategoryVO;
import com.kdy.app.dto.live.ResultVO;
import com.kdy.app.dto.watermark.WatermarkVO;
import com.kdy.app.service.IF.LiveServiceIF;
import com.kdy.live.bean.util.code.LiveBroadcastStatus;
import com.kdy.live.dto.LiveSchedMemoryVO;
import com.kdy.live.dto.live.ChatDTO;

@Service
public class LiveService implements LiveServiceIF{
	
	private final Logger logger = LoggerFactory.getLogger(LiveService.class);

	private final LiveBroadcastBean liveBean;
	private final PagingHtmlBean pagingHtmlBean;
	private final DataSourceTransactionManager transactionManager; //트랜잭션
	private final LiveSchedMemoryVO memoryVO;
	private final LiveChatSaveBean chatBean;
	
	@Autowired
	public LiveService(   LiveBroadcastBean 			liveBean
						, PagingHtmlBean 				pagingHtmlBean
						, DataSourceTransactionManager 	transactionManager
						, LiveSchedMemoryVO 			memoryVO
						, LiveChatSaveBean 				chatBean
	) {
		
		this.liveBean 				= liveBean;
		this.pagingHtmlBean 		= pagingHtmlBean;
		this.transactionManager 	= transactionManager;
		this.memoryVO 				= memoryVO;
		this.chatBean				= chatBean;
	}
	
	@Value("${server.block-count}")
	private int blockCount;
	
	@Value("${server.root-up-category-code}")
	private int rootUpCategoryCode;
	
	@Value("${encoding.isAdaptive}")
	private boolean isAdaptive;
	
	@Value("${encoding.type}")
	private String adaptiveType;
	
	//방송중 리스트
	@Override
	public AppBroadcastListDTO getOnAirList(AppBroadcastListDTO dto) throws Exception{
		
		dto.setRootUpCategoryCode(rootUpCategoryCode);
		dto.setReplaceRootPath(memoryVO.getReplaceRootPath());
		dto.setBlockCount(blockCount);
		dto.setStartNum(dto.getBlockCount() * (dto.getCurrentPage() - 1));
		//리스트 가져오기
		dto.setLiveList(liveBean.getOnAirList(dto));
		//리스트 총 갯수
		dto.setTotalCount(liveBean.getOnAirListCount(dto));
		//페이징
		dto.setPagingHtml(pagingHtmlBean.Paging(dto.getCurrentPage(), dto.getTotalCount(), dto.getBlockCount()));
		
		return dto;
	}
	
	//방송대기 리스트
	@Override
	public AppBroadcastListDTO getWaitList(AppBroadcastListDTO dto) throws Exception{
		
		//기본 페이징 개수
		if(dto.getBlockCount() == 0) {
			dto.setBlockCount(blockCount);
		}
		
		dto.setStartNum(dto.getBlockCount() * (dto.getCurrentPage() - 1));
		//리스트 가져오기
		dto.setLiveList(liveBean.getWaitList(dto));
		//리스트 총 갯수
		dto.setTotalCount(liveBean.getWaitListCount(dto));
		//페이징
		dto.setPagingHtml(pagingHtmlBean.Paging(dto.getCurrentPage(), dto.getTotalCount(), dto.getBlockCount()));
		
		//총 페이지 수
		int totalPage = dto.getTotalCount() / dto.getBlockCount();
		if(dto.getTotalCount() % dto.getBlockCount() != 0) { totalPage++; }
		if(totalPage == 0){ totalPage++; }
		dto.setTotalPage(totalPage);
		
		return dto;
	}
	
	//방송 삭제 (다중 - list에서 삭제)
	@Override
	public String removeBroadcast(AppBroadcastListDTO dto) throws Exception {
		TransactionStatus txStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
		
			try {
				for (String lbSeq : dto.getLbSeqs()) {
					
					//방송 대기
					if(dto.getSearchStatus().equals("0")) {
						
						//방송 대기 상태인지 한번 더 확인
						if(liveBean.getLpStatus(lbSeq).equals(LiveBroadcastStatus.Wait.getTitle())) {
							liveBean.removeBroadcast_lb(lbSeq);
							liveBean.removeBroadcast_lbj(lbSeq);
							liveBean.removeHashTag(lbSeq);
						}
						
					//방송 완료 
					}else {
						liveBean.removeBroadcast_lb(lbSeq);
						liveBean.removeHashTag(lbSeq);
					}
				}
				
				transactionManager.commit(txStatus);
				return "SUCCESS";
				
			}catch (Exception e) {
				transactionManager.rollback(txStatus);
				throw new Exception(e.getMessage());
			}
	}
	
	//방송 삭제 (단일 - detail에서 삭제)
	@Override
	public String removeBroadcastOne(AppBroadcastListDTO dto) throws Exception {
		TransactionStatus txStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
		
		try {
			
			//방송 대기
			if(dto.getSearchStatus().equals("0")) {
				
				//방송 대기 상태인지 한번 더 확인
				if(liveBean.getLpStatus(dto.getLbSeq()).equals(LiveBroadcastStatus.Wait.getTitle())) {
					liveBean.removeBroadcast_lb(dto.getLbSeq());
					liveBean.removeBroadcast_lbj(dto.getLbSeq());
				}
			//방송 완료 
			}else {
				liveBean.removeBroadcast_lb(dto.getLbSeq());
			}
			
			transactionManager.commit(txStatus);
			return "SUCCESS";
			
		}catch (Exception e) {
			transactionManager.rollback(txStatus);
			throw new Exception(e.getMessage());
		}
	}
	
	//방송 대기 복제
	@Override
	public AppBroadcastVO getBroadcast(String lbSeq) throws Exception {
		return liveBean.getBroadcast(lbSeq);
	}
	
	//방송 대기 수정
	@Override
	public ResultVO modifyBroadcast(AppBroadcastVO lbvo, MultipartFile attachFile, MultipartFile thumbnailFile) throws Exception {
		
		ResultVO rsltVO = new ResultVO();
		
		if(liveBean.checkEncoderWorkerCnt(lbvo) == false){
			rsltVO.setRslt(false);
			rsltVO.setRsltMsg("EncoderWorkerCnt Over Error");
			rsltVO.setRsltDesc("라이브 방송 시간을 다시 설정해 주세요.\n해당 시간대의 라이브 인코딩 허용 개수를 초과하였습니다.");
			return rsltVO;
		}
		
		TransactionStatus txStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
		
			try {
				
				//내용
				liveBean.modifyBroadcast(lbvo);
				//썸네일
				liveBean.modifyThumbnail(lbvo, thumbnailFile);
				//첨부파일
				liveBean.modifyAttachFile(lbvo, attachFile);
				
				//이전 tag delete
				liveBean.removeHashTag(lbvo.getLbSeq());
				
				//태그
				if(lbvo.getLhTagNames() != null && lbvo.getLhTagNames().size() > 0) {
					//tag insert
					liveBean.registBroadcastHashTag(lbvo);
				}
					
				rsltVO.setRslt(true);
				rsltVO.setRsltMsg("Success Live Modify");
				rsltVO.setRsltDesc("성공적으로 등록되었습니다.");
				
				transactionManager.commit(txStatus);
				return rsltVO;
				
			} catch (Exception e) {
				transactionManager.rollback(txStatus);
				throw new Exception(e.getMessage());
			}
	}

	//방송 완료 리스트
	@Override
	public AppBroadcastListDTO getFinishList(AppBroadcastListDTO dto) throws Exception{
		
		//기본 페이징 개수
		if(dto.getBlockCount() == 0) {
			dto.setBlockCount(blockCount);
		}
		
		 Calendar cal = Calendar.getInstance();
	     cal.setTime(new Date());
	     DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	     
	     if(dto.getSearchEnDate().equals("")) {
	    	 dto.setSearchEnDate(df.format(cal.getTime()));
	     }
	     
	    cal.add(Calendar.DATE, -7);
		if(dto.getSearchStDate().equals("")) {
			dto.setSearchStDate(df.format(cal.getTime()));
		}
		
		dto.setStartNum(dto.getBlockCount() * (dto.getCurrentPage() - 1));
		//리스트 가져오기
		dto.setLiveList(liveBean.getFinishList(dto));
		//리스트 총 갯수
		dto.setTotalCount(liveBean.getFinishListCount(dto));
		//페이징
		dto.setPagingHtml(pagingHtmlBean.Paging(dto.getCurrentPage(), dto.getTotalCount(), dto.getBlockCount()));
		//총 페이지 수
		int totalPage = dto.getTotalCount() / dto.getBlockCount();
		if(dto.getTotalCount() % dto.getBlockCount() != 0) { totalPage++; }
		if(totalPage == 0){ totalPage++; }
		dto.setTotalPage(totalPage);
		
		return dto;
	}
		

	@Override
	public ResultVO registBroadcast(AppBroadcastVO lbvo, MultipartFile attachFile, MultipartFile thumbnailFile) throws Exception{ 
		
		ResultVO rsltVO = new ResultVO();
		
		if(liveBean.checkEncoderWorkerCnt(lbvo) == false){
			rsltVO.setRslt(false);
			rsltVO.setRsltMsg("EncoderWorkerCnt Over Error");
			rsltVO.setRsltDesc("라이브 방송 시간을 다시 설정해 주세요.\n동일 시간대의 라이브 인코딩 허용 가능 갯수를 초과하였습니다.");
			return rsltVO;
		}
		
		// File Validation
		if(!checkMultipartFiles(rsltVO, attachFile, thumbnailFile)) {
			return rsltVO;
		}
		
		TransactionStatus txStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
		
		try {
			
			//라이브 방송 등록
			if(liveBean.registBroadcast(lbvo)) {
				//라이브 방송 JOB 등록
				if(liveBean.registBroadcastJob(lbvo)){
					//라이브 관련 파일 등록
					if(liveBean.registBroadcastFile(lbvo, attachFile, thumbnailFile)) {
						
						// 해시태그 있을시 
						if(lbvo.getLhTagNames() != null && lbvo.getLhTagNames().size() > 0) {
							// 라이브 해시태그 등록
							if(liveBean.registBroadcastHashTag(lbvo)) {
								rsltVO.setRslt(true);
								rsltVO.setRsltMsg("Success Live Regist");
								rsltVO.setRsltDesc("성공적으로 등록되었습니다.");
								transactionManager.commit(txStatus);
								
							} else {
								rsltVO.setRslt(false);
								rsltVO.setRsltMsg("HashTag Insert Error");
								rsltVO.setRsltDesc("해시태그 등록중 오류가 발생하였습니다.");
								transactionManager.rollback(txStatus);
							}
						// 해시태그 없을시 
						} else {
							rsltVO.setRslt(true);
							rsltVO.setRsltMsg("Success Live Regist");
							rsltVO.setRsltDesc("성공적으로 등록되었습니다.");
							transactionManager.commit(txStatus);
						}
					} else {
						rsltVO.setRslt(false);
						rsltVO.setRsltMsg("File Upload Error");
						rsltVO.setRsltDesc("파일 업로드 중 오류가 발생하였습니다.");
						transactionManager.rollback(txStatus);
					}
				} else {
					rsltVO.setRslt(false);
					rsltVO.setRsltMsg("Insert Live Job Not Found");
					rsltVO.setRsltDesc("등록할 라이브 방송 JOB 이 없습니다.");
					transactionManager.rollback(txStatus);
				}
				
			} else {
				rsltVO.setRslt(false);
				rsltVO.setRsltMsg("Insert Live Not Found");
				rsltVO.setRsltDesc("등록할 라이브 방송이 없습니다.");
				transactionManager.rollback(txStatus);
			}
			
		} catch(Exception e) {
			// DB Transaction Rollback
			transactionManager.rollback(txStatus);
			
			rsltVO.setRslt(false);
			rsltVO.setRsltMsg("DB Insert Error");
			rsltVO.setRsltDesc("라이브 방송 등록에 실패하였습니다.");
			
			e.printStackTrace();
			for(StackTraceElement st : e.getStackTrace()) {
				if(st.toString().startsWith("com.kdy.app")) {
					logger.error(st.toString());
				}
			}
		}
		
		return rsltVO;
	}
	
	private boolean checkMultipartFiles(ResultVO rsltVO, MultipartFile attachFile, MultipartFile thumbnailFile) {
		
		boolean retval = true;
		
		if(thumbnailFile.isEmpty()) {
			rsltVO.setRslt(false);
			rsltVO.setRsltMsg("Thumbnail File Not Found");
			rsltVO.setRsltDesc("썸네일 파일을 찾을 수 없습니다. 다시 등록바랍니다.");
			retval = false;
		} else {
			logger.info("request file size => thumbnail : " + thumbnailFile.getSize() + " / attach : " + attachFile.getSize());
			if(thumbnailFile.getSize() > 20000000)  {
				rsltVO.setRslt(false);
				rsltVO.setRsltMsg("Exceed Max File Size [thumbnail]");
				rsltVO.setRsltDesc("썸네일 최대 업로드 크기 (20MB)를 초과하여 등록이 취소되었습니다.");
				retval = false;
			} else if (attachFile.getSize() > 20000000) {
				rsltVO.setRslt(false);
				rsltVO.setRsltMsg("Exceed Max File Size [attach]");
				rsltVO.setRsltDesc("첨부파일 최대 업로드 크기 (20MB)를 초과하여 등록이 취소되었습니다.");
				retval = false;
			}
			
			if(!checkThumbnailExtension(thumbnailFile.getOriginalFilename())) {
				rsltVO.setRslt(false);
				rsltVO.setRsltMsg("Invalid File Extension");
				rsltVO.setRsltDesc("썸네일 파일 확장자가 유효하지 않습니다. \n이미지 파일만 업로드 가능합니다.");
				retval = false;
			} else if(!attachFile.isEmpty() && !checkAttachExtension(attachFile.getOriginalFilename())) {
				rsltVO.setRslt(false);
				rsltVO.setRsltMsg("Invalid File Extension");
				rsltVO.setRsltDesc("첨부파일의 확장자가 유효하지 않습니다.");
				retval = false;
			}
		}
		return retval;
	}
	
	private boolean checkThumbnailExtension(String fileName) {
		String tmpFileName = fileName.toLowerCase();
		boolean isFormatOk = false;
		if(tmpFileName.endsWith("bmp") || tmpFileName.endsWith("png") || tmpFileName.endsWith("gif") 
				|| tmpFileName.endsWith("jpg") || tmpFileName.endsWith("tif") || tmpFileName.endsWith("tiff")) {
			
			isFormatOk = true;
		}
		
		return isFormatOk;
	}
	
	private boolean checkAttachExtension(String fileName) {
		String tmpFileName = fileName.toLowerCase();
		boolean isFormatOk = false;
		if(tmpFileName.endsWith("doc") || tmpFileName.endsWith("ppt") || tmpFileName.endsWith("pptx") 
				|| tmpFileName.endsWith("xls") || tmpFileName.endsWith("xlsx") || tmpFileName.endsWith("pdf")
				|| tmpFileName.endsWith("hwp") || tmpFileName.endsWith("txt") || tmpFileName.endsWith("bmp") 
				|| tmpFileName.endsWith("png") || tmpFileName.endsWith("gif") || tmpFileName.endsWith("mp3")
				|| tmpFileName.endsWith("tiff") || tmpFileName.endsWith("mp4") || tmpFileName.endsWith("wmv") 
				|| tmpFileName.endsWith("tif") || tmpFileName.endsWith("zip") || tmpFileName.endsWith("egg")
				|| tmpFileName.endsWith("docx") || tmpFileName.endsWith("xml") || tmpFileName.endsWith("rar")
				|| tmpFileName.endsWith("7z") || tmpFileName.endsWith("dotx") || tmpFileName.endsWith("docm")
				|| tmpFileName.endsWith("hwt") || tmpFileName.endsWith("html") || tmpFileName.endsWith("htm")
				|| tmpFileName.endsWith("rtf") || tmpFileName.endsWith("raw") || tmpFileName.endsWith("jpg")
				|| tmpFileName.endsWith("jar") || tmpFileName.endsWith("cell") || tmpFileName.endsWith("avi") 
				|| tmpFileName.endsWith("flv") || tmpFileName.endsWith("webm") || tmpFileName.endsWith("mkv") 
				) {
			isFormatOk = true;
		}
		
		return isFormatOk;
	}

	@Override
	public AppBroadcastListDTO getOnAirStatusList(AppBroadcastListDTO dto) throws Exception {
		List<AppBroadcastStatusVO> statusList = liveBean.getOnAirStatusList(dto);
		
		int onairCnt=0;
		int pauseCnt=0;
		int errorCnt=0;
		int restartCnt=0;
		int startCnt=0;
		int totalStatusCnt=0;
		
		AppBroadcastStatusVO statusVO = null;
		for(int i=0; i<statusList.size(); i++){
			statusVO = statusList.get(i);
			if(statusVO.getLbStatusNm().equalsIgnoreCase("onair")){
				onairCnt = statusVO.getLbStatusCnt();
				dto.setOnairCnt(onairCnt);
			} else if(statusVO.getLbStatusNm().equalsIgnoreCase("pause")){
				pauseCnt = statusVO.getLbStatusCnt();
				dto.setPauseCnt(pauseCnt);
			} else if(statusVO.getLbStatusNm().equalsIgnoreCase("error")){
				errorCnt = statusVO.getLbStatusCnt();
				dto.setErrorCnt(errorCnt);
			} else if(statusVO.getLbStatusNm().equalsIgnoreCase("restart")) {
				restartCnt = statusVO.getLbStatusCnt();
				dto.setRestartCnt(restartCnt);
			} else if(statusVO.getLbStatusNm().equalsIgnoreCase("start")) {
				startCnt = statusVO.getLbStatusCnt();
				dto.setStartCnt(startCnt);
			}
		}
		
		totalStatusCnt = onairCnt + pauseCnt + restartCnt + startCnt + errorCnt;
		dto.setTotalStatusCnt(totalStatusCnt);
		
		return dto;
	}

	@Override
	public AppBroadcastVO getBroadcastDetail(AppBroadcastVO vo) throws Exception {
		
		vo.setRootUpCategoryCode(rootUpCategoryCode);
		vo.setReplaceRootPath(memoryVO.getReplaceRootPath());
		
		vo = liveBean.getBroadcastDetail(vo);
		vo.setLiveStreamUrl(memoryVO.getLiveStreamingUri());
		vo.setVodStreamUrl(memoryVO.getVodStreamingUri());
		
		//live streaming type setting
		vo.setAdaptiveYn(isAdaptive?"Y":"N");
		vo.setAdaptiveType(adaptiveType);
		
		vo.setDefaultThumbnail(memoryVO.getDefaultThumbnail());
		
		return vo;
	}

	@Override
	public void attachFileDownload(AppBroadcastVO vo, HttpServletResponse res) throws Exception {
		AppBroadcastVO fileVo = liveBean.getBroadcastFile(vo);
		String serverFilePath = fileVo.getLbfAttachPath() + File.separator + fileVo.getLbfAttachServerNm();
		FileDownBean.fileDownload(serverFilePath, fileVo.getLbfAttachOriginalNm(), res);
	}

	//카테고리 list
	@Override
	public List<CategoryVO> getCategoryList() throws Exception {
		return liveBean.getCategoryList(rootUpCategoryCode);
	}
	
	//라이브 영상 정보
	@Override
	public AppBroadcastVO getLiveVideoData(AppBroadcastVO vo) throws Exception {
		vo.setLiveStreamUrl(memoryVO.getLiveStreamingUri());
		return vo;
	}

	//라이브 종료하기
	@Override
	public String liveBroadcastStatusEnd(String lbSeq) throws Exception {
		return liveBean.liveBroadcastStatusEnd(lbSeq);
	}

	@Override
	public String getCategoryTreeHtml(List<CategoryVO> categoryList) throws Exception {
		String upSeq = "0";
		String upLevel = "0";
		
		StringBuilder cateHtml = new StringBuilder();
		
		for(CategoryVO v : categoryList) {
			if(v.getLvl().equals("1")){
				if(upSeq != v.getUpCategorySeq()){
					if(upLevel.equals("2")){
						cateHtml.append("</li></ul></li>");
					}else if(upLevel.equals("3")){
						cateHtml.append("</ul></li></ul></li>");
					}else{
						cateHtml.append("</li>");
					}
				}else{
					if(upLevel.equals("1")){
						cateHtml.append("</li>");
					}
				}
				
				cateHtml.append("<li class='main-cate-group-item'><span id='"+v.getCategorySeq()+"' seq='"+v.getCategorySeq()
						+"' fullCategoryName='"+v.getFullCategoryName()+"'>"+v.getCategoryName()+"</span>");
				
			}else if(v.getLvl().equals("2")){
				if(upLevel.equals("3")){
					cateHtml.append("</ul></li>");
				}else if(upLevel.equals("1")){
					cateHtml.append("<ul class='sub1-cate-group'>");
				}else{
					cateHtml.append("</li>");
				}
				
				cateHtml.append("<li class='sub1-cate-group-item'><span id='"+v.getCategorySeq()+"' seq='"+v.getCategorySeq()
						+"' fullCategoryName='"+v.getFullCategoryName()+"'>"+v.getCategoryName()+"</span>");
				
			}else if(v.getLvl().equals("3")){
				if(upLevel.equals("4")){
					cateHtml.append("</ul></li>");
				}else if(upLevel.equals("2")){
					cateHtml.append("<ul class='sub2-cate-group' style='display:none;'>");
				}else{
					cateHtml.append("</li>");
				}
				
				cateHtml.append("<li class='sub2-cate-group-item'><span id='"+v.getCategorySeq()+"' seq='"+v.getCategorySeq()
						+"' fullCategoryName='"+v.getFullCategoryName()+"'>"+v.getCategoryName()+"</span>");
				
			}else if(v.getLvl().equals("4")) {
				if(upLevel.equals("3")){
					cateHtml.append("<ul class='sub3-cate-group' style='display:none;'>");
				}else{
					cateHtml.append("</li>");
				}
				
				cateHtml.append("<li class='sub3-cate-group-item'><span id='"+v.getCategorySeq()+"' seq='"+v.getCategorySeq()
						+"' fullCategoryName='"+v.getFullCategoryName()+"'>"+v.getCategoryName()+"</span></li>");
				
			}
			upSeq = v.getUpCategorySeq();
			upLevel = v.getLvl();
		}
		
		return cateHtml.toString();
	}

	//워터마크 설정
	@Override
	public WatermarkVO getWatermark(WatermarkVO watermarkVo) throws Exception {
		watermarkVo.setReplaceRootPath(memoryVO.getReplaceRootPath());
		return liveBean.getWatermark(watermarkVo);
	}

	//라이브 채팅 다운로드 관련
	@Override
	public AppBroadcastVO getLiveInfo(String lbSeq) throws Exception {
		return liveBean.getLiveInfo(lbSeq);
	}

	//라이브 일시중지 및 재시작
	@Override
	public String livePauseAndStart(AppBroadcastVO vo) throws Exception {
		TransactionStatus txStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
		
		String type = "FAIL";
		int result = 0;
		
		try {
			String status = vo.getLbStatus();
			
			if(status == null || status == "") {
				return type;
			} else {
				result = liveBean.livePauseAndStart(vo);
				//1: 방송중 4: 재시작
				if(vo.getLbStatus().equals("1") || vo.getLbStatus().equals("4")) {
					liveBean.pauseDateUpdate(vo);
				}
			}
			
			if(result != 0) {
				type = "SUCCESS";
			}
			
			transactionManager.commit(txStatus);
			return type;
			
		} catch (Exception e) {
			transactionManager.rollback(txStatus);
			throw new Exception(e.getMessage());
		}
	}

	//Live 상태 체크 (1:진행중/2:종료/3:일시정지)
	@Override
	public String liveStatusCheck(String lbSeq) throws Exception {
		return liveBean.liveStatusCheck(lbSeq);
	}
	
	@Override
	public List<AppBroadcastVO> getFinishListExcel(AppBroadcastListDTO dto) throws Exception {
		return liveBean.getFinishListExcel(dto);
	}

	@Override
	public List<AppBroadcastVO> getWaitListExcel(AppBroadcastListDTO dto) throws Exception {
		return liveBean.getWaitListExcel(dto);
	}

	@Override
	public List<AppBroadcastVO> getOnAirListExcel(AppBroadcastListDTO dto) throws Exception {
		return liveBean.getOnAirListExcel(dto);
	}
	
	@Override
	public String liveErrorStatusEnd(String lbSeq) throws Exception {
		return liveBean.liveErrorStatusEnd(lbSeq);
	}
	
	@Override
	public String liveErrorStatusRestart(String lbSeq) throws Exception {
		return liveBean.liveErrorStatusRestart(lbSeq);
	}

	@Override
	public List<ChatDTO> getChattingListExcel(AppBroadcastListDTO dto) throws Exception {
		return chatBean.getChattingListExcel(dto);
	}

	@Override
	public String updateDynamicRecordEnable(String lbSeq) throws Exception {
		return liveBean.updateDynamicRecordEnable(lbSeq);
	}

	@Override
	public String getDynamicRecordEnable(String lbSeq) throws Exception {
		return liveBean.getDynamicRecordEnable(lbSeq);
	}

	@Override
	public String checkChatFile(String lbSeq) throws Exception {
		
		/*
		 * 1. DB 체크 (TGLIVE_CHAT_HISTORY)
		 * 2. Redis 체크 (key: BACKUP+lbSeq) 
		 * 3. Redis 데이터 있을시 DB insert 처리
		 */
		String retval = "NULL";
		if(chatBean.checkChattingInDB(lbSeq)) {
			logger.info("[live: {}] Chatting Data Exists in DB", lbSeq);
			retval = "SUCCESS";
		} else if(chatBean.checkChattingInRedis(lbSeq, "WEB")) {
			logger.warn("[live: {}] Chatting Data Not Exists in DB -> Success Migrate Process Redis To DB", lbSeq);
			retval = "SUCCESS";
		} 
		return retval;
	}

	@Override
	public List<ChatDTO> getChatHistory(ChatDTO dto) throws Exception {
		return liveBean.getChatHistory(dto);
	}
}
