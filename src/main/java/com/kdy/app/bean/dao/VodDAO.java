package com.kdy.app.bean.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kdy.app.dto.vod.LiveContentsVO;
import com.kdy.app.dto.vod.LiveVodDTO;
import com.kdy.app.dto.vod.LiveVodVO;
import com.kdy.app.dto.vod.VodListDTO;
import com.kdy.app.dto.vod.VodModifyDTO;
import com.kdy.app.dto.vod.VodVO;
import com.kdy.app.dto.vod.VodWriteDTO;

@Repository
public class VodDAO {

	SqlSessionTemplate sqlSessionTemplate;
	
	@Autowired
	public VodDAO(SqlSessionTemplate sqlSessionTemplate) {
		this.sqlSessionTemplate = sqlSessionTemplate;
	}

	
	public List<LiveVodVO> getLiveVodList(LiveVodDTO dto) throws Exception {
		return sqlSessionTemplate.selectList("app_live.getLiveVodList", dto);
	}

	public int getLiveVodListCount(LiveVodDTO dto) throws Exception{
		return sqlSessionTemplate.selectOne("app_live.getLiveVodListCount", dto);
	}


	public List<LiveContentsVO> getContentsListByCateSeq(String lbCategorySeq) throws Exception {
		return sqlSessionTemplate.selectList("app_live.getContentsListByCateSeq", lbCategorySeq);
	}
	
	
	//VOD 관리-----------------------
	
	public List<VodVO> vodList(VodListDTO dto) throws Exception {
		return sqlSessionTemplate.selectList("app_vod.vodList", dto);
	}
	
	public int vodListCount(VodListDTO dto) throws Exception {
		return sqlSessionTemplate.selectOne("app_vod.vodListCount", dto);
	}


	public VodVO vodDetail(VodModifyDTO dto) {
		return sqlSessionTemplate.selectOne("app_vod.vodDetail", dto);
	}
	
	public int vodWrite(VodWriteDTO dto) throws Exception {
		return sqlSessionTemplate.insert("app_vod.vodWrite", dto);
	}
	
	public int vodJobInsert(VodWriteDTO dto) throws Exception {
		return sqlSessionTemplate.insert("app_vod.vodJobInsert", dto);
	}
	
	public int vodModify(VodVO vo) throws Exception {
		return sqlSessionTemplate.insert("app_vod.vodModify", vo);
	}
	
	public int vodDelete(VodListDTO dto) throws Exception{
		return sqlSessionTemplate.delete("app_vod.vodDelete", dto);
	}
	
	public List<VodVO> vodExcelList(VodListDTO dto) throws Exception{
		return sqlSessionTemplate.selectList("app_vod.vodExcelList", dto);
	}
	
	public VodVO getEncFileInfo(String vodSeq) throws Exception{
		return sqlSessionTemplate.selectOne("app_vod.getEncFileInfo", vodSeq);
	}
	
	public String vodEncodingProgress(String videoSeq) throws Exception{
		return sqlSessionTemplate.selectOne("app_vod.vodEncodingProgress", videoSeq);
	}
	
	public int vodThumbInsert(VodVO vodVO) throws Exception {
		return sqlSessionTemplate.insert("app_vod.vodThumbInsert", vodVO);

	}
	
	public List<VodVO> vodThumbList(VodModifyDTO dto) throws Exception {
		return sqlSessionTemplate.selectList("app_vod.vodThumbList", dto);
	}
	
	public int thumbImgInit(VodVO vo) throws Exception {
		return sqlSessionTemplate.update("app_vod.thumbImgInit", vo);
	}
	
	public int thumbImgSave(VodVO vo) throws Exception {
		return sqlSessionTemplate.update("app_vod.thumbImgSave", vo);
	}
	
	public int thumbDelete(String thumbSeq) throws Exception {
		return sqlSessionTemplate.delete("app_vod.thumbDelete", thumbSeq);
	}
	
	public int thumbAddInsert(VodVO vo) throws Exception {
		return sqlSessionTemplate.insert("app_vod.thumbAddInsert", vo);
	}
}
