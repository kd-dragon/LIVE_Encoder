package com.kdy.app.service.api;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.multipart.MultipartFile;

import com.kdy.app.bean.api.ApiLiveBroadcastBean;
import com.kdy.app.bean.util.PagingHtmlBean;
import com.kdy.app.dto.api.request.RequestBroadcastExcelDTO;
import com.kdy.app.dto.api.request.RequestBroadcastInsertDTO;
import com.kdy.app.dto.api.request.RequestBroadcastListDTO;
import com.kdy.app.dto.api.request.RequestBroadcastUpdateDTO;
import com.kdy.app.dto.api.request.RequestUserBroadcastListDTO;
import com.kdy.app.dto.api.response.ResponseBroadcastDetailDTO;
import com.kdy.app.dto.api.response.ResponseBroadcastListDTO;
import com.kdy.app.dto.api.response.ResponseBroadcastListExcelDTO;
import com.kdy.app.dto.api.response.ResponseUserBroadcastListDTO;
import com.kdy.app.dto.live.AppBroadcastStatusVO;
import com.kdy.app.dto.live.AppBroadcastVO;
import com.kdy.app.dto.live.ResultVO;
import com.kdy.app.service.api.IF.ApiLiveServiceIF;
import com.kdy.live.bean.util.code.LiveBroadcastStatus;
import com.kdy.live.dto.LiveSchedMemoryVO;

@Service
public class ApiLiveService implements ApiLiveServiceIF {
	
	private final Logger logger = LoggerFactory.getLogger(ApiLiveService.class);
	
	private final ApiLiveBroadcastBean apiLiveBean;
	private final PagingHtmlBean pagingHtmlBean;
	private final DataSourceTransactionManager transactionManager; //트랜잭션
	private final LiveSchedMemoryVO memoryVO;
	
	
	@Autowired
	public ApiLiveService(ApiLiveBroadcastBean apiLiveBean
						  , PagingHtmlBean pagingHtmlBean
					 	  , DataSourceTransactionManager transactionManager
					 	  , LiveSchedMemoryVO memoryVO) {
		
		this.apiLiveBean = apiLiveBean;
		this.pagingHtmlBean = pagingHtmlBean;
		this.transactionManager = transactionManager;
		this.memoryVO = memoryVO;
	}
	
	@Value("${server.block-count}")
	private int blockCount;
	
	@Value("${server.root-up-category-code}")
	private int rootUpCategoryCode;
	
	@Value("${encoding.isAdaptive}")
	private boolean isAdaptive;
	
	@Value("${encoding.type}")
	private String adaptiveType;
	
	@Override
	public ResponseBroadcastListDTO getBroadcasts(RequestBroadcastListDTO dto) throws Exception {
		
		ResponseBroadcastListDTO res = new ResponseBroadcastListDTO();
		dto.setRootUpCategoryCode(rootUpCategoryCode);
		
		if(dto.getBlockCount() == 0) {
			dto.setBlockCount(blockCount);
		}
		
		dto.setStartNum((dto.getBlockCount() * (dto.getCurrentPage() - 1)));

		/** 방송 완료일 경우 - list 검색 기간 설정**/
		if(dto.getSearchStatus().contains(LiveBroadcastStatus.Finished.getTitle())) {
			
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
		}
		
		
		//리스트 가져오기
		res.setBroadcasts(apiLiveBean.getBroadcastList(dto));
		//리스트 총 갯수
		res.setTotalCount(apiLiveBean.getBroadcastListCount(dto));
		
		
		/** 방송중일 경우에만 **/
		if(	dto.getSearchStatus().contains(LiveBroadcastStatus.OnAir.getTitle()) || dto.getSearchStatus().contains(LiveBroadcastStatus.Pause.getTitle())) {
			
			List<AppBroadcastStatusVO> statusList = apiLiveBean.getOnAirStatusList(dto);
			
			int onairCnt = 0;
			int pauseCnt = 0;
			int restartCnt = 0;
			int totalStatusCnt = 0;
			
			AppBroadcastStatusVO statusVo = null;
			for(int i = 0; i < statusList.size(); i++) {
				statusVo = statusList.get(i);
				if(statusVo.getLbStatusNm().equalsIgnoreCase("onair")) {
					onairCnt = statusVo.getLbStatusCnt();
					res.setOnairCnt(onairCnt);
				} else if(statusVo.getLbStatusNm().equalsIgnoreCase("pause")) {
					pauseCnt = statusVo.getLbStatusCnt();
					res.setPauseCnt(pauseCnt);
				} else if(statusVo.getLbStatusNm().equalsIgnoreCase("restart")) {
					restartCnt = statusVo.getLbStatusCnt();
					res.setRestartCnt(restartCnt);
				}
			}
			
			totalStatusCnt = onairCnt + pauseCnt + restartCnt;
			res.setTotalStatusCnt(totalStatusCnt);
			
		}
		
		dto.setReplaceRootPath(null);
		res.setRequest(dto);
		return res;
	}
	
