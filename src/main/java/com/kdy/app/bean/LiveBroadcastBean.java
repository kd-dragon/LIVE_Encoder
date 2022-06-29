package com.kdy.app.bean;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.kdy.app.bean.dao.GetSequenceDAO;
import com.kdy.app.bean.dao.HashTagDAO;
import com.kdy.app.bean.dao.LiveBroadcastDAO;
import com.kdy.app.bean.dao.LiveBroadcastFileDAO;
import com.kdy.app.bean.util.MultiSlashChk;
import com.kdy.app.bean.util.RandomGUID;
import com.kdy.app.bean.util.ResizeImage;
import com.kdy.app.dto.live.AppBroadcastListDTO;
import com.kdy.app.dto.live.AppBroadcastStatusVO;
import com.kdy.app.dto.live.AppBroadcastVO;
import com.kdy.app.dto.live.CategoryVO;
import com.kdy.app.dto.watermark.WatermarkVO;
import com.kdy.live.bean.util.TimeExpressUtil;
import com.kdy.live.dto.LiveSchedMemoryVO;
import com.kdy.live.dto.LiveSchedMemoryVO.RedisHashKeyword;
import com.kdy.live.dto.live.ChatDTO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LiveBroadcastBean {
	
	private final Logger logger = LoggerFactory.getLogger(LiveBroadcastBean.class);

	private final LiveBroadcastDAO dao;
	private final LiveBroadcastFileDAO fileDao;
	private final GetSequenceDAO seqDao;
	private final HashTagDAO tagDao;
	private final LiveSchedMemoryVO memoryVO;
	
	@Qualifier("redisTemplateObject")
	private RedisTemplate<String, Object> redisTemplate;
	
	//방송중 목록
	public List<AppBroadcastVO> getOnAirList(AppBroadcastListDTO dto) throws Exception{
		
		List<AppBroadcastVO> rtnList = dao.selectOnAirList(dto);
		
		for(AppBroadcastVO vo: rtnList) {
			setDurationFormat(vo);
		}
				
		return rtnList;
	}

	//방송중 목록 갯수
	public int getOnAirListCount(AppBroadcastListDTO dto) throws Exception {
		return dao.selectOnAirListCount(dto);
	}
	
	//방송 대기 목록
	public List<AppBroadcastVO> getWaitList(AppBroadcastListDTO dto) throws Exception{
		
		List<AppBroadcastVO> rtnList = dao.selectWaitList(dto);
		
		for(AppBroadcastVO vo: rtnList) {
			setDurationFormat(vo);
		}
				
		return rtnList;
	}

	//방송 대기 목록 갯수
	public int getWaitListCount(AppBroadcastListDTO dto) throws Exception {
		return dao.selectWaitListCount(dto);
	}
	
	//방송 삭제 (tglive_broadcast)
	public int removeBroadcast_lb(String lbSeq) throws Exception {
		return dao.updateLbDelYn(lbSeq);
	}
	
	//방송 삭제 (tglive_broadcast_job)
	public int removeBroadcast_lbj(String lbSeq) throws Exception{
		return dao.deleteLbj(lbSeq);
	}
	
	//방송 상태
	public String getLpStatus(String lbSeq) throws Exception {
		return dao.selectLpStatus(lbSeq);
	}
	//방송 정보 가져오기
	public AppBroadcastVO getBroadcast(String lbSeq) throws Exception {
		return dao.selectBroadcast(lbSeq);
	}
	
	//방송 수정 - 내용
	public boolean modifyBroadcast(AppBroadcastVO lbvo) throws Exception{
		return dao.updateBroadcast(lbvo) >  0;
	}
	
	//방송 수정 - 첨부파일
	public boolean modifyAttachFile(AppBroadcastVO lbvo, MultipartFile attachFile) throws Exception{
		// 서버 파일명
		RandomGUID myGUID = new RandomGUID();
		// 날짜
		String year = new SimpleDateFormat("yyyy").format(new Date());
		String month = new SimpleDateFormat("MM").format(new Date());
		
		//새로운 첨부파일 있을 때
		if(!attachFile.isEmpty()){
			
			//기존에 첨부했던 첨부파일이 있을 경우
			if(lbvo.getRemoveFileFlag().equals("Y")){
				//기존 첨부파일 탐색
				AppBroadcastVO findFile = dao.selectFile(lbvo.getLbSeq());
				//물리 파일 삭제
				File file = new File(MultiSlashChk.path(findFile.getLbfAttachPath() + File.separator + findFile.getLbfAttachServerNm()));
				if (file.isFile()){ file.delete(); }
			}
			
			String attachFilePath = memoryVO.getEtcFileUploadPath() + "attach" + File.separator + year + File.separator + month;
			File attachDirectory = new File(MultiSlashChk.path(attachFilePath));
			
			if(!attachDirectory.isDirectory()) {
				attachDirectory.mkdirs();
			}
			
			String attachExt = attachFile.getOriginalFilename().substring(attachFile.getOriginalFilename().lastIndexOf(".") );
			String attachServerFileName = myGUID.toString() + attachExt;
			attachFile.transferTo(new File(MultiSlashChk.path(attachFilePath + File.separator + attachServerFileName)));
			
			lbvo.setLbfAttachPath(MultiSlashChk.path(attachFilePath));
			lbvo.setLbfAttachOriginalNm(attachFile.getOriginalFilename());
			lbvo.setLbfAttachServerNm(attachServerFileName);
			
			return dao.updateAttachFile(lbvo) > 0;
		
		//새로운 첨부파일이 없을 때
		}else{
			
			//기존에 첨부했던 파일은 지운 경우
			if(lbvo.getRemoveFileFlag().equals("Y")){
				//기존 첨부파일 탐색
				AppBroadcastVO findFile = dao.selectFile(lbvo.getLbSeq());
				//물리 파일 삭제
				File file = new File(MultiSlashChk.path(findFile.getLbfAttachPath() + File.separator + findFile.getLbfAttachServerNm()));
				if (file.isFile()){ file.delete(); }
				
				lbvo.setLbfAttachPath(null);
				lbvo.setLbfAttachOriginalNm(null);
				lbvo.setLbfAttachServerNm(null);
				
				return dao.updateAttachFile(lbvo) > 0;
			}
		}
		return false;
	}
	
	//방송 수정 - 썸네일
	public boolean modifyThumbnail(AppBroadcastVO lbvo, MultipartFile thumbnailFile) throws Exception{
		
		// 공통 서버 파일명
		RandomGUID myGUID = new RandomGUID();
		// 공통 날짜
		String year = new SimpleDateFormat("yyyy").format(new Date());
		String month = new SimpleDateFormat("MM").format(new Date());
		
		//썸네일을 삭제했고 새 파일을 첨부했을 때
		if(lbvo.getRemoveThumbnailFlag().equals("Y") && !thumbnailFile.isEmpty()) {
			
			//기존 썸네일 탐색
			AppBroadcastVO findFile = dao.selectFile(lbvo.getLbSeq());
			//파일 삭제
			File file = new File(MultiSlashChk.path(findFile.getLbfImgPath() + File.separator + findFile.getLbfImgServerNm()));
			if (file.isFile()){ file.delete(); }
			
			//인코딩 파일 삭제
			file = new File(MultiSlashChk.path(findFile.getLbfImgEncPath() + File.separator + findFile.getLbfImgEncNm()));
			if (file.isFile()){ file.delete(); }
			
			String thumbOriginalPath = memoryVO.getEtcFileUploadPath() + "thumb/original/" + year + "/" + month;
			String thumbEncodingPath = memoryVO.getEtcFileUploadPath() + "thumb/encoding/" + year + "/" + month;
			
			
			File thumbOriginalDirectory = new File(MultiSlashChk.path(thumbOriginalPath));
			File thumbEncodingDirectory = new File(MultiSlashChk.path(thumbEncodingPath));
			
			if(!thumbOriginalDirectory.isDirectory()) {
				thumbOriginalDirectory.mkdirs();
			}
			if(!thumbEncodingDirectory.isDirectory()) {
				thumbEncodingDirectory.mkdirs();
			}
			
			String thumbExt = thumbnailFile.getOriginalFilename().substring(thumbnailFile.getOriginalFilename().lastIndexOf(".") );
			String thumbServerFileName= myGUID.toString() + thumbExt;
			File thumbOriginalFile = new File(MultiSlashChk.path(thumbOriginalPath + File.separator + thumbServerFileName));
			File thumbEncodingFile = new File(MultiSlashChk.path(thumbEncodingPath + File.separator + thumbServerFileName));
			
			// original image file 
			thumbnailFile.transferTo(thumbOriginalFile);
			lbvo.setLbfImgPath(MultiSlashChk.path(thumbOriginalPath));
			lbvo.setLbfImgServerNm(thumbServerFileName);
			lbvo.setLbfImgOriginalNm(thumbnailFile.getOriginalFilename());
			
			// encoding image file
			ResizeImage.resizePngImages(thumbOriginalFile, thumbEncodingFile, 852, 0);
			lbvo.setLbfImgEncNm(thumbServerFileName);
			lbvo.setLbfImgEncPath(MultiSlashChk.path(thumbEncodingPath));
			
			return dao.updateThumbnail(lbvo) > 0;
		}
		return false;
	}
	
	//방송 완료 목록
	public List<AppBroadcastVO> getFinishList(AppBroadcastListDTO dto) throws Exception{
		
		List<AppBroadcastVO> rtnList = dao.selectFinishList(dto);
		
		for(AppBroadcastVO vo: rtnList) {
			setDurationFormat(vo);
		}
				
		return rtnList;
	}

	//방송 완료 목록 갯수
	public int getFinishListCount(AppBroadcastListDTO dto) throws Exception {
		return dao.selectFinishListCount(dto);
	}
	
	public List<AppBroadcastStatusVO> getOnAirStatusList(AppBroadcastListDTO dto) throws Exception {
		return dao.selectOnAirStatusList(dto);
	}
	
	public AppBroadcastVO getBroadcastDetail(AppBroadcastVO vo) throws Exception {
		return dao.selectBroadcastDetail(vo);
	}
	
	public boolean registBroadcast(AppBroadcastVO lbvo) throws Exception {
		lbvo.setLbSeq(seqDao.getSeq_yyyymmdd("sequence_lb_12"));
		return dao.insertBroadcast(lbvo) > 0;
	}
	
	public Boolean registBroadcastFile(AppBroadcastVO lbvo, MultipartFile attachFile, MultipartFile thumbnailFile) throws Exception {
		// 공통 서버 파일명
		RandomGUID myGUID = new RandomGUID();
		// 공통 날짜
		String year = new SimpleDateFormat("yyyy").format(new Date());
		String month = new SimpleDateFormat("MM").format(new Date());
		
		String thumbOriginalPath = memoryVO.getEtcFileUploadPath() + "thumb/original/" + year + "/" + month;
		String thumbEncodingPath = memoryVO.getEtcFileUploadPath() + "thumb/encoding/" + year + "/" + month;
		
		
		File thumbOriginalDirectory = new File(MultiSlashChk.path(thumbOriginalPath));
		File thumbEncodingDirectory = new File(MultiSlashChk.path(thumbEncodingPath));
		
		if(!thumbOriginalDirectory.isDirectory()) {
			thumbOriginalDirectory.mkdirs();
		}
		if(!thumbEncodingDirectory.isDirectory()) {
			thumbEncodingDirectory.mkdirs();
		}
		
		String thumbExt = thumbnailFile.getOriginalFilename().substring(thumbnailFile.getOriginalFilename().lastIndexOf(".") );
		String thumbServerFileName= myGUID.toString() + thumbExt;
		File thumbOriginalFile = new File(MultiSlashChk.path(thumbOriginalPath + File.separator + thumbServerFileName));
		File thumbEncodingFile = new File(MultiSlashChk.path(thumbEncodingPath + File.separator + thumbServerFileName));
		
		// original image file 
		thumbnailFile.transferTo(thumbOriginalFile);
		lbvo.setLbfImgPath(MultiSlashChk.path(thumbOriginalPath));
		lbvo.setLbfImgServerNm(thumbServerFileName);
		lbvo.setLbfImgOriginalNm(thumbnailFile.getOriginalFilename());
		
		// encoding image file
		ResizeImage.resizePngImages(thumbOriginalFile, thumbEncodingFile, 852, 0);
		lbvo.setLbfImgEncNm(thumbServerFileName);
		lbvo.setLbfImgEncPath(MultiSlashChk.path(thumbEncodingPath));
		
		
		// 첨부파일 있을시에만 처리 
		if(!attachFile.isEmpty()) {
			String attachFilePath = memoryVO.getEtcFileUploadPath() + "attach" + File.separator + year + File.separator + month;
			File attachDirectory = new File(MultiSlashChk.path(attachFilePath));
			
			if(!attachDirectory.isDirectory()) {
				attachDirectory.mkdirs();
			}
			
			String attachExt = attachFile.getOriginalFilename().substring(attachFile.getOriginalFilename().lastIndexOf(".") );
			String attachServerFileName = myGUID.toString() + attachExt;
			attachFile.transferTo(new File(MultiSlashChk.path(attachFilePath + File.separator + attachServerFileName)));
			
			lbvo.setLbfAttachPath(MultiSlashChk.path(attachFilePath));
			lbvo.setLbfAttachOriginalNm(attachFile.getOriginalFilename());
			lbvo.setLbfAttachServerNm(attachServerFileName);
		}

		return fileDao.insertBroadcastFile(lbvo) > 0;
	}
	
	public boolean registBroadcastJob(AppBroadcastVO lbvo) throws Exception {
		lbvo.setLbjProcessId("0");
		lbvo.setLbjDuration("0");
		return dao.insertBroadcastJob(lbvo) > 0;
	}
	
	public AppBroadcastVO getBroadcastFile(AppBroadcastVO vo) throws Exception {
		return fileDao.selectFileBySeq(vo);
	}
	
	public boolean registBroadcastHashTag(AppBroadcastVO vo) throws Exception {
		return tagDao.insertHashTag(vo) > 0;
	}

	//태그 삭제
	public boolean removeHashTag(String lbSeq) throws Exception {
		return tagDao.deleteHashTag(lbSeq) > 0;
	}

	//카테고리 list
	public List<CategoryVO> getCategoryList(int rootUpCategoryCode) throws Exception {
		return dao.selectCategoryList(rootUpCategoryCode);
	}
	
	//라이브 종료
	public String liveBroadcastStatusEnd(String lbSeq) throws Exception{
		if(dao.liveBroadcastStatusEnd(lbSeq)>0) {
			return "SUCCESS";
		}
		return "FAIL";
	}
	
	private void setDurationFormat(AppBroadcastVO vo) {
		
		int curDuration = 0;
		
		try {
			curDuration = vo.getLbjDuration() != null ? Integer.parseInt(vo.getLbjDuration()) : 0;
		} catch(Exception e) {
			logger.warn(vo.getLbTitle() + " ### Fail Duration Formatting ###");
		}
		vo.setLbjDurationFormat(TimeExpressUtil.secToHhmmss(curDuration, "시간 ", "분 ", "초 "));
	}
	
	//워터마크 설정
	public WatermarkVO getWatermark(WatermarkVO watermarkVo) throws Exception {
		return dao.selectWatermark(watermarkVo);
	}
	
	//라이브 채팅 다운로드 관련
	public AppBroadcastVO getLiveInfo(String lbSeq) throws Exception {
		return dao.getLiveInfo(lbSeq);
	}

	public boolean checkEncoderWorkerCnt(AppBroadcastVO lbvo) throws Exception {
		return dao.checkEncoderWorkerCnt(lbvo);
	}
	
	//라이브 일시중지 및 재시작
	public int livePauseAndStart(AppBroadcastVO vo) throws Exception {
		return dao.livePauseAndStart(vo);
	}
	
	//라이브, 재시작 >> 일시중지 시 Date Update
	public int pauseDateUpdate(AppBroadcastVO vo) throws Exception {
		return dao.pauseDateUpdate(vo);
	}
	
	//LIVE 상태 확인
	public String liveStatusCheck(String lbSeq) throws Exception{
		
		ValueOperations<String, Object> valueOperation = redisTemplate.opsForValue();
		String key = RedisHashKeyword.LIVESTATUS.toString() + lbSeq;
		String retval = (String) valueOperation.get(key);
		if(retval == null || retval.equals("")) {
			logger.warn("********* DB STATUS CHECK [NO DATA IN REDIS] ********");
			retval = dao.liveStatusCheck(lbSeq);
		}
		return retval;
	}

	public List<AppBroadcastVO> getFinishListExcel(AppBroadcastListDTO dto) throws Exception {
		return dao.getFinishListExcel(dto);
	}

	public List<AppBroadcastVO> getWaitListExcel(AppBroadcastListDTO dto) throws Exception {
		return dao.getWaitListExcel(dto);
	}

	public List<AppBroadcastVO> getOnAirListExcel(AppBroadcastListDTO dto) throws Exception{
		return dao.getOnAirListExcel(dto);
	}
	
	//라이브 종료 (스트리밍 오류)
	public String liveErrorStatusEnd(String lbSeq) throws Exception{
		if(dao.liveErrorStatusEnd(lbSeq)>0) {
			return "SUCCESS";
		}
		return "FAIL";
	}
	
	//라이브 재시작요청 (스트리밍 오류)
	public String liveErrorStatusRestart(String lbSeq) throws Exception{
		if(dao.liveErrorStatusRestart(lbSeq)>0) {
			return "SUCCESS";
		}
		return "FAIL";
	}
	
	public String getDynamicRecordEnable(String lbSeq) throws Exception{
		String retval = "";
			
		try {
			ValueOperations<String, Object> valueOperation = redisTemplate.opsForValue();
			String key = RedisHashKeyword.RECENABLE.toString() + lbSeq;
		
			retval = valueOperation.get(key).toString();
		}catch (NullPointerException e) {
			return "";
		} catch (Exception e) {
			logger.error("getDynamicRecordEnable() error : " + e);
		}
		
		return retval;
	}
	
	public String updateDynamicRecordEnable(String lbSeq) throws Exception{
		ValueOperations<String, Object> valueOperation = redisTemplate.opsForValue();
		String key = RedisHashKeyword.RECENABLE.toString() + lbSeq;
		String key2 = RedisHashKeyword.RECSTARTDATE.toString() + lbSeq;
		String key3 = RedisHashKeyword.RECDURATION.toString() + lbSeq;
		
		String retval = "SUCCESS";
		
		try {
			if(valueOperation.get(key) != null) {
				if(valueOperation.get(key).toString().equalsIgnoreCase("Y")) {
					valueOperation.set(key, "N", 3, TimeUnit.DAYS);
					
					if(memoryVO.getChatRecYn().equalsIgnoreCase("Y")) {
						if(valueOperation.get(key3) != null) {
							
							long recStartDate = Long.parseLong(valueOperation.get(key2).toString());
							long recDuration = Long.parseLong(valueOperation.get(key3).toString());
							long totalDuration = recDuration + (((System.currentTimeMillis() - recStartDate)/1000));
							valueOperation.set(key3, totalDuration, 3, TimeUnit.DAYS);
						}
						
						if(valueOperation.get(key2) != null) {
							valueOperation.set(key2, 0, 3, TimeUnit.DAYS);
						}
					}
					
				} else {
					valueOperation.set(key, "Y", 3, TimeUnit.DAYS);
					
					if(memoryVO.getChatRecYn().equalsIgnoreCase("Y")) {
						if(valueOperation.get(key2) != null) {
							valueOperation.set(key2, System.currentTimeMillis(), 3, TimeUnit.DAYS);
						}
					}

					
					
				}
				
			} else {
				retval = "NULL";
			}
			
		} catch(Exception e) {
			logger.error("error : \n{}", e);
			retval = "FAIL";
		}
		return retval;
		
	}

	public List<ChatDTO> getChatHistory(ChatDTO dto) throws Exception {
		return dao.getChatHistory(dto);
	}
}
