package com.kdy.app.bean;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kdy.app.bean.dao.VodDAO;
import com.kdy.app.dto.vod.LiveContentsVO;
import com.kdy.app.dto.vod.LiveVodDTO;
import com.kdy.app.dto.vod.LiveVodVO;
import com.kdy.app.dto.vod.VodListDTO;
import com.kdy.app.dto.vod.VodModifyDTO;
import com.kdy.app.dto.vod.VodVO;
import com.kdy.app.dto.vod.VodWriteDTO;

@Component
public class VodBean {

	private VodDAO vodDao;
	
	@Autowired
	public VodBean(VodDAO vodDao) {
		this.vodDao = vodDao;
	}

	
	public List<LiveVodVO> getLiveVodList(LiveVodDTO dto) throws Exception {
		return vodDao.getLiveVodList(dto);
	}

	public int getLiveVodListCount(LiveVodDTO dto) throws Exception {
		return vodDao.getLiveVodListCount(dto);
	}


	public List<LiveContentsVO> getContentsListByCateSeq(String lbCategorySeq) throws Exception {
		return vodDao.getContentsListByCateSeq(lbCategorySeq);
	}
	
	
	// VOD 관리-----------
	
	public List<VodVO> vodList(VodListDTO dto) throws Exception{
		return vodDao.vodList(dto);
	}
	
	public int vodListCount(VodListDTO dto) throws Exception{
		return vodDao.vodListCount(dto);
	}
	
	public VodVO vodDetail(VodModifyDTO dto) throws Exception{
		return vodDao.vodDetail(dto);
	}
	
	public int vodWrite(VodWriteDTO dto) throws Exception {
		return vodDao.vodWrite(dto);
	}
	
	public int vodJobInsert(VodWriteDTO dto) throws Exception {
		return vodDao.vodJobInsert(dto);
	}
	
	public int vodModify(VodVO vo) throws Exception {
		return vodDao.vodModify(vo);
	}
	
	public int vodDelete(VodListDTO dto) throws Exception {
		return vodDao.vodDelete(dto);
	}
	
	public List<VodVO> vodExcelList(VodListDTO dto) throws Exception{
		return vodDao.vodExcelList(dto);
	}
	
	public VodVO getEncFileInfo(String vodSeq) throws Exception{
		return vodDao.getEncFileInfo(vodSeq);
	}
	
	public String vodEncodingProgress(String videoSeq) throws Exception{
		return vodDao.vodEncodingProgress(videoSeq);
	}
	
	public int vodThumbInsert(VodVO vodVO) throws Exception {
		return vodDao.vodThumbInsert(vodVO);
	}

	public List<VodVO> vodThumbList(VodModifyDTO dto) throws Exception {
		return vodDao.vodThumbList(dto);
	}
	
	public int thumbImgInit(VodVO vo) throws Exception {
		return vodDao.thumbImgInit(vo);
	}
	
	public int thumbImgSave(VodVO vo) throws Exception {
		return vodDao.thumbImgSave(vo);
	}
	
	public int thumbDelete(String thumbSeq) throws Exception {
		return vodDao.thumbDelete(thumbSeq);
	}
	
	public int thumbAddInsert(VodVO vo) throws Exception {
		return vodDao.thumbAddInsert(vo);
	}
}
