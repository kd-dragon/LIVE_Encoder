package com.kdy.app.dto.vod;

import java.util.List;

import lombok.Data;

@Data
public class VodModifyDTO {
	
	private String vodSeq;
	private String replaceRootPath;
	private int rootUpCategoryCode;
	
	List<VodVO> vodThumbList;
	List<VodVO> vodThumbAList;
	List<VodVO> vodThumbGList;
}
