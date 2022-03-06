package com.kdy.app.service.IF;

import java.util.List;

import com.kdy.app.dto.live.CategoryDTO;
import com.kdy.app.dto.live.CategoryVO;

public interface CategoryServiceIF {
	
	//카테고리 리스트
	public List<CategoryVO> getCategoryList() throws Exception;
	
	//카테고리 등록
	public String insertCategory(CategoryDTO dto) throws Exception;
	
	//카테고리 정보 수정
	public String updateCategory(CategoryDTO dto) throws Exception;
	
	//카테고리 정보 삭제
	public String deleteCategory(CategoryDTO dto) throws Exception;
	
	//리스트 트리형식으로 표출
	public String getCategoryTreeHtml(List<CategoryVO> categoryList) throws Exception;
	
	//카테고리 상세 정보
	public String getDetailCategoryTreeHtml(List<CategoryVO> categoryList) throws Exception;
}
