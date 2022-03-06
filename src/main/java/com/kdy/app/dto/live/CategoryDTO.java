package com.kdy.app.dto.live;

import lombok.Data;

@Data
public class CategoryDTO {

	private String cateName;
	private String lbCategorySeq;
	private String upCateSeq;
	private String regUseYn;
	private String cateComent;
	private String cateLevel;
	
	private String modCateName;
	private String targetCateSeq;
	private String modUseYn;
	private String modComent;
	private String modUpCateSeq;
		
}
