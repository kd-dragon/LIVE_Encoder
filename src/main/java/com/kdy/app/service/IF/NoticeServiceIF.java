package com.kdy.app.service.IF;


import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.kdy.app.dto.userHome.NoticeDeleteDTO;
import com.kdy.app.dto.userHome.NoticeDetailDTO;
import com.kdy.app.dto.userHome.NoticeFileVO;
import com.kdy.app.dto.userHome.NoticeListDTO;
import com.kdy.app.dto.userHome.NoticeWriteDTO;

public interface NoticeServiceIF {

	public Object getNoticeList(NoticeListDTO dto) throws Exception;

	public String noticeWrite(NoticeWriteDTO dto) throws Exception;

	public NoticeDetailDTO noticeDetail(NoticeDetailDTO dto) throws Exception;

	public void noticeFileDown(NoticeFileVO fileVo, HttpServletResponse res) throws Exception;

	public boolean noticeDelete(NoticeDeleteDTO dto) throws Exception;

	public String noticeModify(NoticeWriteDTO dto) throws Exception;

	public List<NoticeFileVO> noticeEditImgUpload(MultipartHttpServletRequest multiRequest) throws Exception;

	public boolean noticeImp(String noticeSeq) throws Exception;

	public boolean noticeImpCalcel(String noticeSeq) throws Exception;
}
