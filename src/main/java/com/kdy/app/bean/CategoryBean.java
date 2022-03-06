package com.kdy.app.bean;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.kdy.app.bean.dao.CategoryDAO;
import com.kdy.app.dto.live.CategoryDTO;
import com.kdy.app.dto.live.CategoryVO;

@Component
public class CategoryBean {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private CategoryDAO dao;
	
	public CategoryBean(CategoryDAO dao) {
		this.dao = dao;
	}
	
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
