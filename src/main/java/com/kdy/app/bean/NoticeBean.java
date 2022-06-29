package com.kdy.app.bean;

import java.util.List;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;

import com.kdy.app.bean.dao.NoticeDAO;
import com.kdy.app.bean.util.MultiSlashChk;
import com.kdy.app.dto.userHome.NoticeDeleteDTO;
import com.kdy.app.dto.userHome.NoticeFileVO;
import com.kdy.app.dto.userHome.NoticeListDTO;
import com.kdy.app.dto.userHome.NoticeVO;
import com.kdy.app.dto.userHome.NoticeWriteDTO;
import com.kdy.app.dto.util.FileVO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NoticeBean {

	private final NoticeDAO dao;
	
	private final DataSourceTransactionManager transactionManager;
	
	//공지사항 목록
	public List<NoticeVO> noticeList(NoticeListDTO dto) throws Exception {
		return dao.noticeList(dto);
	}

	//공지사항 총 갯수
	public int noticeListCnt(NoticeListDTO dto) throws Exception {
		return dao.noticeListCnt(dto);
	}

	//공지사항 등록
	public boolean noticeWrite(NoticeWriteDTO dto) throws Exception {
		return dao.noticeWrite(dto) > 0;
	}

	public String noticeFileInsert(String noticeSeq, List<FileVO> list) throws Exception{
		NoticeFileVO dto = null;
		int cnt = 0;
		
		for(int i=0; i<list.size(); i++) {
			dto = new NoticeFileVO();
			FileVO vo = list.get(i);
			dto.setFileName(vo.getFileName());
			dto.setFileNameServer(vo.getFileNameServer());
			dto.setFilePath(MultiSlashChk.path(vo.getFilePath()));
			dto.setNoticeSeq(noticeSeq);
			cnt += dao.noticeFileInsert(dto);
		}
		
		if(cnt == list.size()) {
			return "SUCCESS";
		}
		
		return "FAIL";
	}

	public int chkMainNoticeCnt(String noticeSeq) throws Exception {
		return dao.chkMainNoticeCnt(noticeSeq);
	}

	public NoticeVO noticeDetail(String noticeSeq) throws Exception {
		return dao.noticeDetail(noticeSeq);
	}

	public List<NoticeFileVO> noticeFileList(String noticeSeq) throws Exception {
		return dao.noticeFileList(noticeSeq);
	}

	public boolean noticeViewCntUp(String noticeSeq) throws Exception {
		return dao.noticeViewCntUp(noticeSeq) > 0 ;
	}

	public boolean noticeDelete(NoticeDeleteDTO dto) throws Exception {
		return dao.noticeDelete(dto) > 0;
	}

	public boolean noticeModify(NoticeWriteDTO dto) throws Exception{
		return dao.noticeModify(dto) > 0 ;
	}

	public boolean deleteUploadFile(List<String> delFileSeq) throws Exception {
		return dao.deleteUploadFile(delFileSeq) > 0 ;
	}

	public boolean noticeImp(String noticeSeq) throws Exception {
		return dao.noticeImp(noticeSeq) > 0 ;
	}

	public boolean noticeImpCalcel(String noticeSeq) throws Exception {
		return dao.noticeImpCalcel(noticeSeq) > 0 ;
	}

}
