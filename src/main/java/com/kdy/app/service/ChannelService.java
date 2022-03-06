package com.kdy.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.kdy.app.bean.CategoryBean;
import com.kdy.app.bean.ChannelBean;
import com.kdy.app.bean.util.PagingHtmlBean;
import com.kdy.app.dto.channel.ChannelDTO;
import com.kdy.app.dto.channel.ChannelVO;
import com.kdy.app.dto.live.CategoryVO;
import com.kdy.app.service.IF.ChannelServiceIF;

@Service
public class ChannelService implements ChannelServiceIF{
	
	private ChannelBean channelBean;
	private CategoryBean categoryBean;
	private final PagingHtmlBean pagingHtmlBean;
	
	@Autowired
	public ChannelService(ChannelBean channelBean
						, CategoryBean categoryBean
						, PagingHtmlBean pagingHtmlBean) {
		this.channelBean = channelBean;
		this.categoryBean = categoryBean;
		this.pagingHtmlBean = pagingHtmlBean;
	}

	@Value("${server.root-up-category-code}")
	private int rootUpCategoryCode;
	
	@Value("${server.block-count}")
	private int blockCount;
	
	//채널 리스트
	@Override
	public ChannelDTO channelList(ChannelDTO dto) throws Exception {
		dto.setBlockCount(blockCount);
		//mariaDB
		dto.setStartNum(dto.getBlockCount() * (dto.getCurrentPage() -1));
		//채널 리스트
		dto.setChannelList(channelBean.channelList(dto));
		//리스트 개수
		dto.setTotalCount(channelBean.getChannelListCount(dto));
		//페이징
		dto.setPagingHtml(pagingHtmlBean.Paging(dto.getCurrentPage(), dto.getTotalCount(), dto.getBlockCount()));
		
		return dto;
	}


	//채널 추가
	@Override
	public String addChannel(ChannelVO vo) throws Exception {
		
		if(channelBean.channelNameExistChk(vo) > 0) {
			return "EXIST";
		}
		
		return channelBean.addChannel(vo);
	}

	
	//채널 수정
	@Override
	public String modChannel(ChannelVO vo) throws Exception {
		
		if(channelBean.channelNameExistChk(vo) > 0) {
			return "EXIST";
		}
		
		return channelBean.modChannel(vo);
	}
	
	
	//채널 삭제
	@Override
	public String delChannel(String deleteSeqs) throws Exception {
		String[] deleteSeqArr = deleteSeqs.split(",");
		return channelBean.delChannel(deleteSeqArr);
	}


	@Override
	public List<CategoryVO> categoryList() throws Exception {
		return categoryBean.getCategoryList(rootUpCategoryCode);
	}


}
