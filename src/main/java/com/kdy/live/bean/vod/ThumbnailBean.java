package com.kdy.live.bean.vod;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.kdy.app.bean.util.MultiSlashChk;
import com.kdy.app.bean.util.RandomGUID;
import com.kdy.app.bean.util.ResizeImage;
import com.kdy.app.dto.vod.VodVO;
import com.kdy.live.bean.encoding.EncodeManagerBean;
import com.kdy.live.dao.vod.VodManageDAOFactory;
import com.kdy.live.dto.LiveSchedMemoryVO;
import com.kdy.live.dto.live.LiveBroadcastVO;
import com.kdy.live.dto.vod.ThumbnailVO;

@Component
@RequiredArgsConstructor
public class ThumbnailBean {
	private Logger logger = LoggerFactory.getLogger(getClass());

	private final EncodeManagerBean encodingMngBean;

	private final VttParserBean vttParserBean;

	private final VodManageDAOFactory vodManageDAOFactory;

	private final LiveSchedMemoryVO memoryVO;
	
	public void service(LiveBroadcastVO vo) throws Exception {
		logger.info("service() - Thumbnail work started");
		
		//썸네일 갯수
		int thumbnailCnt = vo.getThumbnailCnt();
		String thumbnailFormat = vo.getThumbnailFormat();
		
		//썸네일 추출
		encodingMngBean.encodeThumbnailSource(vo);
		
		//썸네일 테이블에 다중 INSERT 전 썸네일 List 가공
		Map<String, Object> map = new HashMap<String, Object>();
		List<ThumbnailVO> list = new ArrayList<ThumbnailVO>();
		ThumbnailVO thumbVO = null;
		//String[] preset = vo.getPresetData().split(":");
//		String preset = vo.getPresetData();
		
		for (int i=1; i<thumbnailCnt+1; i++) {
			thumbVO = new ThumbnailVO();
//			thumbVO.setLbSeq(vo.getLbSeq()); 
			thumbVO.setVodSeq(vo.getVodSeq()); //lbSeq 대신 vodSeq 넣기
			thumbVO.setThumbFilePath(vo.getRecordCopyPath());
			thumbVO.setThumbFileName(String.format("%1$s_%2$s_%3$d.%4$s", vo.getRecordCopyName() + "high", "thumb", i, thumbnailFormat));
			//thumbVO.setThumbWidth(preset[0]);
			//thumbVO.setThumbHeight(preset[1]);
			if(i==1) {
				thumbVO.setRepreimageYn("Y");
			} else {
				thumbVO.setRepreimageYn("N");
			}
			thumbVO.setThumbType("A");
			thumbVO.setThumbTime(vo.getThumbnailTime());
			list.add(thumbVO);
		}
		
		map.put("list", list);
		// 썸네일 정보 다중 INSERT 
		vodManageDAOFactory.getDAO().insertVodThumnail(map);
		
		// 프리뷰 파일 설정 파일 (VTT) 생성
		vttParserBean.makeVttFileByFrame(vo);
		
	}
	
	public VodVO multipartThumb(MultipartFile thumbnailFile) {
		
		VodVO vodVO = new VodVO();
		// 공통 서버 파일명
		RandomGUID myGUID = new RandomGUID();
		// 공통 날짜
		String year = new SimpleDateFormat("yyyy").format(new Date());
		String month = new SimpleDateFormat("MM").format(new Date());
		
		String thumbOriginalPath = memoryVO.getEtcFileUploadPath() + "thumb/original/" + year + "/" + month;
		String thumbEncodingPath = memoryVO.getEtcFileUploadPath() + "thumb/encoding/" + year + "/" + month;
		
		try {
		
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
			vodVO.setOriginalFilePath(MultiSlashChk.path(thumbOriginalPath+"/"));
			vodVO.setOriginalFileName(thumbnailFile.getOriginalFilename());
			
			// encoding image file
			ResizeImage.resizePngImages(thumbOriginalFile, thumbEncodingFile, 852, 0);
			
			vodVO.setEncodingFilePath(MultiSlashChk.path(thumbEncodingPath+"/"));
			vodVO.setEncodingFileName(thumbServerFileName);
			
		} catch(Exception e) {
			logger.error("error : \n{}", e);
			return null;
		}
		
		return vodVO;
				
	}
}
