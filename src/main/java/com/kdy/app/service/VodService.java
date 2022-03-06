package com.kdy.app.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kdy.app.bean.VodBean;
import com.kdy.app.bean.util.ExcelDownBean;
import com.kdy.app.bean.util.FileCopyUploadBean;
import com.kdy.app.bean.util.FileDownBean;
import com.kdy.app.bean.util.MultiSlashChk;
import com.kdy.app.bean.util.PagingHtmlBean;
import com.kdy.app.dto.live.ResultVO;
import com.kdy.app.dto.vod.LiveContentsVO;
import com.kdy.app.dto.vod.LiveVodDTO;
import com.kdy.app.dto.vod.VodListDTO;
import com.kdy.app.dto.vod.VodModifyDTO;
import com.kdy.app.dto.vod.VodVO;
import com.kdy.app.dto.vod.VodWriteDTO;
import com.kdy.app.service.IF.VodServiceIF;
import com.kdy.live.bean.vod.ThumbnailBean;
import com.kdy.live.dto.LiveSchedMemoryVO;

@Service
public class VodService implements VodServiceIF {

	private final PagingHtmlBean pagingHtmlBean;
	private final VodBean vodBean;
	private final ExcelDownBean excelDownBean;
	private final LiveSchedMemoryVO memoryVO;
	private final FileCopyUploadBean fileCopyBean;
	private final ThumbnailBean thumbBean;
	
	@Autowired
	public VodService(PagingHtmlBean pagingHtmlBean, VodBean vodBean
			, LiveSchedMemoryVO memoryVO, ExcelDownBean excelDownBean
			, FileCopyUploadBean fileCopyBean, ThumbnailBean thumbBean) {
		this.pagingHtmlBean = pagingHtmlBean;
		this.vodBean = vodBean;
		this.memoryVO = memoryVO;
		this.excelDownBean = excelDownBean;
		this.fileCopyBean = fileCopyBean;
		this.thumbBean = thumbBean;
	}

	@Value("${server.block-count}")
	private int blockCount;
	
	@Value("${vod.limit-size}")
	private String vodLimitSize;
	
	@Value("${vod.uploader-port}")
	private String uploaderPort;
	
	@Value("${encoding.isAdaptive}")
	private boolean isAdaptive;
	
	@Value("${encoding.type}")
	private String adaptiveType;
	
	@Value("${server.root-up-category-code}")
	private int rootUpCategoryCode;

	@Override
	public LiveVodDTO liveVodList(LiveVodDTO dto) throws Exception {
		//페이징 setting
		int startNo = (dto.getCurrentPage() - 1) * dto.getBlockCount();
		//int endNo = startNo + dto.getBlockCount();
		int endNo = 5;
		dto.setStartNo(startNo);
		dto.setEndNo(endNo);
		
		//비디오 리스트
		dto.setLiveVodList(vodBean.getLiveVodList(dto));
		
		//비디오 리스트 count
		dto.setTotalCount(vodBean.getLiveVodListCount(dto));
		
		//페이징 html
		dto.setPagingHtml(pagingHtmlBean.Paging(dto.getCurrentPage(), dto.getTotalCount(), dto.getBlockCount()));
		
		return dto;
	}


	@Override
	public List<LiveContentsVO> getContentsListByCateSeq(String lbCategorySeq) throws Exception {
		return vodBean.getContentsListByCateSeq(lbCategorySeq);
	}


	//VOD 관리
	@Override
	public VodListDTO vodList(VodListDTO dto) throws Exception {
		
		if(dto.getBlockCount() == 0) {
			dto.setBlockCount(blockCount);
		}
		
		dto.setStartNo(dto.getBlockCount() * (dto.getCurrentPage() -1));
		
		dto.setReplaceRootPath(memoryVO.getReplaceRootPath());
	
		dto.setRootUpCategoryCode(rootUpCategoryCode);
		
		//VOD 리스트
		dto.setVodList(vodBean.vodList(dto));
		
		//VOD 총 개수
		dto.setTotalCount(vodBean.vodListCount(dto));
		
		//페이징
		dto.setPagingHtml(pagingHtmlBean.Paging(dto.getCurrentPage(), dto.getTotalCount(), dto.getBlockCount()));
		
		//총 페이지 수
		int totalPage = dto.getTotalCount() / dto.getBlockCount();
		if(dto.getTotalCount() % dto.getBlockCount() != 0) { totalPage++; }
		if(totalPage == 0) { totalPage++; }
		dto.setTotalPage(totalPage);
		
		return dto;
	}


