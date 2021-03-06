package com.kdy.app.service.api;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApiLiveService implements ApiLiveServiceIF {
	
	private final Logger logger = LoggerFactory.getLogger(ApiLiveService.class);
	
	private final ApiLiveBroadcastBean apiLiveBean;
	private final PagingHtmlBean pagingHtmlBean;
	private final DataSourceTransactionManager transactionManager; //νΈλμ­μ
	private final LiveSchedMemoryVO memoryVO;
	
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

		/** λ°©μ‘ μλ£μΌ κ²½μ° - list κ²μ κΈ°κ° μ€μ **/
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
		
		
		//λ¦¬μ€νΈ κ°μ Έμ€κΈ°
		res.setBroadcasts(apiLiveBean.getBroadcastList(dto));
		//λ¦¬μ€νΈ μ΄ κ°―μ
		res.setTotalCount(apiLiveBean.getBroadcastListCount(dto));
		
		
		/** λ°©μ‘μ€μΌ κ²½μ°μλ§ **/
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
			rsltVO.setRsltDesc("λΌμ΄λΈ λ°©μ‘ μκ°μ λ€μ μ€μ ν΄ μ£ΌμΈμ.\nλμΌ μκ°λμ λΌμ΄λΈ μΈμ½λ© νμ© κ°λ₯ κ°―μλ₯Ό μ΄κ³Όνμμ΅λλ€.");
			return rsltVO;
		}
		
		// File Validation
		rsltVO = checkMultipartFiles(rsltVO, attachFile, thumbnailFile);
		if(!rsltVO.getRslt()) { return rsltVO; }
		
		TransactionStatus txStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
		
		try {
			//λΌμ΄λΈ λ°©μ‘ λ±λ‘
			if(apiLiveBean.registBroadcast(requestDTO)) {
				//λΌμ΄λΈ λ°©μ‘ JOB λ±λ‘
				if(apiLiveBean.registBroadcastJob(requestDTO)){
					//λΌμ΄λΈ κ΄λ ¨ νμΌ λ±λ‘
					if(apiLiveBean.registBroadcastFile(requestDTO, attachFile, thumbnailFile)) {
						// ν΄μνκ·Έ μμμ 
						if(requestDTO.getLhTagNames() != null && requestDTO.getLhTagNames().size() > 0) {
							// λΌμ΄λΈ ν΄μνκ·Έ λ±λ‘
							if(apiLiveBean.registBroadcastHashTag(requestDTO)) {
								rsltVO.setRslt(true);
								rsltVO.setRsltMsg("Success Live Regist");
								rsltVO.setRsltDesc("μ±κ³΅μ μΌλ‘ λ±λ‘λμμ΅λλ€.");
								transactionManager.commit(txStatus);
								
							} else {
								rsltVO.setRslt(false);
								rsltVO.setRsltMsg("HashTag Insert Error");
								rsltVO.setRsltDesc("ν΄μνκ·Έ λ±λ‘μ€ μ€λ₯κ° λ°μνμμ΅λλ€.");
								transactionManager.rollback(txStatus);
							}
						// ν΄μνκ·Έ μμ μ 
						} else {
							rsltVO.setRslt(true);
							rsltVO.setRsltMsg("Success Live Regist");
							rsltVO.setRsltDesc("μ±κ³΅μ μΌλ‘ λ±λ‘λμμ΅λλ€.");
							transactionManager.commit(txStatus);
						}
						
					} else {
						rsltVO.setRslt(false);
						rsltVO.setRsltMsg("File Upload Error");
						rsltVO.setRsltDesc("νμΌ μλ‘λ μ€ μ€λ₯κ° λ°μνμμ΅λλ€.");
						transactionManager.rollback(txStatus);
					}
				} else {
					rsltVO.setRslt(false);
					rsltVO.setRsltMsg("Insert Live Job Not Found");
					rsltVO.setRsltDesc("λ±λ‘ν  λΌμ΄λΈ λ°©μ‘ JOB μ΄ μμ΅λλ€.");
					transactionManager.rollback(txStatus);
				}
				
			} else {
				rsltVO.setRslt(false);
				rsltVO.setRsltMsg("Insert Live Not Found");
				rsltVO.setRsltDesc("λ±λ‘ν  λΌμ΄λΈ λ°©μ‘μ΄ μμ΅λλ€.");
				transactionManager.rollback(txStatus);
			}
			
		} catch(Exception e) {
			// DB Transaction Rollback
			transactionManager.rollback(txStatus);
			
			rsltVO.setRslt(false);
			rsltVO.setRsltMsg("DB Insert Error");
			rsltVO.setRsltDesc("λΌμ΄λΈ λ°©μ‘ λ±λ‘μ μ€ν¨νμμ΅λλ€.");
			
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
	 * μ²¨λΆνμΌ ν΅ν© κ²μ¦ λ‘μ§
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
				rsltVO.setRsltDesc("μΈλ€μΌ μ΅λ μλ‘λ ν¬κΈ° (20MB)λ₯Ό μ΄κ³Όνμ¬ λ±λ‘μ΄ μ·¨μλμμ΅λλ€.");
				return rsltVO;
			}
			
			if(!checkThumbnailExtension(thumbnailFile.getOriginalFilename())) {
				rsltVO.setRslt(false);
				rsltVO.setRsltMsg("Invalid File Extension");
				rsltVO.setRsltDesc("μΈλ€μΌ νμΌ νμ₯μκ° μ ν¨νμ§ μμ΅λλ€. \nμ΄λ―Έμ§ νμΌλ§ μλ‘λ κ°λ₯ν©λλ€.");
				return rsltVO;
			} 
		}
		
		if(attachFile != null && !attachFile.isEmpty()) {
			if (attachFile.getSize() > 20000000) {
				rsltVO.setRslt(false);
				rsltVO.setRsltMsg("Exceed Max File Size [attach]");
				rsltVO.setRsltDesc("μ²¨λΆνμΌ μ΅λ μλ‘λ ν¬κΈ° (20MB)λ₯Ό μ΄κ³Όνμ¬ λ±λ‘μ΄ μ·¨μλμμ΅λλ€.");
				return rsltVO;
			}
				
			if(!attachFile.isEmpty() && !checkAttachExtension(attachFile.getOriginalFilename())) {
				rsltVO.setRslt(false);
				rsltVO.setRsltMsg("Invalid File Extension");
				rsltVO.setRsltDesc("μ²¨λΆνμΌμ νμ₯μκ° μ ν¨νμ§ μμ΅λλ€.");
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
//			rsltVO.setRsltDesc("μΈλ€μΌ νμΌμ μ°Ύμ μ μμ΅λλ€. λ€μ λ±λ‘λ°λλλ€.");
//			retval = false;
//		} else {
//			logger.info("request file size => thumbnail : " + thumbnailFile.getSize() + " / attach : " + attachFile.getSize());
//			if(thumbnailFile.getSize() > 20000000)  {
//				rsltVO.setRslt(false);
//				rsltVO.setRsltMsg("Exceed Max File Size [thumbnail]");
//				rsltVO.setRsltDesc("μΈλ€μΌ μ΅λ μλ‘λ ν¬κΈ° (20MB)λ₯Ό μ΄κ³Όνμ¬ λ±λ‘μ΄ μ·¨μλμμ΅λλ€.");
//				retval = false;
//			} else if (attachFile.getSize() > 20000000) {
//				rsltVO.setRslt(false);
//				rsltVO.setRsltMsg("Exceed Max File Size [attach]");
//				rsltVO.setRsltDesc("μ²¨λΆνμΌ μ΅λ μλ‘λ ν¬κΈ° (20MB)λ₯Ό μ΄κ³Όνμ¬ λ±λ‘μ΄ μ·¨μλμμ΅λλ€.");
//				retval = false;
//			}
//			
//			if(!checkThumbnailExtension(thumbnailFile.getOriginalFilename())) {
//				rsltVO.setRslt(false);
//				rsltVO.setRsltMsg("Invalid File Extension");
//				rsltVO.setRsltDesc("μΈλ€μΌ νμΌ νμ₯μκ° μ ν¨νμ§ μμ΅λλ€. \nμ΄λ―Έμ§ νμΌλ§ μλ‘λ κ°λ₯ν©λλ€.");
//				retval = false;
//			} else if(!attachFile.isEmpty() && !checkAttachExtension(attachFile.getOriginalFilename())) {
//				rsltVO.setRslt(false);
//				rsltVO.setRsltMsg("Invalid File Extension");
//				rsltVO.setRsltDesc("μ²¨λΆνμΌμ νμ₯μκ° μ ν¨νμ§ μμ΅λλ€.");
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
	 * μ²¨λΆνμΌ νμ₯μ μ²΄ν¬
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
				
				//λ°©μ‘ λκΈ°
				if(status.equals(LiveBroadcastStatus.Wait.getTitle())) {
					apiLiveBean.deleteBroadcastLb(seq);
					apiLiveBean.deleteBroadcastLbj(seq);
					apiLiveBean.deleteHashTag(seq);
					
				//λ°©μ‘ μλ£ 
				}else if(status.equals(LiveBroadcastStatus.Finished.getTitle())){
					apiLiveBean.deleteBroadcastLb(seq);
					apiLiveBean.deleteHashTag(seq);
					
				}else {
					transactionManager.rollback(txStatus);
					rsltVO.setRslt(false);
					rsltVO.setRsltMsg("cannot delete onAir broadcast");
					rsltVO.setRsltDesc("λΌμ΄λΈ μ€μΈ λ°©μ‘μ μ§μΈ μ μμ΅λλ€.");
					return rsltVO;
				}
			}
			
			transactionManager.commit(txStatus);
			
			rsltVO.setRslt(true);
			rsltVO.setRsltMsg("Success broadcast delete");
			rsltVO.setRsltDesc("λ°©μ‘μ΄ μ­μ λμμ΅λλ€.");
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
			rsltVO.setRsltDesc("λΌμ΄λΈ λ°©μ‘ μκ°μ λ€μ μ€μ ν΄ μ£ΌμΈμ.\nν΄λΉ μκ°λμ λΌμ΄λΈ μΈμ½λ© νμ© κ°μλ₯Ό μ΄κ³Όνμμ΅λλ€.");
			return rsltVO;
		}
		
		rsltVO = checkMultipartFiles(rsltVO, attachFile, thumbnailFile);
		if(!rsltVO.getRslt()) { return rsltVO; }
		
		TransactionStatus txStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
		
		try {
			/*λ΄μ©*/
			if (!apiLiveBean.updateLb(requestDTO)) {
				transactionManager.rollback(txStatus);
				rsltVO.setRslt(false);
				rsltVO.setRsltMsg("Failed to update for live broadcast");
				rsltVO.setRsltDesc("λΌμ΄λΈ λ°©μ‘ μμ μ μ€ν¨νμμ΅λλ€.");
				return rsltVO;
			}
			
			/*μΈλ€μΌ*/
			if(!apiLiveBean.updateThumbnail(requestDTO, thumbnailFile)){
				transactionManager.rollback(txStatus);
				rsltVO.setRslt(false);
				rsltVO.setRsltMsg("Thumnail Upload Error");
				rsltVO.setRsltDesc("μΈλ€μΌ μλ‘λ μ€ μ€λ₯κ° λ°μνμμ΅λλ€.");
				return rsltVO;
			}
			
			/*μ²¨λΆνμΌ*/
			if(!apiLiveBean.updateAttach(requestDTO, attachFile)) {
					transactionManager.rollback(txStatus);
					rsltVO.setRslt(false);
					rsltVO.setRsltMsg("Attach File Upload Error");
					rsltVO.setRsltDesc("μ²¨λΆνμΌ μλ‘λ μ€ μ€λ₯κ° λ°μνμμ΅λλ€.");
					return rsltVO;
			}
			
			/*νκ·Έ*/
			apiLiveBean.deleteHashTag(requestDTO.getLbSeq());
			if(requestDTO.getLhTagNames() != null && requestDTO.getLhTagNames().size() > 0) {
				if(!apiLiveBean.registBroadcastHashTag(requestDTO)) {
					transactionManager.rollback(txStatus);
					rsltVO.setRslt(false);
					rsltVO.setRsltMsg("HashTag Insert Error");
					rsltVO.setRsltDesc("ν΄μνκ·Έ λ±λ‘μ€ μ€λ₯κ° λ°μνμμ΅λλ€.");
					return rsltVO;
				}
			}
			
			transactionManager.commit(txStatus);
			
			rsltVO.setRslt(true);
			rsltVO.setRsltMsg("Success broadcast update");
			rsltVO.setRsltDesc("λΌμ΄λΈ λ°©μ‘μ΄ μμ λμμ΅λλ€.");
			
			return rsltVO;
			
		} catch(Exception e) {
			// DB Transaction Rollback
			transactionManager.rollback(txStatus);
			
			rsltVO.setRslt(false);
			rsltVO.setRsltMsg("Fail broadcast update");
			rsltVO.setRsltDesc("λΌμ΄λΈ λ°©μ‘ μμ μ μ€ν¨νμμ΅λλ€.");
			
			e.printStackTrace();
			for(StackTraceElement st : e.getStackTrace()) {
				if(st.toString().startsWith("com.kdy.app")) {
					logger.error(st.toString());
				}
			}
		}
		
		return rsltVO;
	}

	//λΌμ΄λΈ μμ μ λ³΄ κ°μ Έμ€κΈ°
	@Override
	public AppBroadcastVO getLiveVideoData(AppBroadcastVO vo) throws Exception {
		vo.setLiveStreamUrl(memoryVO.getLiveStreamingUri());
		return vo;
	}

	//λΌμ΄λΈ μ’λ£
	@Override
	public ResultVO liveBroadcastStatusEnd(String lbSeq) throws Exception {
		ResultVO resultVo = new ResultVO();
		
		if(apiLiveBean.liveBroadcastStatusEnd(lbSeq)) {
			resultVo.setRslt(true);
			resultVo.setRsltMsg("SUCCESS");
			resultVo.setRsltDesc("λΌμ΄λΈ λ°©μ‘μ΄ μ’λ£λμμ΅λλ€.");
		} else {
			resultVo.setRslt(false);
			resultVo.setRsltMsg("Update Live Status Not Found");
			resultVo.setRsltDesc("λΌμ΄λΈ λ°©μ‘μ μ’λ£νλλ° μ€ν¨νμμ΅λλ€.");
		}
		return resultVo;
	}

	//λΌμ΄λΈ μ±ν λ€μ΄λ‘λ
	@Override
	public AppBroadcastVO getLiveInfo(String lbSeq) throws Exception {
		return apiLiveBean.getLiveInfo(lbSeq);
	}
	
	//λΌμ΄λΈ μΌμμ€μ§ λ° μ¬μμ
	@Override
	public ResultVO livePauseAndStart(AppBroadcastVO vo) throws Exception {
		ResultVO resultVo = new ResultVO();
		
		if(apiLiveBean.livePauseAndStart(vo)) {
			//1: λ°©μ‘μ€ 4: μ¬μμ
			if(vo.getLbStatus().equals("1") || vo.getLbStatus().equals("4")) {
				apiLiveBean.pauseDateUpdate(vo.getLbSeq());
			}
			resultVo.setRslt(true);
			resultVo.setRsltMsg("SUCCESS");
			resultVo.setRsltDesc("λΌμ΄λΈ λ°©μ‘μ μΌμμ€μ§ νμ΅λλ€.");
		} else {
			resultVo.setRslt(false);
			resultVo.setRsltMsg("Update Live Status Not Found");
			resultVo.setRsltDesc("λ°©μ‘ μΌμμ€μ§μ μ€ν¨νμ΅λλ€.");
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

	//LIVE μν νμΈ
	@Override
	public String liveStatusCheck(String lbSeq) throws Exception {
		return apiLiveBean.liveStatusCheck(lbSeq);
	}

	//λ°©μ‘ λͺ©λ‘ (excel μ©)
	@Override
	public ResponseBroadcastListExcelDTO getBroadcastListExcel(RequestBroadcastListDTO requestDTO) throws Exception {
		ResponseBroadcastListExcelDTO res = new ResponseBroadcastListExcelDTO();
		res.setBroadcasts(apiLiveBean.getBroadcastListExcel(requestDTO));
		res.setRequest(requestDTO);
		return res;
	}
}
