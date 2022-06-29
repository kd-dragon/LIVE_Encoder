package com.kdy.app.bean;

import java.util.List;

import org.springframework.stereotype.Component;

import com.kdy.app.bean.dao.CategoryDAO;
import com.kdy.app.dto.live.CategoryDTO;
import com.kdy.app.dto.live.CategoryVO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CategoryBean {

	private final CategoryDAO dao;
	
	public List<CategoryVO> getCategoryList(int rootUpCategoryCode) throws Exception {
		return dao.selectCategoryList(rootUpCategoryCode);
	}
	
	public String insertCategory(CategoryDTO dto) throws Exception {
		if(dao.insertCategory(dto) > 0) {
			return "SUCCESS";
		}
		return "FAIL";
	}
	
	public String updateCategory(CategoryDTO dto) throws Exception {
		if(dao.updateCategory(dto) > 0) {
			return "SUCCESS";
		}
		return "FAIL";
	}
	
	public String deleteCategory(CategoryDTO dto) throws Exception {
		if(dao.deleteCategory(dto) > 0) {
			return "SUCCESS";
		}
		return "FAIL";
	}
	
	public int categoryNameExistChk(CategoryDTO dto) throws Exception {
		return dao.categoryNameExistChk(dto);
	}
}
