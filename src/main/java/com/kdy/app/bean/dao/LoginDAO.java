package com.kdy.app.bean.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kdy.app.dto.login.LoginVO;
import com.kdy.app.dto.login.MenuVO;
import com.kdy.app.dto.system.ConnectIpVO;
import com.kdy.app.dto.system.LoginIpLogVO;

@Repository
public class LoginDAO {
     
	@Autowired
	SqlSessionTemplate sqlSessionTemplate;
     
    //DB 값 가져오기
    public LoginVO loginChk(String userId){
        return sqlSessionTemplate.selectOne("login.loginChk", userId );
    }
    
    public void logInsert(LoginIpLogVO vo) throws Exception{
    	sqlSessionTemplate.insert("login.logInsert", vo);
    }
    
    public void loginDateUpdate(String userId) throws Exception{
    	sqlSessionTemplate.update("login.loginDateUpdate",userId);
    }
    
    public int userIpChk(String lscIp) throws Exception {
    	return sqlSessionTemplate.selectOne("login.userIpChk", lscIp);
    }
    
    public int connectIpCnt() throws Exception {
    	return sqlSessionTemplate.selectOne("login.connectIpCnt");
    }
    
    public List<ConnectIpVO> onlyIpList () throws Exception {
    	return sqlSessionTemplate.selectList("login.onlyIpList");
    }
    
    public List<MenuVO> menuList(LoginVO vo) throws Exception{
    	return sqlSessionTemplate.selectList("login.menuList", vo);
    }
    
}