	@Override
	public ResultVO addBroadcasts(RequestBroadcastInsertDTO requestDTO, MultipartFile attachFile, MultipartFile thumbnailFile) throws Exception{ 
		
		ResultVO rsltVO = new ResultVO();
		
		if(apiLiveBean.checkEncoderWorkerCnt(requestDTO) == false){
			rsltVO.setRslt(false);
			rsltVO.setRsltMsg("EncoderWorkerCnt Over Error");
			rsltVO.setRsltDesc("라이브 방송 시간을 다시 설정해 주세요.\n동일 시간대의 라이브 인코딩 허용 가능 갯수를 초과하였습니다.");
			return rsltVO;
		}
		
		// File Validation
		rsltVO = checkMultipartFiles(rsltVO, attachFile, thumbnailFile);
		if(!rsltVO.getRslt()) { return rsltVO; }
		
		TransactionStatus txStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
		
		try {
			//라이브 방송 등록
			if(apiLiveBean.registBroadcast(requestDTO)) {
				//라이브 방송 JOB 등록
				if(apiLiveBean.registBroadcastJob(requestDTO)){
					//라이브 관련 파일 등록
					if(apiLiveBean.registBroadcastFile(requestDTO, attachFile, thumbnailFile)) {
						// 해시태그 있을시 
						if(requestDTO.getLhTagNames() != null && requestDTO.getLhTagNames().size() > 0) {
							// 라이브 해시태그 등록
							if(apiLiveBean.registBroadcastHashTag(requestDTO)) {
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
						// 해시태그 없을 시 
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
	
	/**
	 * 첨부파일 통합 검증 로직
	 * @param rsltVO
	 * @param attachFile
	 * @param thumbnailFile
	 * @return boolean
	 */
	private ResultVO checkMultipartFiles(ResultVO rsltVO, MultipartFile attachFile, MultipartFile thumbnailFile) {
		
		if(thumbnailFile != null && !thumbnailFile.isEmpty()) {
			if(thumbnailFile.getSize() > 20000000)  {
				rsltVO.setRslt(false);
				rsltVO.setRsltMsg("Exceed Max File Size [thumbnail]");
				rsltVO.setRsltDesc("썸네일 최대 업로드 크기 (20MB)를 초과하여 등록이 취소되었습니다.");
				return rsltVO;
			}
			
			if(!checkThumbnailExtension(thumbnailFile.getOriginalFilename())) {
				rsltVO.setRslt(false);
				rsltVO.setRsltMsg("Invalid File Extension");
				rsltVO.setRsltDesc("썸네일 파일 확장자가 유효하지 않습니다. \n이미지 파일만 업로드 가능합니다.");
				return rsltVO;
			} 
		}
		
		if(attachFile != null && !attachFile.isEmpty()) {
			if (attachFile.getSize() > 20000000) {
				rsltVO.setRslt(false);
				rsltVO.setRsltMsg("Exceed Max File Size [attach]");
				rsltVO.setRsltDesc("첨부파일 최대 업로드 크기 (20MB)를 초과하여 등록이 취소되었습니다.");
				return rsltVO;
			}
				
			if(!attachFile.isEmpty() && !checkAttachExtension(attachFile.getOriginalFilename())) {
				rsltVO.setRslt(false);
				rsltVO.setRsltMsg("Invalid File Extension");
				rsltVO.setRsltDesc("첨부파일의 확장자가 유효하지 않습니다.");
				return rsltVO;
			}
		}
		
		rsltVO.setRslt(true);
		return rsltVO;
	}
	
/**/
//	private boolean checkMultipartFiles(ResultVO rsltVO, MultipartFile attachFile, MultipartFile thumbnailFile) {
//		
//		boolean retval = true;
//		
//		if(thumbnailFile.isEmpty()) {
//			rsltVO.setRslt(false);
//			rsltVO.setRsltMsg("Thumbnail File Not Found");
//			rsltVO.setRsltDesc("썸네일 파일을 찾을 수 없습니다. 다시 등록바랍니다.");
//			retval = false;
//		} else {
//			logger.info("request file size => thumbnail : " + thumbnailFile.getSize() + " / attach : " + attachFile.getSize());
//			if(thumbnailFile.getSize() > 20000000)  {
//				rsltVO.setRslt(false);
//				rsltVO.setRsltMsg("Exceed Max File Size [thumbnail]");
//				rsltVO.setRsltDesc("썸네일 최대 업로드 크기 (20MB)를 초과하여 등록이 취소되었습니다.");
//				retval = false;
//			} else if (attachFile.getSize() > 20000000) {
//				rsltVO.setRslt(false);
//				rsltVO.setRsltMsg("Exceed Max File Size [attach]");
//				rsltVO.setRsltDesc("첨부파일 최대 업로드 크기 (20MB)를 초과하여 등록이 취소되었습니다.");
//				retval = false;
//			}
//			
//			if(!checkThumbnailExtension(thumbnailFile.getOriginalFilename())) {
//				rsltVO.setRslt(false);
//				rsltVO.setRsltMsg("Invalid File Extension");
//				rsltVO.setRsltDesc("썸네일 파일 확장자가 유효하지 않습니다. \n이미지 파일만 업로드 가능합니다.");
//				retval = false;
//			} else if(!attachFile.isEmpty() && !checkAttachExtension(attachFile.getOriginalFilename())) {
//				rsltVO.setRslt(false);
//				rsltVO.setRsltMsg("Invalid File Extension");
//				rsltVO.setRsltDesc("첨부파일의 확장자가 유효하지 않습니다.");
//				retval = false;
//			}
//		}
//		return retval;
//	}
	
	private boolean checkThumbnailExtension(String fileName) {
		String tmpFileName = fileName.toLowerCase();
		boolean isFormatOk = false;
		if(tmpFileName.endsWith("bmp") || tmpFileName.endsWith("png") || tmpFileName.endsWith("gif") 
				|| tmpFileName.endsWith("jpg") || tmpFileName.endsWith("tif") || tmpFileName.endsWith("tiff")) {
			
			isFormatOk = true;
		}
		
		return isFormatOk;
	}
	
	/**
	 * 첨부파일 확장자 체크
	 * @param fileName
	 * @return boolean
	 */
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
	public List<AppBroadcastVO> getBroadcastsExcel(RequestBroadcastExcelDTO requestDTO) throws Exception {
		return apiLiveBean.getBroadcastListExcel(requestDTO);
	}

	@Override
	public ResponseBroadcastDetailDTO getBroadcast(String seq) throws Exception {
		
		AppBroadcastVO vo = new AppBroadcastVO();
		vo.setRootUpCategoryCode(rootUpCategoryCode);
		vo.setReplaceRootPath(memoryVO.getReplaceRootPath());
		vo.setLbSeq(seq);
		
		ResponseBroadcastDetailDTO dto = apiLiveBean.getBroadcast(vo);
		
		if(dto == null) {
			return null;
		}
		dto.setLiveStreamUrl(memoryVO.getLiveStreamingUri());
		dto.setVodStreamUrl(memoryVO.getVodStreamingUri());
		
		//live streaming type setting
		dto.setAdaptiveYn(isAdaptive?"Y":"N");
		dto.setAdaptiveType(adaptiveType);
		
		return dto;
	}

	@Override
	public ResultVO deleteBroadcast(List<String> seqs) throws Exception {
		ResultVO rsltVO = new ResultVO();
		
		TransactionStatus txStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
		
		try {
			
			for (String seq : seqs) {
				
				String status = apiLiveBean.getLbStatus(seq);
				
				//방송 대기
				if(status.equals(LiveBroadcastStatus.Wait.getTitle())) {
					apiLiveBean.deleteBroadcastLb(seq);
					apiLiveBean.deleteBroadcastLbj(seq);
					apiLiveBean.deleteHashTag(seq);
					
				//방송 완료 
				}else if(status.equals(LiveBroadcastStatus.Finished.getTitle())){
					apiLiveBean.deleteBroadcastLb(seq);
					apiLiveBean.deleteHashTag(seq);
					
				}else {
					transactionManager.rollback(txStatus);
					rsltVO.setRslt(false);
					rsltVO.setRsltMsg("cannot delete onAir broadcast");
					rsltVO.setRsltDesc("라이브 중인 방송은 지울 수 없습니다.");
					return rsltVO;
				}
			}
			
			transactionManager.commit(txStatus);
			
			rsltVO.setRslt(true);
			rsltVO.setRsltMsg("Success broadcast delete");
			rsltVO.setRsltDesc("방송이 삭제되었습니다.");
			return rsltVO;
			
		}catch (Exception e) {
			transactionManager.rollback(txStatus);
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public ResultVO updateBroadcast(RequestBroadcastUpdateDTO requestDTO, MultipartFile attachFile, MultipartFile thumbnailFile) throws Exception {
		ResultVO rsltVO = new ResultVO();

		if(apiLiveBean.checkEncoderWorkerCnt(requestDTO) == false){
			rsltVO.setRslt(false);
			rsltVO.setRsltMsg("EncoderWorkerCnt Over Error");
			rsltVO.setRsltDesc("라이브 방송 시간을 다시 설정해 주세요.\n해당 시간대의 라이브 인코딩 허용 개수를 초과하였습니다.");
			return rsltVO;
		}
		
		rsltVO = checkMultipartFiles(rsltVO, attachFile, thumbnailFile);
		if(!rsltVO.getRslt()) { return rsltVO; }
		
		TransactionStatus txStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
		
		try {
			/*내용*/
			if (!apiLiveBean.updateLb(requestDTO)) {
				transactionManager.rollback(txStatus);
				rsltVO.setRslt(false);
				rsltVO.setRsltMsg("Failed to update for live broadcast");
				rsltVO.setRsltDesc("라이브 방송 수정에 실패하였습니다.");
				return rsltVO;
			}
			
			/*썸네일*/
			if(!apiLiveBean.updateThumbnail(requestDTO, thumbnailFile)){
				transactionManager.rollback(txStatus);
				rsltVO.setRslt(false);
				rsltVO.setRsltMsg("Thumnail Upload Error");
				rsltVO.setRsltDesc("썸네일 업로드 중 오류가 발생하였습니다.");
				return rsltVO;
			}
			
			/*첨부파일*/
			if(!apiLiveBean.updateAttach(requestDTO, attachFile)) {
					transactionManager.rollback(txStatus);
					rsltVO.setRslt(false);
					rsltVO.setRsltMsg("Attach File Upload Error");
					rsltVO.setRsltDesc("첨부파일 업로드 중 오류가 발생하였습니다.");
					return rsltVO;
			}
			
			/*태그*/
			apiLiveBean.deleteHashTag(requestDTO.getLbSeq());
			if(requestDTO.getLhTagNames() != null && requestDTO.getLhTagNames().size() > 0) {
				if(!apiLiveBean.registBroadcastHashTag(requestDTO)) {
					transactionManager.rollback(txStatus);
					rsltVO.setRslt(false);
					rsltVO.setRsltMsg("HashTag Insert Error");
					rsltVO.setRsltDesc("해시태그 등록중 오류가 발생하였습니다.");
					return rsltVO;
				}
			}
			
			transactionManager.commit(txStatus);
			
			rsltVO.setRslt(true);
			rsltVO.setRsltMsg("Success broadcast update");
			rsltVO.setRsltDesc("라이브 방송이 수정되었습니다.");
			
			return rsltVO;
			
		} catch(Exception e) {
			// DB Transaction Rollback
			transactionManager.rollback(txStatus);
			
			rsltVO.setRslt(false);
			rsltVO.setRsltMsg("Fail broadcast update");
			rsltVO.setRsltDesc("라이브 방송 수정에 실패하였습니다.");
			
			e.printStackTrace();
			for(StackTraceElement st : e.getStackTrace()) {
				if(st.toString().startsWith("com.kdy.app")) {
					logger.error(st.toString());
				}
			}
		}
		
		return rsltVO;
	}

	//라이브 영상 정보 가져오기
	@Override
	public AppBroadcastVO getLiveVideoData(AppBroadcastVO vo) throws Exception {
		vo.setLiveStreamUrl(memoryVO.getLiveStreamingUri());
		return vo;
	}

	//라이브 종료
	@Override
	public ResultVO liveBroadcastStatusEnd(String lbSeq) throws Exception {
		ResultVO resultVo = new ResultVO();
		
		if(apiLiveBean.liveBroadcastStatusEnd(lbSeq)) {
			resultVo.setRslt(true);
			resultVo.setRsltMsg("SUCCESS");
			resultVo.setRsltDesc("라이브 방송이 종료되었습니다.");
		} else {
			resultVo.setRslt(false);
			resultVo.setRsltMsg("Update Live Status Not Found");
			resultVo.setRsltDesc("라이브 방송을 종료하는데 실패하였습니다.");
		}
		return resultVo;
	}

	//라이브 채팅 다운로드
	@Override
	public AppBroadcastVO getLiveInfo(String lbSeq) throws Exception {
		return apiLiveBean.getLiveInfo(lbSeq);
	}
	
	//라이브 일시중지 및 재시작
	@Override
	public ResultVO livePauseAndStart(AppBroadcastVO vo) throws Exception {
		ResultVO resultVo = new ResultVO();
		
		if(apiLiveBean.livePauseAndStart(vo)) {
			//1: 방송중 4: 재시작
			if(vo.getLbStatus().equals("1") || vo.getLbStatus().equals("4")) {
				apiLiveBean.pauseDateUpdate(vo.getLbSeq());
			}
			resultVo.setRslt(true);
			resultVo.setRsltMsg("SUCCESS");
			resultVo.setRsltDesc("라이브 방송을 일시중지 했습니다.");
		} else {
			resultVo.setRslt(false);
			resultVo.setRsltMsg("Update Live Status Not Found");
			resultVo.setRsltDesc("방송 일시중지에 실패했습니다.");
		}
		return resultVo;
	}

	//User live Broadcast List
	@Override
	public ResponseUserBroadcastListDTO getUserBroadcasts(RequestUserBroadcastListDTO requestDTO) throws Exception {
		
		ResponseUserBroadcastListDTO dto = new ResponseUserBroadcastListDTO();
		requestDTO.setReplaceRootPath(memoryVO.getReplaceRootPath());
		List<String> status = new ArrayList<String>();
		status.add(LiveBroadcastStatus.OnAir.getTitle());
		status.add(LiveBroadcastStatus.Pause.getTitle());
		status.add(LiveBroadcastStatus.Restart.getTitle());
		
		requestDTO.setLbStatusList(status);
		dto.setOnairbroadcasts(apiLiveBean.getLiveStatusList(requestDTO)); //On-air
		
		status = new ArrayList<String>();
		status.add(LiveBroadcastStatus.Wait.getTitle());
		requestDTO.setLbStatusList(status);
		dto.setWaitbroadcasts(apiLiveBean.getLiveStatusList(requestDTO)); //wait
		
		return dto;
	}

	//LIVE 상태 확인
	@Override
	public String liveStatusCheck(String lbSeq) throws Exception {
		return apiLiveBean.liveStatusCheck(lbSeq);
	}

	//방송 목록 (excel 용)
	@Override
	public ResponseBroadcastListExcelDTO getBroadcastListExcel(RequestBroadcastListDTO requestDTO) throws Exception {
		ResponseBroadcastListExcelDTO res = new ResponseBroadcastListExcelDTO();
		res.setBroadcasts(apiLiveBean.getBroadcastListExcel(requestDTO));
		res.setRequest(requestDTO);
		return res;
	}
}
