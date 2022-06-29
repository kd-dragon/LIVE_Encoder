package com.kdy.app.bean.api;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.kdy.app.bean.dao.GetSequenceDAO;
import com.kdy.app.bean.dao.HashTagDAO;
import com.kdy.app.bean.dao.LiveBroadcastFileDAO;
import com.kdy.app.bean.dao.api.ApiLiveBroadcastDAO;
import com.kdy.app.bean.util.MultiSlashChk;
import com.kdy.app.bean.util.RandomGUID;
import com.kdy.app.bean.util.ResizeImage;
import com.kdy.app.dto.api.request.RequestBroadcastExcelDTO;
import com.kdy.app.dto.api.request.RequestBroadcastInsertDTO;
import com.kdy.app.dto.api.request.RequestBroadcastListDTO;
import com.kdy.app.dto.api.request.RequestBroadcastUpdateDTO;
import com.kdy.app.dto.api.request.RequestUserBroadcastListDTO;
import com.kdy.app.dto.api.response.ResponseBroadcastDetailDTO;
import com.kdy.app.dto.live.AppBroadcastStatusVO;
import com.kdy.app.dto.live.AppBroadcastVO;
import com.kdy.live.bean.util.TimeExpressUtil;
import com.kdy.live.dto.LiveSchedMemoryVO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ApiLiveBroadcastBean {
	
	private final Logger logger = LoggerFactory.getLogger(ApiLiveBroadcastBean.class);

	private final ApiLiveBroadcastDAO dao;
	private final LiveBroadcastFileDAO fileDao;
	private final HashTagDAO tagDao;
	private final GetSequenceDAO seqDao;
	private final LiveSchedMemoryVO memoryVO;
	
	
	//API 방송 목록
	public List<AppBroadcastVO> getBroadcastList(RequestBroadcastListDTO dto) throws Exception{
		List<AppBroadcastVO> rtnList = dao.selectBroadcastList(dto);
		
		for(AppBroadcastVO vo : rtnList) {
			setDurationFormat(vo);
		}
		return rtnList;
	}

	//API 방송 목록 갯수
	public int getBroadcastListCount(RequestBroadcastListDTO dto) throws Exception {
		return dao.selectBroadcastListCount(dto);
	}
	
	//API 방송 상세
	public ResponseBroadcastDetailDTO getBroadcast(AppBroadcastVO vo) throws Exception {
		return dao.selectBroadcastDetail(vo);
	}
	
	//API 방송 목록 엑셀다운로드
	public List<AppBroadcastVO> getBroadcastListExcel(RequestBroadcastExcelDTO requestDTO) throws Exception{
		return dao.selectBroadcastListExcel(requestDTO);
	}
	
	//API 방송 등록
	public boolean registBroadcast(RequestBroadcastInsertDTO requestDTO) throws Exception {
		requestDTO.setLbSeq(seqDao.getSeq_yyyymmdd("sequence_lb_12"));
		return dao.insertBroadcast(requestDTO) > 0;
	}
	
	public Boolean registBroadcastFile(RequestBroadcastInsertDTO requestDTO, MultipartFile attachFile, MultipartFile thumbnailFile) throws Exception {
		
		
		AppBroadcastVO lbvo = new AppBroadcastVO();
		lbvo.setLbSeq(requestDTO.getLbSeq());
		
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
		if(attachFile != null && !attachFile.isEmpty()) {
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
		}else {
			lbvo.setLbfAttachPath(null);
			lbvo.setLbfAttachOriginalNm(null);
			lbvo.setLbfAttachServerNm(null);
		}
		
		return fileDao.insertBroadcastFile(lbvo) > 0;
	}
	
	public boolean registBroadcastJob(RequestBroadcastInsertDTO requestDTO) throws Exception {
		return dao.insertBroadcastJob(requestDTO) > 0;
	}
	
	public boolean registBroadcastHashTag(RequestBroadcastInsertDTO requestDTO) throws Exception {
		AppBroadcastVO vo = new AppBroadcastVO();
		vo.setLbSeq(requestDTO.getLbSeq());
		vo.setLhTagNames(requestDTO.getLhTagNames());
		return tagDao.insertHashTag(vo) > 0;
	}
	
	public boolean registBroadcastHashTag(RequestBroadcastUpdateDTO requestDTO) throws Exception {
		AppBroadcastVO vo = new AppBroadcastVO();
		vo.setLbSeq(requestDTO.getLbSeq());
		vo.setLhTagNames(requestDTO.getLhTagNames());
		return tagDao.insertHashTag(vo) > 0;
	}
	

	public String getLbStatus(String id) throws Exception {
		return dao.selectLbStatus(id);
	}

	public int deleteBroadcastLb(String id) throws Exception {
		return dao.updateLbDelYn(id);
	}

	public int deleteBroadcastLbj(String id) throws Exception {
		return dao.deleteLbj(id);
	}

	public int deleteHashTag(String seq) throws Exception {
		return tagDao.deleteHashTag(seq);
	}

	public boolean updateLb(RequestBroadcastUpdateDTO requestDTO) throws Exception {
		return dao.updateLb(requestDTO) > 0;
	}

	public boolean updateThumbnail(RequestBroadcastUpdateDTO requestDTO, MultipartFile thumbnailFile) throws Exception {
		
		AppBroadcastVO lbvo = new AppBroadcastVO();
		lbvo.setLbSeq(requestDTO.getLbSeq());
		
		//썸네일을 삭제, 새 썸네일 첨부 -> 썸네일 변경
		if(requestDTO.getRemoveThumbnailFlag().equals("Y")) {
			
			if(thumbnailFile != null && !thumbnailFile.isEmpty()) {
				
				/**기존 썸네일 파일 삭제*/
				AppBroadcastVO findFile = fileDao.selectFile(requestDTO.getLbSeq());
				File file = new File(MultiSlashChk.path(findFile.getLbfImgPath() + File.separator + findFile.getLbfImgServerNm()));
				if (file.isFile()){ file.delete(); }
				file = new File(MultiSlashChk.path(findFile.getLbfImgEncPath() + File.separator + findFile.getLbfImgEncNm()));
				if (file.isFile()){ file.delete(); }
				
				RandomGUID myGUID = new RandomGUID();
				
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
				
				return fileDao.updateThumbnail(lbvo);
			}
			
		}
		//썸네일 삭제를 안햇을 경우 썸네일 변경x -> 기존 썸네일 유지
		return true;
	}
	
	public boolean updateAttach(RequestBroadcastUpdateDTO requestDTO, MultipartFile attachFile) throws Exception {
		
		AppBroadcastVO lbvo = new AppBroadcastVO();
		lbvo.setLbSeq(requestDTO.getLbSeq());
		
		/**기존 첨부파일을 지웠을 때*/
		if(requestDTO.getRemoveAttachFlag().equals("Y")) {
			/**파일 삭제*/
			AppBroadcastVO findFile = fileDao.selectFile(requestDTO.getLbSeq());
			File file = new File(MultiSlashChk.path(findFile.getLbfAttachPath() + File.separator + findFile.getLbfAttachServerNm()));
			if (file.isFile()){ file.delete(); }
		}
		
		//새로운 첨부파일 o
		if(attachFile != null && !attachFile.isEmpty()) {
			RandomGUID myGUID = new RandomGUID();
			String year = new SimpleDateFormat("yyyy").format(new Date());
			String month = new SimpleDateFormat("MM").format(new Date());
			
			String attachFilePath = memoryVO.getEtcFileUploadPath() + "attach" + File.separator + year + File.separator + month;
			File attachDirectory = new File(MultiSlashChk.path(attachFilePath));
			
			if(!attachDirectory.isDirectory()) { attachDirectory.mkdirs();
			}
			
			String attachExt = attachFile.getOriginalFilename().substring(attachFile.getOriginalFilename().lastIndexOf(".") );
			String attachServerFileName = myGUID.toString() + attachExt;
			attachFile.transferTo(new File(MultiSlashChk.path(attachFilePath + File.separator + attachServerFileName)));
			
			lbvo.setLbfAttachPath(MultiSlashChk.path(attachFilePath));
			lbvo.setLbfAttachOriginalNm(attachFile.getOriginalFilename());
			lbvo.setLbfAttachServerNm(attachServerFileName);
		
			return fileDao.updateAttach(lbvo);
			
		//새로운 첨부파일 x
		}else {
			
			if(requestDTO.getRemoveAttachFlag().equals("Y")){
				lbvo.setLbfAttachPath(null);
				lbvo.setLbfAttachOriginalNm(null);
				lbvo.setLbfAttachServerNm(null);
				
				return fileDao.updateAttach(lbvo);
			}
		}
		
		return true;
	}

	public boolean checkEncoderWorkerCnt(Object dto) throws Exception {
		return dao.checkEncoderWorkerCnt(dto);
	}

	public List<AppBroadcastStatusVO> getOnAirStatusList(RequestBroadcastListDTO dto) throws Exception {
		return dao.selectOnAirStatusList(dto);
	}
	
	//라이브 종료
	public boolean liveBroadcastStatusEnd(String lbSeq) throws Exception {
		return dao.liveBroadcastStatusEnd(lbSeq) > 0;
	}
	
	//라이브 채팅 다운로드
	public AppBroadcastVO getLiveInfo(String lbSeq) throws Exception {
		return dao.getLiveInfo(lbSeq);
	}
	
	//라이브 일시중지 및 재시작
	public boolean livePauseAndStart(AppBroadcastVO vo) throws Exception {
		return dao.livePauseAndStart(vo) > 0;
	}
	
	//라이브, 재시작 >> 일시중지 시 Date Update
	public boolean pauseDateUpdate(String lbSeq) throws Exception {
		return dao.pauseDateUpdate(lbSeq) > 0;
	}
	
	
	//라이브 중인 리스트 가져오기
	public List<AppBroadcastVO> getLiveStatusList(RequestUserBroadcastListDTO dto) throws Exception{
		return dao.getLiveStatusList(dto);
	}
	
	//LIVE 상태 확인
	public String liveStatusCheck(String lbSeq) throws Exception{
		return dao.liveStatusCheck(lbSeq);
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

	// 방송 목록 (excel 용)
	public List<AppBroadcastVO> getBroadcastListExcel(RequestBroadcastListDTO requestDTO) throws Exception {
		return dao.getBroadcastListExcel(requestDTO);
	}
}
