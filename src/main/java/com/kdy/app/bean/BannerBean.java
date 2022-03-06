package com.kdy.app.bean;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.kdy.app.bean.dao.BannerDAO;
import com.kdy.app.dto.userHome.BannerDTO;
import com.kdy.app.dto.userHome.BannerVO;

@Component
public class BannerBean {

	private final Logger logger = LoggerFactory.getLogger(BannerBean.class);
	
	private final BannerDAO dao;
	
	private final DataSourceTransactionManager transactionManager;
	
	@Autowired
	public BannerBean(BannerDAO dao
					, DataSourceTransactionManager transactionManager) {
		this.dao = dao;
		this.transactionManager = transactionManager;
	}
	
	public List<BannerVO> getBannerList() throws Exception {
		return dao.getBannerList();
	}
	
	public Boolean modifyBanner(BannerDTO dto) throws Exception {
		TransactionStatus txStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
		Boolean rtnVal = true;
		
		try {
			
			//배너 1~5 삭제
			if(dto.getBanner1().getBannerSeq() != null && !dto.getBanner1().getBannerSeq().equals("") 
					&& dto.getBanner1().getDelYn() != null && dto.getBanner1().getDelYn().equalsIgnoreCase("Y")) {
				dto.getBanner1().setDelUserId(dto.getUserId());
				dao.deleteBanner(dto.getBanner1());
			}
			
			if(dto.getBanner2().getBannerSeq() != null && !dto.getBanner2().getBannerSeq().equals("") 
					&& dto.getBanner2().getDelYn() != null && dto.getBanner2().getDelYn().equalsIgnoreCase("Y")) {
				dto.getBanner2().setDelUserId(dto.getUserId());
				dao.deleteBanner(dto.getBanner2());
			}
			
			if(dto.getBanner3().getBannerSeq() != null && !dto.getBanner3().getBannerSeq().equals("") 
					&& dto.getBanner3().getDelYn() != null && dto.getBanner3().getDelYn().equalsIgnoreCase("Y")) {
				dto.getBanner3().setDelUserId(dto.getUserId());
				dao.deleteBanner(dto.getBanner3());
			}
			
			if(dto.getBanner4().getBannerSeq() != null && !dto.getBanner4().getBannerSeq().equals("") 
					&& dto.getBanner4().getDelYn() != null && dto.getBanner4().getDelYn().equalsIgnoreCase("Y")) {
				dto.getBanner4().setDelUserId(dto.getUserId());
				dao.deleteBanner(dto.getBanner4());
			}
			
			if(dto.getBanner5().getBannerSeq() != null && !dto.getBanner5().getBannerSeq().equals("") 
					&& dto.getBanner5().getDelYn() != null && dto.getBanner5().getDelYn().equalsIgnoreCase("Y")) {
				dto.getBanner5().setDelUserId(dto.getUserId());
				dao.deleteBanner(dto.getBanner5());
			}
			
			//배너 1~5 등록 및 수정
			if(dto.getBanner1().getBannerImgFileNameServer() != null) {
				dto.getBanner1().setRegUserId(dto.getUserId());
				dao.insertBanner(dto.getBanner1());
			} else if(dto.getBanner1().getBannerSeq() != null && !dto.getBanner1().getBannerSeq().equals("")){
				dto.getBanner1().setModUserId(dto.getUserId());
				dao.updateBanner(dto.getBanner1());
			}
			
			if(dto.getBanner2().getBannerImgFileNameServer() != null) {
				dto.getBanner2().setRegUserId(dto.getUserId());
				dao.insertBanner(dto.getBanner2());
			} else if(dto.getBanner2().getBannerSeq() != null && !dto.getBanner2().getBannerSeq().equals("")){
				dto.getBanner2().setModUserId(dto.getUserId());
				dao.updateBanner(dto.getBanner2());
			}
			
			if(dto.getBanner3().getBannerImgFileNameServer() != null) {
				dto.getBanner3().setRegUserId(dto.getUserId());
				dao.insertBanner(dto.getBanner3());
			} else if(dto.getBanner3().getBannerSeq() != null && !dto.getBanner3().getBannerSeq().equals("")){
				dto.getBanner3().setModUserId(dto.getUserId());
				dao.updateBanner(dto.getBanner3());
			}
			
			if(dto.getBanner4().getBannerImgFileNameServer() != null) {
				dto.getBanner4().setRegUserId(dto.getUserId());
				dao.insertBanner(dto.getBanner4());
			} else if(dto.getBanner4().getBannerSeq() != null && !dto.getBanner4().getBannerSeq().equals("")){
				dto.getBanner4().setModUserId(dto.getUserId());
				dao.updateBanner(dto.getBanner4());
			}
			
			if(dto.getBanner5().getBannerImgFileNameServer() != null) {
				dto.getBanner5().setRegUserId(dto.getUserId());
				dao.insertBanner(dto.getBanner5());
			} else if(dto.getBanner5().getBannerSeq() != null && !dto.getBanner5().getBannerSeq().equals("")){
				dto.getBanner5().setModUserId(dto.getUserId());
				dao.updateBanner(dto.getBanner5());
			}
			
			transactionManager.commit(txStatus);
			
		} catch (Exception e) {
			rtnVal = false;
			transactionManager.rollback(txStatus);
			throw new Exception(e.getMessage());
		}
		
		return rtnVal;
	}
}