	//VOD 상세보기
	@Override
	public VodVO vodDetail(String vodSeq) throws Exception {
		
		VodModifyDTO dto = new VodModifyDTO();
		dto.setVodSeq(vodSeq);
		dto.setReplaceRootPath(memoryVO.getReplaceRootPath());
		dto.setRootUpCategoryCode(rootUpCategoryCode);
		
		VodVO vo = vodBean.vodDetail(dto);
		vo.setVodStreamUrl(memoryVO.getVodStreamingUri());
//		dto.setVodThumbList(vodBean.vodThumbList(dto));
		
		//적응형 여부
		vo.setAdaptiveYn(isAdaptive?"Y":"N");
		vo.setAdaptiveType(adaptiveType);
		
		return vo;
	}


	//VOD 등록 화면
	@Override
	public VodWriteDTO vodWriteForm() throws Exception {
		
		VodWriteDTO dto = new VodWriteDTO();
		dto.setVodLimitSize(vodLimitSize);
		dto.setUploaderPort(uploaderPort);
		
		return dto;
	}

	
	//VOD 등록
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
	public ResultVO vodWrite(VodWriteDTO dto, MultipartFile thumbnailFile) throws Exception {
		
		ResultVO rsltVO = new ResultVO();
		
		// File Validation
		if(!checkMultipartFiles(rsltVO, thumbnailFile)) {
			return rsltVO;
		}
					
		// 날짜
		String year = new SimpleDateFormat("yyyy").format(new Date());
		String month = new SimpleDateFormat("MM").format(new Date());
		String originalPath = memoryVO.getVodOriginalFilePath()+ year + "/" + month +"/";
		
		try {

			dto.setOriginalFilePath(MultiSlashChk.path(originalPath));
		
			//file copy (temp -> original :: NAS로 이동)
			fileCopyBean.service(memoryVO.getVodTempFilePath(), dto.getOriginalFilePath(), dto.getOriginalFileServer());
			
			
			//vod insert
			vodBean.vodWrite(dto);
			
			//vod_job insert
			vodBean.vodJobInsert(dto);
			
			// 썸네일 업로드 
			if(thumbnailFile.getOriginalFilename() != null && !thumbnailFile.getOriginalFilename().equals("")) {
				VodVO vodVO = thumbBean.multipartThumb(thumbnailFile);
				if(vodVO != null) {
					try {
						vodVO.setVodSeq(dto.getVodSeq());
						vodBean.vodThumbInsert(vodVO);
					} catch (Exception e) {
						rsltVO.setRslt(false);
						rsltVO.setRsltMsg("Thumbnail DB Insert Error");
						rsltVO.setRsltDesc("썸네일 DB INSERT 중에 오류가 발생하였습니다.");
					}
				} else {
					rsltVO.setRslt(false);
					rsltVO.setRsltMsg("Thumbnail Encoding Error");
					rsltVO.setRsltDesc("썸네일 인코딩 중에 오류가 발생하였습니다.");
				}
			}
		} catch (Exception e) {
			rsltVO.setRslt(false);
			rsltVO.setRsltMsg("Insert VOD Fail");
			rsltVO.setRsltDesc("VOD 등록중에 오류가 발생하였습니다.");
			
		} finally {
			//temp(local)파일 삭제하기
			File tempFile = new File(MultiSlashChk.path(memoryVO.getVodTempFilePath() + dto.getOriginalFileServer()));
			if(tempFile.isFile()) {
				tempFile.delete();
			}
		}
		
		rsltVO.setRslt(true);
		rsltVO.setRsltMsg("SUCCESS");
		rsltVO.setRsltDesc("VOD 등록 완료");
		
		return rsltVO;
	}

	//VOD 삭제
	@Override
	public String vodDelete(VodListDTO dto) throws Exception {
		
		if(dto.getVodSeqs().length > 0) {
			if(vodBean.vodDelete(dto) > 0) {
				return "SUCCESS";
			}
		}
		return "FAIL";
	}


	//VOD 수정
	@Override
	public String vodModify(VodVO vo) throws Exception {
		if(vodBean.vodModify(vo) > 0) {
			return "SUCCESS";
		}
		return "FAIL";
	}


