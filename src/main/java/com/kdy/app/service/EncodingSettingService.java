package com.kdy.app.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kdy.app.bean.EncodingSettingBean;
import com.kdy.app.bean.util.MultiSlashChk;
import com.kdy.app.bean.util.RandomGUID;
import com.kdy.app.dto.watermark.WatermarkVO;
import com.kdy.app.service.IF.EncodingSettingServiceIF;
import com.kdy.live.dto.LiveSchedMemoryVO;
import com.kdy.live.dto.netty.NettyVO;
import com.kdy.live.dto.system.SystemConfigVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EncodingSettingService implements EncodingSettingServiceIF {

	private final EncodingSettingBean encodingSettingBean;
	private final LiveSchedMemoryVO systemVO;
	
	//워터마크 불러오기
	@Override
	public WatermarkVO getWatermark(WatermarkVO vo) throws Exception {
		vo.setReplaceRootPath(systemVO.getReplaceRootPath());
		return encodingSettingBean.getWatermark(vo);
	}

	//워터마크 등록 및 수정
	@Override
	public String saveWatermark(WatermarkVO vo, MultipartFile watermarkFile) throws Exception {
		RandomGUID  guid = new RandomGUID();
		
		String year = new SimpleDateFormat("yyyy").format(new Date());
		String month = new SimpleDateFormat("MM").format(new Date());
		String result = "FAIL";
		
		vo.setReplaceRootPath(systemVO.getReplaceRootPath());
		WatermarkVO chkVo = encodingSettingBean.getWatermark(vo);
		if(chkVo == null) {
			if (!watermarkFile.isEmpty()) {
				String watermarkPath = systemVO.getEtcFileUploadPath() + "watermark/" + year + "/" + month;
				File watermarkDir = new File(MultiSlashChk.path(watermarkPath));

				if (!watermarkDir.isDirectory()) {
					watermarkDir.mkdirs();
				}

				String watermarkExt = watermarkFile.getOriginalFilename()
						.substring(watermarkFile.getOriginalFilename().lastIndexOf("."));
				String serverFileName = guid.toString() + watermarkExt;
				watermarkFile.transferTo(new File(MultiSlashChk.path(watermarkPath + "/" + serverFileName)));

				vo.setImgFilePath(MultiSlashChk.path(watermarkPath));
				vo.setImgFileName(watermarkFile.getOriginalFilename());
				vo.setImgServerFileName(serverFileName);
				encodingSettingBean.registerWatermark(vo);
				
				result = "SUCCESS";
			}
		} else {
			WatermarkVO findFile = encodingSettingBean.getWatermarkFile();
			//파일 삭제
			File file = new File(MultiSlashChk.path(findFile.getImgFilePath() + File.separator + findFile.getImgServerFileName()));
			if(file.isFile()) { file.delete(); }
			
			String watermarkPath = systemVO.getEtcFileUploadPath() + "watermark/" + year + "/" + month;
			File watermarkDir = new File(MultiSlashChk.path(watermarkPath));

			if (!watermarkDir.isDirectory()) {
				watermarkDir.mkdirs();
			}

			String watermarkExt = watermarkFile.getOriginalFilename()
					.substring(watermarkFile.getOriginalFilename().lastIndexOf("."));
			String serverFileName = guid.toString() + watermarkExt;
			watermarkFile.transferTo(new File(MultiSlashChk.path(watermarkPath + "/" + serverFileName)));

			vo.setImgFilePath(MultiSlashChk.path(watermarkPath));
			vo.setImgFileName(watermarkFile.getOriginalFilename());
			vo.setImgServerFileName(serverFileName);
			encodingSettingBean.modifyWatermark(vo);
			result = "SUCCESS";
		}
		return result;
	}

	//워터마크 수정 > 사용여부
	@Override
	public String modifyStatus(WatermarkVO vo) throws Exception {
		return encodingSettingBean.modifyStatus(vo);
	}

	//미디어 설정 불러오기
	@Override
	public SystemConfigVO getMediaConfig(SystemConfigVO vo) throws Exception {
		return encodingSettingBean.getSystemConfig(vo);
	}

	//미디어 설정 업데이트
	@Override
	public String modifyMediaConfig(SystemConfigVO vo) throws Exception {
		return encodingSettingBean.modifyMediaConfig(vo);
	}

}
