package com.kdy.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.kdy.app.bean.CategoryBean;
import com.kdy.app.dto.live.CategoryDTO;
import com.kdy.app.dto.live.CategoryVO;
import com.kdy.app.service.IF.CategoryServiceIF;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService implements CategoryServiceIF{
	
	private final CategoryBean bean;
	
	@Value("${server.root-up-category-code}")
	private int rootUpCategoryCode;

	@Override
	public List<CategoryVO> getCategoryList() throws Exception {
		return bean.getCategoryList(rootUpCategoryCode);
	}

	@Override
	public String insertCategory(CategoryDTO dto) throws Exception {
		
		if(bean.categoryNameExistChk(dto) > 0) {
			return "EXIST";
		}
		
		return bean.insertCategory(dto);
	}
	
	@Override
	public String updateCategory(CategoryDTO dto) throws Exception {
		
		if(bean.categoryNameExistChk(dto) > 0) {
			return "EXIST";
		}
		dto.setUpCateSeq(rootUpCategoryCode+"");
		
		return bean.updateCategory(dto);
	}
	
	@Override
	public String deleteCategory(CategoryDTO dto) throws Exception {
		return bean.deleteCategory(dto);
	}
	
	@Override
	public String getCategoryTreeHtml(List<CategoryVO> categoryList) throws Exception {
		String upSeq = "0";
		String upLevel = "0";
		
		StringBuilder cateHtml = new StringBuilder();
		
		for(CategoryVO v : categoryList) {
			
			if(v.getLvl().equals("1")){
				if(!upSeq.equals(v.getUpCategorySeq())){
					if(upLevel.equals("2")){
						cateHtml.append("</li></ul></li>");
					}else if(upLevel.equals("3")){
						cateHtml.append("</ul></li></ul></li>");
					}else{
						cateHtml.append("</li>");
					}
				}else{
					if(upLevel.equals("1")){
						cateHtml.append("</li>");
					}
				}
				
				cateHtml.append("<li class='main-cate-group-item'><span id='"+v.getCategorySeq()
							+"' cateName='"+v.getCategoryName()+"' fullCategoryName='"+v.getFullCategoryName()+"' cateLevel='"+v.getCategoryLevel()
							+"' useYn='"+v.getUseYn()+"' coment='"+v.getComent()+"'>"+v.getCategoryName()+"</span>");
			}else if(v.getLvl().equals("2")){
				if(upLevel.equals("3")){
					cateHtml.append("</ul></li>");
				}else if(upLevel.equals("1")){
					cateHtml.append("<ul class='sub1-cate-group'>");
				}else{
					cateHtml.append("</li>");
				}
				
				cateHtml.append("<li class='sub1-cate-group-item'><span id='"+v.getCategorySeq()
							+"' cateName='"+v.getCategoryName()+"' fullCategoryName='"+v.getFullCategoryName()+"' cateLevel='"+v.getCategoryLevel()
							+"' useYn='"+v.getUseYn()+"' coment='"+v.getComent()+"'>"+v.getCategoryName()+"</span>");
				
			}else if(v.getLvl().equals("3")){
				
				if(upLevel.equals("4")){
					cateHtml.append("</ul></li>");
				} else if(upLevel.equals("2")){
					cateHtml.append("<ul class='sub2-cate-group' style='display:none;'>");
				} else{
					cateHtml.append("</li>");
				}
				
				cateHtml.append("<li class='sub2-cate-group-item'><span id='"+v.getCategorySeq()
							+"' cateName='"+v.getCategoryName()+"' fullCategoryName='"+v.getFullCategoryName()+"' cateLevel='"+v.getCategoryLevel()
							+"' useYn='"+v.getUseYn()+"' coment='"+v.getComent()+"'>"+v.getCategoryName()+"</span>");
				
			}else if(v.getLvl().equals("4")) {
				if(upLevel.equals("3")){
					cateHtml.append("<ul class='sub3-cate-group' style='display:none;'>");
				}else{
					cateHtml.append("</li>");
				}
				
				cateHtml.append("<li class='sub3-cate-group-item'><span id='"+v.getCategorySeq()+"' onclick='"+"modCategoryFrom(this)"
						+"' cateName='"+v.getCategoryName()+"' fullCategoryName='"+v.getFullCategoryName()+"' cateLevel='"+v.getCategoryLevel()
						+"' useYn='"+v.getUseYn()+"' coment='"+v.getComent()+"'>"+v.getCategoryName()+"</span></li>");
				
			}
			upSeq = v.getUpCategorySeq();
			upLevel = v.getLvl();
		}
		
		return cateHtml.toString();
	}

	@Override
	public String getDetailCategoryTreeHtml(List<CategoryVO> categoryList) throws Exception {
		String upSeq = "0";
		String upLevel = "0";
		
		StringBuilder cateHtml = new StringBuilder();
		
		for(CategoryVO v : categoryList) {
			
			if(v.getLvl().equals("1")){
				if(!upSeq.equals(v.getUpCategorySeq())){
					if(upLevel.equals("2")){
						cateHtml.append("</li></ul></li>");
					}else if(upLevel.equals("3")){
						cateHtml.append("</ul></li></ul></li>");
					}else{
						cateHtml.append("</li>");
					}
				}else{
					if(upLevel.equals("1")){
						cateHtml.append("</li>");
					}
				}
				
				cateHtml.append("<li class='main-cate-group-item'><span id='"+v.getCategorySeq()+"' onclick='"+"modCategoryFrom(this)"
							+"' cateName='"+v.getCategoryName()+"' fullCategoryName='"+v.getFullCategoryName()+"' cateLevel='"+v.getCategoryLevel()
							+"' useYn='"+v.getUseYn()+"' coment='"+v.getComent()+"'>"+v.getCategoryName()+"</span>");
				
			}else if(v.getLvl().equals("2")){
				if(upLevel.equals("3")){
					cateHtml.append("</ul></li>");
				}else if(upLevel.equals("1")){
					cateHtml.append("<ul class='sub1-cate-group'>");
				}else{
					cateHtml.append("</li>");
				}
				
				cateHtml.append("<li class='sub1-cate-group-item'><span id='"+v.getCategorySeq()+"' onclick='"+"modCategoryFrom(this)"
							+"' cateName='"+v.getCategoryName()+"' fullCategoryName='"+v.getFullCategoryName()+"' cateLevel='"+v.getCategoryLevel()
							+"' useYn='"+v.getUseYn()+"' coment='"+v.getComent()+"'>"+v.getCategoryName()+"</span>");
				
			}else if(v.getLvl().equals("3")){
				if(upLevel.equals("4")){
					cateHtml.append("</ul></li>");
				}else if(upLevel.equals("2")){
					cateHtml.append("<ul class='sub2-cate-group' style='display:none;'>");
				}else{
					cateHtml.append("</li>");
				}
				
				cateHtml.append("<li class='sub2-cate-group-item'><span id='"+v.getCategorySeq()+"' onclick='"+"modCategoryFrom(this)"
							+"' cateName='"+v.getCategoryName()+"' fullCategoryName='"+v.getFullCategoryName()+"' cateLevel='"+v.getCategoryLevel()
							+"' useYn='"+v.getUseYn()+"' coment='"+v.getComent()+"'>"+v.getCategoryName()+"</span>");
				
			}else if(v.getLvl().equals("4")) {
				if(upLevel.equals("3")){
					cateHtml.append("<ul class='sub3-cate-group' style='display:none;'>");
				}else{
					cateHtml.append("</li>");
				}
				
				cateHtml.append("<li class='sub3-cate-group-item'><span id='"+v.getCategorySeq()+"' onclick='"+"modCategoryFrom(this)"
						+"' cateName='"+v.getCategoryName()+"' fullCategoryName='"+v.getFullCategoryName()+"' cateLevel='"+v.getCategoryLevel()
						+"' useYn='"+v.getUseYn()+"' coment='"+v.getComent()+"'>"+v.getCategoryName()+"</span></li>");
				
			}
			upSeq = v.getUpCategorySeq();
			upLevel = v.getLvl();
		}
		
		return cateHtml.toString();
	}




}