	@Override
	public void vodExcelDown(VodListDTO dto, HttpServletRequest req, HttpServletResponse res) throws Exception {
		excelDownBean.vodExcelDown(vodBean.vodExcelList(dto), req, res);
	}


	@Override
	public void vodVideoDown(String vodSeq, HttpServletRequest req, HttpServletResponse res) throws Exception {
		VodVO vo = vodBean.getEncFileInfo(vodSeq);
		FileDownBean.fileDownload(vo.getEncodingFilePath()+vo.getEncodingFileName(), vo.getOriginalFileName(), res);
	}


	@Override
	public String vodEncodingProgress(String videoSeq) throws Exception {
		return vodBean.vodEncodingProgress(videoSeq);
	}
	
	
	private boolean checkMultipartFiles(ResultVO rsltVO, MultipartFile thumbnailFile) {
		
		boolean retval = true;
		
		if(!thumbnailFile.isEmpty()) {
			if(thumbnailFile.getSize() > 20000000)  {
				rsltVO.setRslt(false);
				rsltVO.setRsltMsg("Exceed Max File Size [thumbnail]");
				rsltVO.setRsltDesc("썸네일 최대 업로드 크기 (20MB)를 초과하여 등록이 취소되었습니다.");
				retval = false;
			} 
			
			if(!checkThumbnailExtension(thumbnailFile.getOriginalFilename())) {
				rsltVO.setRslt(false);
				rsltVO.setRsltMsg("Invalid File Extension");
				rsltVO.setRsltDesc("썸네일 파일 확장자가 유효하지 않습니다. \n이미지 파일만 업로드 가능합니다.");
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
	public VodModifyDTO thumbList(VodModifyDTO dto) throws Exception {
		dto.setReplaceRootPath(memoryVO.getReplaceRootPath());
		dto.setVodThumbList(vodBean.vodThumbList(dto));
		List<VodVO> thumbAList = new ArrayList<VodVO>();
		List<VodVO> thumbGList = new ArrayList<VodVO>();
		for(int i = 0; i < dto.getVodThumbList().size(); i++) {
			VodVO vo = dto.getVodThumbList().get(i);
			if(vo.getThumbType().equalsIgnoreCase("A")) {
				thumbAList.add(vo);
				dto.setVodThumbAList(thumbAList);
			} else if(vo.getThumbType().equalsIgnoreCase("G")) {
				thumbGList.add(vo);
				dto.setVodThumbGList(thumbGList);
			}
		}
		return dto;
	}
	
	@Override
	public String thumbImgSave(VodVO vo) throws Exception {
		vodBean.thumbImgInit(vo);
		
		if(vodBean.thumbImgSave(vo) > 0) {
			return "SUCCESS";
		} 
		return "FAIL";
	}
	
	@Override
	public String thumbDelete(String thumbSeq) throws Exception {
			if(vodBean.thumbDelete(thumbSeq) > 0) {
				return "SUCCESS";
			}
		return "FAIL";
	}
	
	@Override
	public ResultVO thumbAddInsert(VodVO vo, MultipartFile thumbnailFile) throws Exception {
		ResultVO rsltVO = new ResultVO();
		// File Validation
		if(!checkMultipartFiles(rsltVO, thumbnailFile)) {
			return rsltVO;
		}
		// 썸네일 업로드 
		VodVO thumbVO = thumbBean.multipartThumb(thumbnailFile);
		if(thumbVO != null) {
			try {
				thumbVO.setRegUserId(vo.getRegUserId());
				thumbVO.setVodSeq(vo.getVodSeq());
				vodBean.thumbAddInsert(thumbVO);
			} catch (Exception e) {
				rsltVO.setRslt(false);
				rsltVO.setRsltMsg("Thumbnail DB Insert Error");
				rsltVO.setRsltDesc("썸네일 DB INSERT 중에 오류가 발생하였습니다.");
			}
		} else {
			rsltVO.setRslt(false);
			rsltVO.setRsltMsg("Thumbnail Encoding Error");
			rsltVO.setRsltDesc("썸네일 인코딩 중에 오류가 발생하였습니다.");
		}
	
		rsltVO.setRslt(true);
		rsltVO.setRsltMsg("SUCCESS");
		rsltVO.setRsltDesc("이미지가 추가되었습니다.");
	
	return rsltVO;
	}
}
