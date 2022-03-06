package com.kdy.app.bean.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kdy.app.dto.userHome.NoticeDeleteDTO;
import com.kdy.app.dto.userHome.NoticeFileVO;
import com.kdy.app.dto.userHome.NoticeListDTO;
import com.kdy.app.dto.userHome.NoticeVO;
import com.kdy.app.dto.userHome.NoticeWriteDTO;

@Repository
public class NoticeDAO {

	@Autowired
	SqlSessionTemplate sqlSessionTemplate;

	public List<NoticeVO> noticeList(NoticeListDTO dto) throws Exception {
		return sqlSessionTemplate.selectList("app_notice.noticeList", dto);
	}

	public int noticeListCnt(NoticeListDTO dto) throws Exception {
		return sqlSessionTemplate.selectOne("app_notice.noticeListCnt", dto);
	}

	public int noticeWrite(NoticeWriteDTO dto) throws Exception {
		return sqlSessionTemplate.insert("app_notice.noticeWrite", dto);
	}

	public int noticeFileInsert(NoticeFileVO dto) throws Exception {
		return sqlSessionTemplate.insert("app_notice.noticeFileInsert", dto);
	}

	public int chkMainNoticeCnt(String noticeSeq) throws Exception {
		return sqlSessionTemplate.selectOne("app_notice.chkMainNoticeCnt", noticeSeq);
	}

	public NoticeVO noticeDetail(String noticeSeq) throws Exception {
		return sqlSessionTemplate.selectOne("app_notice.noticeDetail", noticeSeq);
	}

	public List<NoticeFileVO> noticeFileList(String noticeSeq) throws Exception {
		return sqlSessionTemplate.selectList("app_notice.noticeFileList",noticeSeq);
	}

	public int noticeViewCntUp(String noticeSeq) throws Exception {
		return sqlSessionTemplate.update("app_notice.noticeViewCntUp", noticeSeq);
	}

	public int noticeDelete(NoticeDeleteDTO dto) throws Exception {
		return sqlSessionTemplate.update("app_notice.noticeDelete", dto);
	}

	public int noticeModify(NoticeWriteDTO dto) throws Exception {
		return sqlSessionTemplate.update("app_notice.noticeModify", dto);
	}

	public int deleteUploadFile(List<String> delFileSeq) throws Exception {
		return sqlSessionTemplate.delete("app_notice.deleteUploadFile", delFileSeq);
	}

	public int noticeImp(String noticeSeq) throws Exception {
		return sqlSessionTemplate.update("app_notice.noticeImp", noticeSeq);
	}

	public int noticeImpCalcel(String noticeSeq) throws Exception {
		return sqlSessionTemplate.update("app_notice.noticeImpCalcel", noticeSeq);
	}

}
