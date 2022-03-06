package com.kdy.app.bean.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class GetSequenceDAO {
	
	@Autowired
	SqlSessionTemplate sqlSessionTemplate;
	
	//년월일
	public String getSeq_yyyymmdd(String seqName) throws Exception {
		return sqlSessionTemplate.selectOne("app_util.getSeq_yyyymmdd", seqName);
	}
	
	//년
	public String getSeq_yyyymm(String seqName) throws Exception {
		return sqlSessionTemplate.selectOne("app_util.getSeq_yyyymm", seqName);
	}
}
