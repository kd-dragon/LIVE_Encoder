package com.kdy.live.bean.vod;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.kdy.app.bean.util.FileCopyUploadBean;
import com.kdy.live.dao.vod.VodManageDAOFactory;
import com.kdy.live.dto.LiveSchedMemoryVO;
import com.kdy.live.dto.live.LiveBroadcastVO;
import com.kdy.live.dto.vod.VodMetaVO;

@Component
@RequiredArgsConstructor
public class LiveVodSaveBean { //라이브 VOD 저장시
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private final VodManageDAOFactory vodManageDAOFactory;
	private final MetaInfoBean vodMetaInfoBean; //메타정보 가져오기
	private final FileCopyUploadBean fileCopyBean;
	private final LiveSchedMemoryVO memoryVO;

	//녹화영상 복사하기 (recording_file_path to encoding_file_path) (사용X)
	public void recordingVodCopyToEncodingPath(LiveBroadcastVO lbvo) throws Exception {
		
		// 복사할 파일 - > 이동경로 , 파일명   
		fileCopyBean.service(lbvo.getVodSavePath(), lbvo.getRecordCopyPath(), lbvo.getRecordCopyName() +"high.mp4");
	}
	
	//메타 정보 DB insert
	public void insertVodMeta(LiveBroadcastVO vo) throws Exception {
		logger.info("LiveVodSaveBean insertVodMeta() - Started");
		
		String savePath = vo.getVodSavePath() + vo.getLbSeq();
		
		try {
		
			VodMetaVO mvo = null;
			mvo = vodMetaInfoBean.getParsedMetaInfo(savePath , vo.getRecordCopyName() + "high.mp4");
			vo.setVodSize(mvo.getMetaFileSize()); //영상 사이즈
			vo.setVodDuration(mvo.getMetaDuration()); //영상 시간
			mvo.setQuality("high"); //영상 화질 추후 하드코딩 부분 수정 예정
			mvo.setEncodeFileName(vo.getRecordCopyName() + "high.mp4");
			mvo.setVodSavePath(savePath);
			mvo.setVodSeq(vo.getVodSeq());
			
			//Meta 정보 insert (고화질) 
			vodManageDAOFactory.getDAO().insertVodMeta(mvo);
			
			if(memoryVO.getIsAdaptive()) {
			
				mvo = vodMetaInfoBean.getParsedMetaInfo(savePath , vo.getRecordCopyName() + "low.mp4");
				vo.setVodSize(mvo.getMetaFileSize()); //영상 사이즈
				vo.setVodDuration(mvo.getMetaDuration()); //영상 시간
				mvo.setQuality("low"); //영상 화질
				mvo.setEncodeFileName(vo.getRecordCopyName() + "low.mp4");
				mvo.setVodSavePath(savePath);
				mvo.setVodSeq(vo.getVodSeq());
				
				//Meta 정보 insert (저화질)
				vodManageDAOFactory.getDAO().insertVodMeta(mvo);
				
				if(memoryVO.getEncodingType().equalsIgnoreCase("advance")) {
					mvo = vodMetaInfoBean.getParsedMetaInfo(savePath , vo.getRecordCopyName() + "mid.mp4");
					vo.setVodSize(mvo.getMetaFileSize()); //영상 사이즈
					vo.setVodDuration(mvo.getMetaDuration()); //영상 시간
					mvo.setQuality("mid"); //영상 화질
					mvo.setEncodeFileName(vo.getRecordCopyName() + "mid.mp4");
					mvo.setVodSavePath(savePath);
					mvo.setVodSeq(vo.getVodSeq());
					
					//Meta 정보 insert (중화질)
					vodManageDAOFactory.getDAO().insertVodMeta(mvo);
				}
			}
		} catch (Exception e) {
			vo.setLbjLogMsg("RECORDING_META_ERROR");
			vo.setLbjLogDesc("[ERROR] LIVE 영상 녹화 META 정보 처리중 오류가 발생하였습니다.");
		}
	}
	
	
	
	
	//MCMS 연동시... [녹화 파일 정보 Insert]
	public void insertMcmsLiveVodData(LiveBroadcastVO lbvo) throws Exception {
		//String[] preset = lbvo.getPresetData().split(":");
		//String preset = lbvo.getPresetData();
		//lbvo.setVodWidth(preset[0]);
		//lbvo.setVodHeight(preset[1]);
		vodManageDAOFactory.getDAO().insertMcmsLiveVodData(lbvo);
	}
	
	public void insertTgLiveVod(LiveBroadcastVO lbvo) throws Exception {
		vodManageDAOFactory.getDAO().insertTgLiveVod(lbvo);
	}
		
}
