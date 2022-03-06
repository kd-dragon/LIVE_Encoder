package com.kdy.app.bean.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kdy.app.dto.live.CategoryDTO;
import com.kdy.app.dto.live.CategoryVO;

@Repository
public class CategoryDAO {
	
	@Autowired
	SqlSessionTemplate sqlSessionTemplate;
	
	public List<CategoryVO> selectCategoryList(int rootUpCategoryCode) throws Exception {
		return sqlSessionTemplate.selectList("app_category.selectCategoryList", rootUpCategoryCode);
	}
	
	public int insertCategory(CategoryDTO dto) throws Exception {
		return sqlSessionTemplate.insert("app_category.insertCategory", dto);
	}
	
	public int updateCategory(CategoryDTO dto) throws Exception {
		return sqlSessionTemplate.insert("app_category.updateCategory", dto);
	}
	
	public int deleteCategory(CategoryDTO dto) throws Exception {
		return sqlSessionTemplate.insert("app_category.deleteCategory", dto);
	}
	
	public int categoryNameExistChk(CategoryDTO dto) throws Exception {
		return sqlSessionTemplate.selectOne("app_category.categoryNameExistChk", dto);
	}
}
