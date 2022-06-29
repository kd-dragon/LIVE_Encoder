package com.kdy.app.service;


import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.kdy.app.bean.NoticeBean;
import com.kdy.app.bean.util.FileDownBean;
import com.kdy.app.bean.util.MultiFileUploadBean;
import com.kdy.app.bean.util.MultiSlashChk;
import com.kdy.app.bean.util.PagingHtmlBean;
import com.kdy.app.bean.util.RandomGUID;
import com.kdy.app.dto.userHome.NoticeDeleteDTO;
import com.kdy.app.dto.userHome.NoticeDetailDTO;
import com.kdy.app.dto.userHome.NoticeFileVO;
import com.kdy.app.dto.userHome.NoticeListDTO;
import com.kdy.app.dto.userHome.NoticeWriteDTO;
import com.kdy.app.dto.util.FileVO;
import com.kdy.app.service.IF.NoticeServiceIF;
import com.kdy.live.dto.LiveSchedMemoryVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeService implements NoticeServiceIF {

	Logger logger = LoggerFactory.getLogger(NoticeService.class);
	
	private final NoticeBean 			noticeBean;
	private final DataSourceTransactionManager transactionManager; //트랜잭션
	private final PagingHtmlBean 		pagingHtmlBean;
	private final MultiFileUploadBean 	multiFileUploadBean;
	private final LiveSchedMemoryVO 	memoryVO;
	
	@Value("${server.block-count}")
	private int blockCount;
	
	@Value("${server.main-notice-max}")
	private int mainNoticeMax;
	
	@Override
	public NoticeListDTO getNoticeList(NoticeListDTO dto) throws Exception {
		
		//페이징 개수
		dto.setBlockCount(blockCount);
		
		//mariaDB
		dto.setStartNo(dto.getBlockCount() * (dto.getCurrentPage() - 1));
		
		//noticeList
		dto.setNoticeList(noticeBean.noticeList(dto));
		
		//tocalCount
		dto.setTotalCount(noticeBean.noticeListCnt(dto));
		
		//totalPage
		int totalPage = dto.getTotalCount() / dto.getBlockCount();
		if(dto.getTotalCount() % dto.getBlockCount() != 0) { totalPage++; }
		if(totalPage == 0){ totalPage++; }
		dto.setTotalPage(totalPage);
		
		//페이징
		dto.setPagingHtml(pagingHtmlBean.Paging(dto.getCurrentPage(), dto.getTotalCount(), dto.getBlockCount()));
		
		return dto;
		
	}

	@Override
	public String noticeWrite(NoticeWriteDTO dto) throws Exception {
		
		TransactionStatus txStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());

		try {
			
			//메인 노출 공지사항 갯수 확인 (3개까지만 가능)
			if(dto.getMainNoticeYn().equals("Y")) {
				int mainNoticeCnt = noticeBean.chkMainNoticeCnt(dto.getNoticeSeq());
				if( mainNoticeCnt >= mainNoticeMax ) {
					transactionManager.rollback(txStatus);
					return "MAIN_NOTICE_CNT_OVER";
				}
			}
			
			//공지사항 저장
			noticeBean.noticeWrite(dto);
			
			if(dto.getUploadFile() != null) {
				List<FileVO> fileList = multiFileUploadBean.service(dto.getUploadFile(), "notice/file");
				//File 정보 DB 저장
				noticeBean.noticeFileInsert(dto.getNoticeSeq(), fileList);
			}
			
			transactionManager.commit(txStatus);
			
			return "SUCCESS";
			
		}catch (Exception e) {
			transactionManager.rollback(txStatus);
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public NoticeDetailDTO noticeDetail(NoticeDetailDTO dto) throws Exception {
		
		//조회수 증가
		noticeBean.noticeViewCntUp(dto.getNoticeSeq());
		
		//공지사항 상세보기
		dto.setNoticeVo(noticeBean.noticeDetail(dto.getNoticeSeq()));
		
		//공지사항 첨부파일
		dto.setFileList(noticeBean.noticeFileList(dto.getNoticeSeq()));
		
		return dto;
	}

	@Override
	public void noticeFileDown(NoticeFileVO fileVo, HttpServletResponse res) throws Exception {
		String serverFilePath = fileVo.getFilePath() + File.separator + fileVo.getFileNameServer();
		FileDownBean.fileDownload(serverFilePath, fileVo.getFileName(), res);
	}

	@Override
	public boolean noticeDelete(NoticeDeleteDTO dto) throws Exception {
		return noticeBean.noticeDelete(dto);
	}

	@Override
	public String noticeModify(NoticeWriteDTO dto) throws Exception {
		TransactionStatus txStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());

		try {
			
			//메인 노출 공지사항 갯수 확인 (3개까지만 가능)
			if(dto.getMainNoticeYn().equals("Y")) {
				int mainNoticeCnt = noticeBean.chkMainNoticeCnt(dto.getNoticeSeq());
				if( mainNoticeCnt >= mainNoticeMax ) {
					transactionManager.rollback(txStatus);
					return "MAIN_NOTICE_CNT_OVER";
				}
			}
			
			//공지사항 저장
			noticeBean.noticeModify(dto);
			
			if(dto.getDelFileSeqs() != null && dto.getDelFileSeqs().size() != 0) {
				noticeBean.deleteUploadFile(dto.getDelFileSeqs());
				
			}
			
			if(dto.getUploadFile() != null) {
				List<FileVO> fileList = multiFileUploadBean.service(dto.getUploadFile(), "notice/file");
				//File 정보 DB 저장
				noticeBean.noticeFileInsert(dto.getNoticeSeq(), fileList);
			}
			
			transactionManager.commit(txStatus);
			
			return "SUCCESS";
			
		}catch (Exception e) {
			transactionManager.rollback(txStatus);
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public List<NoticeFileVO> noticeEditImgUpload(MultipartHttpServletRequest request) throws Exception {
		List<NoticeFileVO> uploadFileList = new ArrayList<NoticeFileVO>();
		
		String uploadFilePath = memoryVO.getEtcFileUploadPath() + "notice/img";
		
		try {
			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
			String 						fieldName	 = "";
			
			Iterator fileIterator        = multiRequest.getFileNames();
			File     uploadFileDirectory = new File(MultiSlashChk.path(uploadFilePath));
			
			//업로드 폴더 없을 시 생성
			if(!uploadFileDirectory.isDirectory()) {
				if(uploadFileDirectory.mkdir()) {
				} else {
				}
			}
			
			NoticeFileVO		fileDto 	 = null;
			RandomGUID			myGUID		 = null;
			File                destFile     = null;
			MultipartFile		originalFile = null;
			
			while (fileIterator.hasNext()) {
				fieldName = (String) fileIterator.next();
				originalFile = multiRequest.getFile(fieldName);
				String originalFileName = new String(originalFile.getOriginalFilename().getBytes("UTF-8"), "UTF-8");
			
				String ext = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
				
				List<String> extArray = new ArrayList<String>();
				extArray.add("gif");
				extArray.add("jpg");
				extArray.add("jpeg");
				extArray.add("png");
				extArray.add("GIF");
				extArray.add("JPG");
				extArray.add("JPEG");
				extArray.add("PNG");
				
				if(extArray.indexOf(ext) == -1) {
					logger.error("File Extension Error");
					return null;
				}
				
				if("".equals(originalFileName)) {
					continue;
				}
				
				myGUID = new RandomGUID();
				
				//파일 확장자
				String fileExt = originalFileName.substring(originalFileName.lastIndexOf('.'));
				
				//서버 이름
				String serverFileName = myGUID.toString() + fileExt;
				
				destFile = new File(MultiSlashChk.path(uploadFilePath + File.separator + serverFileName));
				
				originalFile.transferTo(destFile);
				
				fileDto = new NoticeFileVO();
				
				fileDto.setFilePath(MultiSlashChk.path(uploadFilePath));
				fileDto.setFileName(originalFileName);
				fileDto.setFileNameServer(destFile.getName());
				
				uploadFileList.add(fileDto);
			}
		} catch (Exception ioe) {
			logger.error("Util IO Error", ioe);
		}
		
		return uploadFileList;
	}

	@Override
	public boolean noticeImp(String noticeSeq) throws Exception {
		return noticeBean.noticeImp(noticeSeq);
	}

	@Override
	public boolean noticeImpCalcel(String noticeSeq) throws Exception {
		return noticeBean.noticeImpCalcel(noticeSeq);
	}

}
