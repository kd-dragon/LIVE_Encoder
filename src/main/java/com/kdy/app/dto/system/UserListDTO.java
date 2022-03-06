package com.kdy.app.dto.system;

import java.util.List;

import com.kdy.app.dto.live.CategoryVO;

import lombok.Data;

@Data
public class UserListDTO {

	private List<UserVO> userList;
	
	private int totalCount;
	private int blockCount;
	private int currentPage=1;
	private int startNum;
	private int totalPage;
	private String pagingHtml;

	private String searchText="";
	private String searchTextType="";
	
	private List<String> delUserList;
	
	private List<AuthVO> authList;
	private List<CategoryVO> categoryList;
}
